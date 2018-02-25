package simple_travel_example.model

import State

data class MyState(val person: Person, val taxiDriver: SimplePerson, val location: List<Location>): State<MyState>() {
    override fun deepCopy(): MyState {
        return this.copy()
    }
}