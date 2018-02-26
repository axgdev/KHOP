package simpletravel.operators

import simpletravel.model.Location
import simpletravel.model.SimpleTravelState
import khop.Operator
import simpletravel.DomainFunctions

data class CallTaxi(val fromLocation: Location): Operator<SimpleTravelState> {
    override fun satisfiesPreconditions(state: SimpleTravelState): Boolean {
        return true
    }

    override fun applyEffects(state: SimpleTravelState): SimpleTravelState {
        return state.copy(taxiDriver = state.taxiDriver.copy(location = fromLocation))
    }
}

class PayDriver(): Operator<SimpleTravelState> {
    override fun satisfiesPreconditions(state: SimpleTravelState): Boolean {
        return state.person.cash >= state.person.owe
    }

    override fun applyEffects(state: SimpleTravelState): SimpleTravelState {
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

data class RideTaxi(val fromLocation: Location, val toLocation: Location): Operator<SimpleTravelState> {
    override fun satisfiesPreconditions(state: SimpleTravelState): Boolean {
        return state.taxiDriver.location == fromLocation && state.person.location == fromLocation
    }

    override fun applyEffects(state: SimpleTravelState): SimpleTravelState {
        return state.copy(
                person = state.person.copy(location = toLocation, owe = DomainFunctions.taxiRate(fromLocation, toLocation)),
                taxiDriver = state.taxiDriver.copy(location = toLocation)
        )
    }
}

data class Walk(val fromLocation: Location, val toLocation: Location) : Operator<SimpleTravelState> {
    override fun satisfiesPreconditions(state: SimpleTravelState): Boolean {
        return state.person.location == fromLocation
    }

    override fun applyEffects(state: SimpleTravelState): SimpleTravelState {
        return state.copy(person = state.person.copy(location = toLocation))
    }
}
