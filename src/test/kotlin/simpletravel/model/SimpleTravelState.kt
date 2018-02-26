package simpletravel.model

import khop.State

data class SimpleTravelState(val person: Person, val taxiDriver: SimplePerson, val location: List<Location>): State<SimpleTravelState>() {
    override fun deepCopy(): SimpleTravelState {
        return this.copy()
    }
}