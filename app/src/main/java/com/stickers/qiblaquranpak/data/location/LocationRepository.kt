package com.stickers.qiblaquranpak.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class Place(val lat: Double, val lng: Double, val alt: Double, val city: String, val country: String)

class LocationRepository(private val context: Context) {

    private val fused = LocationServices.getFusedLocationProviderClient(context)

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(lm)
    }

    /** Current fix (last known if fresh enough), or null without permission / when GPS is off / on timeout. */
    @SuppressLint("MissingPermission")
    suspend fun currentPlace(): Place? {
        if (!hasPermission()) return null
        val loc = withTimeoutOrNull(15_000) {
            suspendCancellableCoroutine<android.location.Location?> { cont ->
                val cts = CancellationTokenSource()
                fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                    .addOnSuccessListener { if (cont.isActive) cont.resume(it) }
                    .addOnFailureListener { if (cont.isActive) cont.resume(null) }
                cont.invokeOnCancellation { cts.cancel() }
            }
        } ?: withTimeoutOrNull(3_000) {
            suspendCancellableCoroutine<android.location.Location?> { cont ->
                fused.lastLocation
                    .addOnSuccessListener { if (cont.isActive) cont.resume(it) }
                    .addOnFailureListener { if (cont.isActive) cont.resume(null) }
            }
        } ?: return null
        val (city, country) = reverseGeocode(loc.latitude, loc.longitude)
        return Place(loc.latitude, loc.longitude, loc.altitude, city, country)
    }

    private val offline = OfflineCities(context)

    /** System geocoder first; bundled city list when it is missing, offline or returns nothing. */
    suspend fun reverseGeocode(lat: Double, lng: Double): Pair<String, String> {
        val online = systemGeocode(lat, lng)
        if (online.first.isNotBlank()) return online
        return offline.nearest(lat, lng) ?: online
    }

    private suspend fun systemGeocode(lat: Double, lng: Double): Pair<String, String> = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext "" to ""
        val geocoder = Geocoder(context, Locale.ENGLISH)
        val address = withTimeoutOrNull(8_000) {
            if (Build.VERSION.SDK_INT >= 33) {
                suspendCancellableCoroutine<android.location.Address?> { cont ->
                    geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(list: MutableList<android.location.Address>) { if (cont.isActive) cont.resume(list.firstOrNull()) }
                        override fun onError(errorMessage: String?) { if (cont.isActive) cont.resume(null) }
                    })
                }
            } else {
                @Suppress("DEPRECATION")
                runCatching { geocoder.getFromLocation(lat, lng, 1)?.firstOrNull() }.getOrNull()
            }
        }
        val city = address?.locality ?: address?.subAdminArea ?: address?.adminArea ?: ""
        city to (address?.countryName ?: "")
    }
}
