import org.junit.Assert.assertEquals
import org.junit.Test
import simple_travel_example.methods.Travel
import simple_travel_example.model.*
import simple_travel_example.operators.*
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
        val initialState = MyState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement>(listOf(Travel(london, manchester)))
        val khop = KHOP(Domain(initialState,initialNetwork), 1)
        val plan = khop.findPlan()
        println("out of planner, plan: " + plan)
        val expectedActions = mutableListOf(Walk(london, manchester) as Operator<MyState>)
        val expectedPlan = PlanObj<MyState>(actions = expectedActions)
        assertEquals(expectedPlan, plan)
    }

    @Test
    fun travelByTaxiMethodTest() {
        val axel = Person(london, 534.0 * 1000, 0.0)
        val initialState = MyState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement>(listOf(Travel(london, edinburgh)))
        val khop = KHOP(Domain(initialState,initialNetwork), 1)
        val plan = khop.findPlan()
        println("out of planner, plan: " + plan)
        assertEquals(PlanObj<MyState>(actions = mutableListOf(
                CallTaxi(london),
                RideTaxi(london, edinburgh),
                PayDriver())), plan)
    }
}