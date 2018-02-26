import khop.*
import org.junit.Assert.assertEquals
import org.junit.Test
import simpletravel.methods.Travel
import simpletravel.model.*
import simpletravel.operators.*
import java.util.*

class SimpleTravelExampleTests {

    val london = Location("London", 51.507351, -0.127758)
    val manchester = Location("Manchester", 53.480759, -2.242631)
    val edinburgh = Location("Edinburgh", 55.953252, -3.188267)
    val locations = arrayListOf(london, manchester, edinburgh)
    val taxiDriver = SimplePerson(manchester)

    @Test
    fun travelByFootOperatorTest() {
        val axel = Person(london, 0.0, 0.0)
        val initialState = SimpleTravelState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement>(listOf(Travel(london, manchester)))
        val mkhop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = mkhop.findPlan()
        println("out of planner, plan: " + plan)
        val expectedActions = mutableListOf(Walk(london, manchester) as Operator<SimpleTravelState>)
        val expectedPlan = PlanObj(actions = expectedActions)
        assertEquals(expectedPlan, plan)
    }

    @Test
    fun travelByTaxiMethodTest() {
        val axel = Person(london, 534.0 * 1000, 0.0)
        val initialState = SimpleTravelState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement>(listOf(Travel(london, edinburgh)))
        val mkhop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = mkhop.findPlan()
        println("out of planner, plan: " + plan)
        assertEquals(PlanObj(actions = mutableListOf(
                CallTaxi(london),
                RideTaxi(london, edinburgh),
                PayDriver())), plan)
    }
}