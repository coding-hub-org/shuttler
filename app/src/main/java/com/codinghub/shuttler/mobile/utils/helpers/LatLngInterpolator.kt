package com.codinghub.shuttler.mobile.utils.helpers

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.*

interface LatLngInterpolator {
    fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng
 }

class Spherical : LatLngInterpolator {
    /* From github.com/googlemaps/android-maps-utils */
    override fun interpolate(fraction: Float, from: LatLng, to: LatLng): LatLng {
        // http://en.wikipedia.org/wiki/Slerp
        val fromLat = toRadians(from.latitude)
        val fromLng = toRadians(from.longitude)
        val toLat = toRadians(to.latitude)
        val toLng = toRadians(to.longitude)
        val cosFromLat = cos(fromLat)
        val cosToLat = cos(toLat)
        // Computes Spherical interpolation coefficients.
        val angle = computeAngleBetween(fromLat, fromLng, toLat, toLng)
        val sinAngle = sin(angle)
        if (sinAngle < 1E-6) {
            return from
        }
        val a = sin((1 - fraction) * angle) / sinAngle
        val b = sin(fraction * angle) / sinAngle
        // Converts from polar to vector and interpolate.
        val x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng)
        val y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng)
        val z = a * sin(fromLat) + b * sin(toLat)
        // Converts interpolated vector back to polar.
        val lat = atan2(z, sqrt(x * x + y * y))
        val lng = atan2(y, x)
        return LatLng(toDegrees(lat), toDegrees(lng))
    }

    private fun computeAngleBetween(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Double {
        // Haversine's formula
        val dLat = fromLat - toLat
        val dLng = fromLng - toLng
        return 2 * asin(sqrt(pow(sin(dLat / 2), 2.0) + cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2.0)))
    }
}