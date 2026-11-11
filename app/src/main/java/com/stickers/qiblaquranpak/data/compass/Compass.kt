package com.stickers.qiblaquranpak.data.compass

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class Heading(
    /** Degrees from true north, 0..360. */
    val azimuth: Float,
    /** SensorManager.SENSOR_STATUS_* of the magnetometer; < MEDIUM means calibration is needed. */
    val accuracy: Int,
)

class Compass(context: Context) {
    private val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotation = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnet = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val isAvailable: Boolean get() = rotation != null || (accel != null && magnet != null)

    /** Smoothed heading; [lat]/[lng]/[alt] are used to correct magnetic north to true north. */
    fun headings(lat: Double, lng: Double, alt: Double = 0.0): Flow<Heading> = callbackFlow {
        val declination = GeomagneticField(lat.toFloat(), lng.toFloat(), alt.toFloat(), System.currentTimeMillis()).declination
        val rot = FloatArray(9)
        val orient = FloatArray(3)
        val grav = FloatArray(3)
        val geo = FloatArray(3)
        var hasGrav = false
        var hasGeo = false
        var sinS = 0.0
        var cosS = 1.0
        var first = true
        var accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH

        fun emit() {
            SensorManager.getOrientation(rot, orient)
            val a = orient[0].toDouble() + Math.toRadians(declination.toDouble())
            // Low-pass on the unit circle so 359° -> 1° does not spin the dial.
            if (first) { sinS = sin(a); cosS = cos(a); first = false } else {
                sinS += ALPHA * (sin(a) - sinS)
                cosS += ALPHA * (cos(a) - cosS)
            }
            val deg = ((Math.toDegrees(atan2(sinS, cosS)) + 360) % 360).toFloat()
            trySend(Heading(deg, accuracy))
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                when (e.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rot, e.values)
                        emit()
                    }
                    Sensor.TYPE_ACCELEROMETER -> { System.arraycopy(e.values, 0, grav, 0, 3); hasGrav = true }
                    Sensor.TYPE_MAGNETIC_FIELD -> { System.arraycopy(e.values, 0, geo, 0, 3); hasGeo = true }
                }
                if (rotation == null && hasGrav && hasGeo && e.sensor.type != Sensor.TYPE_ROTATION_VECTOR) {
                    if (SensorManager.getRotationMatrix(rot, null, grav, geo)) emit()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, acc: Int) {
                if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD || sensor.type == Sensor.TYPE_ROTATION_VECTOR) accuracy = acc
            }
        }
        val rate = SensorManager.SENSOR_DELAY_UI
        if (rotation != null) sm.registerListener(listener, rotation, rate) else {
            sm.registerListener(listener, accel, rate)
            sm.registerListener(listener, magnet, rate)
        }
        // Magnetometer accuracy callbacks still drive the calibration prompt when using rotation vector.
        if (rotation != null && magnet != null) sm.registerListener(listener, magnet, SensorManager.SENSOR_DELAY_NORMAL)
        awaitClose { sm.unregisterListener(listener) }
    }

    private companion object { const val ALPHA = 0.15 }
}
