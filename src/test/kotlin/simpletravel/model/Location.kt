package simpletravel.model

data class Location(val name: String, val latitude: Double, val longitude: Double) {
    fun distanceToInKm(startLatitude: Double, startLongitude: Double,
                       endLatitude: Double, endLongitude: Double): Double {
        val earthRadius = 6371
        val diffLatitude = Math.toRadians(endLatitude - startLatitude)
        val diffLongitude = Math.toRadians(endLongitude - startLongitude)
        /**
         * At this point are possible to improve the resources' utilization by
         * assign the new results inside the existing variables, like startLatitude
         * and endLatitude. But I prefer to keep the clean code and the
         * self-explanatory name convention.
         */
        val radiusStartLatitude = Math.toRadians(startLatitude)
        val radiusEndLatitude = Math.toRadians(endLatitude)

        // A and C are the 'sides' from the spherical triangle.
        val a = Math.pow(Math.sin(diffLatitude / 2), 2.0) + Math.pow(Math.sin(diffLongitude / 2), 2.0) * Math.cos(radiusStartLatitude) * Math.cos(radiusEndLatitude)
        val c = 2 * Math.asin(Math.sqrt(a))

        return earthRadius * c
    }

    fun distanceTo(toLocation: Location): Double {
        return distanceToInKm(latitude, longitude, toLocation.latitude, toLocation.longitude)
    }
}