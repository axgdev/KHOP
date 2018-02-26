package simpletravel.methods

import simpletravel.model.Location
import simpletravel.model.SimpleTravelState
import khop.Method
import khop.MethodGroup
import khop.NetworkElement
import simpletravel.DomainFunctions
import simpletravel.operators.*

data class TravelByFoot(val fromLocation: Location, val toLocation: Location): Method<SimpleTravelState> {
    override fun satisfiesPreconditions(state: SimpleTravelState): Boolean {
        return fromLocation.distanceTo(toLocation) <= 300
    }

    override fun decompose(state: SimpleTravelState): List<NetworkElement> {
        return listOf(Walk(fromLocation, toLocation))
    }
}

data class TravelByTaxi(val fromLocation: Location, val toLocation: Location): Method<SimpleTravelState> {
    override fun satisfiesPreconditions(state: SimpleTravelState): Boolean {
        return state.person.cash >= DomainFunctions.taxiRate(fromLocation, toLocation)
    }

    override fun decompose(state: SimpleTravelState): List<NetworkElement> {
        return listOf(CallTaxi(fromLocation), RideTaxi(fromLocation, toLocation), PayDriver())
    }
}

data class Travel(val fromLocation: Location, val toLocation: Location): MethodGroup<SimpleTravelState> {
    override val methods =
            listOf(TravelByFoot(fromLocation, toLocation), TravelByTaxi(fromLocation, toLocation))
}
