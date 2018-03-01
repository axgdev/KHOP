import khop.*
import org.junit.Test
import simpletravel.methods.Travel
import simpletravel.model.*
import simpletravel.operators.*
import java.util.*

class SimpleTravelExampleTests {

    private val london = Location("London", 51.507351, -0.127758)
    private val manchester = Location("Manchester", 53.480759, -2.242631)
    private val edinburgh = Location("Edinburgh", 55.953252, -3.188267)
    private val locations = arrayListOf(london, manchester, edinburgh)
    private val taxiDriver = SimplePerson(manchester)

    @Test
    fun travelByFootOperatorTest() {
        val axel = Person(london, 0.0, 0.0)
        val initialState = SimpleTravelState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement>(listOf(Travel(london, manchester)))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        val expectedActions = mutableListOf(Walk(london, manchester) as Operator<SimpleTravelState>)
        val expectedPlan = PlanObj(actions = expectedActions)
        assertEquals(expectedPlan, plan)
    }

    @Test
    fun travelByTaxiMethodTest() {
        val axel = Person(london, 534.0 * 1000, 0.0)
        val initialState = SimpleTravelState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement>(listOf(Travel(london, edinburgh)))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        assertEquals(PlanObj(actions = mutableListOf(
                CallTaxi(london),
                RideTaxi(london, edinburgh),
                PayDriver())), plan)
    }
}