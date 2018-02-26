package simpletravel.model

data class Location(val name: String, val latitude: Double, val longitude: Double) {
    fun distanceToInKm(startLati: Double, startLong: Double, endLati: Double, endLong: Double): Double {
        val earthRadius = 6371;
        val diffLati = Math.toRadians(endLati - startLati)
        val diffLong = Math.toRadians(endLong - startLong)
        /**
         * At this point are possible to improve the resources' utilization by
         * assign the new results inside the existing variables, like startLati
         * and endLati. But I prefer to keep the clean code and the
         * self-explanatory name convention.
         */
        val radiusStartLati = Math.toRadians(startLati)
        val radiusEndLati = Math.toRadians(endLati)

        // A and C are the 'sides' from the spherical triangle.
        val a = Math.pow(Math.sin(diffLati / 2), 2.0) + Math.pow(Math.sin(diffLong / 2), 2.0) * Math.cos(radiusStartLati) * Math.cos(radiusEndLati)
        val c = 2 * Math.asin(Math.sqrt(a))

        return earthRadius * c
    }

    fun distanceTo(toLocation: Location): Double {
        return distanceToInKm(latitude, longitude, toLocation.latitude, toLocation.longitude)
    }
}