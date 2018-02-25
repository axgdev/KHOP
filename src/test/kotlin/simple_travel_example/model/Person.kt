package simple_travel_example.model

data class Person(override val location: Location, val cash: Double, val owe: Double): Localizable