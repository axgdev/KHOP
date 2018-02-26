package simpletravel.model

data class Person(override val location: Location, val cash: Double, val owe: Double): HasLocation