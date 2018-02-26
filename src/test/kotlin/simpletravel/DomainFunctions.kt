package simpletravel

import simpletravel.model.Location

object DomainFunctions {
    fun taxiRate(location1: Location, location2: Location): Double {
        return (1.5 + 0.5*location1.distanceTo(location2))
    }
}
