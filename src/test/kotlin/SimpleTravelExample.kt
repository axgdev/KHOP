data class Location(val name: String, val latitude: Double, val longitude: Double) {
    fun distanceToInKm(startLati: Double, startLong: Double, endLati: Double, endLong: Double): Double {
        val earth_radius = 6371;
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

        return earth_radius * c
    }

    fun distanceTo(toLocation: Location): Double {
        return distanceToInKm(latitude, longitude, toLocation.latitude, toLocation.longitude)
    }
}

interface Localizable {
    val location: Location
}

data class SimplePerson(override val location: Location): Localizable

data class Person(override val location: Location, val cash: Double, val owe: Double): Localizable


data class MyState(val person: Person, val taxiDriver: SimplePerson, val location: List<Location>): State<MyState>() {
    override fun deepCopy(): MyState {
        return this.copy()
    }
}

fun taxiRate(location1: Location, location2: Location): Double {
    return (1.5 + 0.5*location1.distanceTo(location2))
}

data class Walk(val fromLocation: Location, val toLocation: Location) : Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.person.location == fromLocation
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(person = state.person.copy(location = toLocation))
    }
}

data class CallTaxi(val toLocation: Location): Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return true
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(taxiDriver = state.taxiDriver.copy(location = toLocation))
    }
}

data class RideTaxi(val fromLocation: Location, val toLocation: Location): Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.taxiDriver.location == fromLocation && state.person.location == fromLocation
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(
                person = state.person.copy(location = toLocation, owe = taxiRate(fromLocation, toLocation)),
                taxiDriver = state.taxiDriver.copy(location = toLocation)
        )
    }
}

class PayDriver(): Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.person.cash >= state.person.owe
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(person = state.person.copy(cash = state.person.cash - state.person.owe, owe = 0.0))
    }
}

data class TravelByFoot(val fromLocation: Location, val toLocation: Location): Method<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return fromLocation.distanceTo(toLocation) <= 300
    }

    override fun decompose(): List<NetworkElement> {
        return listOf(Walk(fromLocation, toLocation))
    }
}

data class TravelByTaxi(val fromLocation: Location, val toLocation: Location): Method<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.person.cash >= taxiRate(fromLocation,toLocation)
    }

    override fun decompose(): List<NetworkElement> {
        return listOf(CallTaxi(toLocation), RideTaxi(fromLocation, toLocation), PayDriver())
    }
}

data class Travel(val fromLocation: Location, val toLocation: Location): MethodGroup<MyState> {
    override val methods =
            listOf(TravelByFoot(fromLocation, toLocation), TravelByTaxi(fromLocation, toLocation))
}