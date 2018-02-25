package simple_travel_example.methods

import simple_travel_example.model.Location
import simple_travel_example.model.MyState
import Method
import MethodGroup
import NetworkElement
import simple_travel_example.DomainFunctions
import simple_travel_example.operators.*

data class TravelByFoot(val fromLocation: Location, val toLocation: Location): Method<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return fromLocation.distanceTo(toLocation) <= 300
    }

    override fun decompose(state: MyState): List<NetworkElement> {
        return listOf(Walk(fromLocation, toLocation))
    }
}

data class TravelByTaxi(val fromLocation: Location, val toLocation: Location): Method<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.person.cash >= DomainFunctions.taxiRate(fromLocation, toLocation)
    }

    override fun decompose(state: MyState): List<NetworkElement> {
        return listOf(CallTaxi(fromLocation), RideTaxi(fromLocation, toLocation), PayDriver())
    }
}

data class Travel(val fromLocation: Location, val toLocation: Location): MethodGroup<MyState> {
    override val methods =
            listOf(TravelByFoot(fromLocation, toLocation), TravelByTaxi(fromLocation, toLocation))
}
