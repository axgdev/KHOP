package simple_travel_example.operators

import simple_travel_example.model.Location
import simple_travel_example.model.MyState
import Operator
import simple_travel_example.DomainFunctions

data class CallTaxi(val fromLocation: Location): Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return true
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(taxiDriver = state.taxiDriver.copy(location = fromLocation))
    }
}

class PayDriver(): Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.person.cash >= state.person.owe
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(person = state.person.copy(cash = state.person.cash - state.person.owe, owe = 0.0))
    }

    override fun toString(): String {
        return "PayDriver()"
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other?.toString()
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

data class RideTaxi(val fromLocation: Location, val toLocation: Location): Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.taxiDriver.location == fromLocation && state.person.location == fromLocation
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(
                person = state.person.copy(location = toLocation, owe = DomainFunctions.taxiRate(fromLocation, toLocation)),
                taxiDriver = state.taxiDriver.copy(location = toLocation)
        )
    }
}

data class Walk(val fromLocation: Location, val toLocation: Location) : Operator<MyState> {
    override fun satisfiesPreconditions(state: MyState): Boolean {
        return state.person.location == fromLocation
    }

    override fun applyEffects(state: MyState): MyState {
        return state.copy(person = state.person.copy(location = toLocation))
    }
}
