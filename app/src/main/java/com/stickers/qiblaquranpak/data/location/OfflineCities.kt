package com.stickers.qiblaquranpak.data.location

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.cos

/**
 * Nearest-city lookup from the bundled GeoNames list (cities > 15k people, CC BY 4.0),
 * used when the system Geocoder is unavailable or offline.
 */
class OfflineCities(private val context: Context) {
    private val mutex = Mutex()
    private var names: Array<String>? = null
    private var countries: Array<String> = emptyArray()
    private var lats = FloatArray(0)
    private var lngs = FloatArray(0)

    suspend fun nearest(lat: Double, lng: Double): Pair<String, String>? = withContext(Dispatchers.Default) {
        load()
        val n = names ?: return@withContext null
        val k = cos(Math.toRadians(lat)).toFloat()
        var best = -1
        var bestD = Float.MAX_VALUE
        for (i in n.indices) {
            val dy = lats[i] - lat.toFloat()
            val dx = (lngs[i] - lng.toFloat()) * k
            val d = dx * dx + dy * dy
            if (d < bestD) { bestD = d; best = i }
        }
        // ~0.5° (about 55 km) — further than that we'd rather show nothing than a wrong city.
        if (best < 0 || bestD > 0.25f) return@withContext null
        n[best] to Locale("", countries[best]).getDisplayCountry(Locale.ENGLISH)
    }

    private suspend fun load() = mutex.withLock {
        if (names != null) return@withLock
        withContext(Dispatchers.IO) {
            val nm = ArrayList<String>(35_000)
            val cc = ArrayList<String>(35_000)
            val la = ArrayList<Float>(35_000)
            val lo = ArrayList<Float>(35_000)
            context.assets.open("geo/cities.tsv").bufferedReader().useLines { lines ->
                for (line in lines) {
                    val p = line.split('\t')
                    if (p.size < 4) continue
                    nm += p[0]; cc += p[1]; la += p[2].toFloat(); lo += p[3].toFloat()
                }
            }
            countries = cc.toTypedArray()
            lats = la.toFloatArray()
            lngs = lo.toFloatArray()
            names = nm.toTypedArray()
        }
    }
}
