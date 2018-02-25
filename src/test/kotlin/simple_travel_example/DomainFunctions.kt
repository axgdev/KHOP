package simple_travel_example

import simple_travel_example.model.Location

object DomainFunctions {
    fun taxiRate(location1: Location, location2: Location): Double {
        return (1.5 + 0.5*location1.distanceTo(location2))
    }
}
