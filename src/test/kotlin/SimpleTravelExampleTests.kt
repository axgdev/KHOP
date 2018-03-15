import khop.*
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    fun getTravelByFootTestPlanner(): KHOP<SimpleTravelState> {
        val axel = Person(london, 0.0, 0.0)
        val initialState = SimpleTravelState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement<SimpleTravelState>>(listOf(Travel(london, manchester)))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        return planner
    }

    @Test
    fun travelByFootOperatorTest() {
        val plan = getTravelByFootTestPlanner().findPlan()
        val expectedActions = mutableListOf(Walk(london, manchester) as Operator<SimpleTravelState>)
        val expectedPlan = PlanObj(actions = expectedActions)
        assertPlanActionStatusEquals(expectedPlan, plan)
    }

    fun getTravelByTaxiTestPlanner(): KHOP<SimpleTravelState> {
        val axel = Person(london, 534.0 * 1000, 0.0)
        val initialState = SimpleTravelState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement<SimpleTravelState>>(listOf(Travel(london, edinburgh)))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        return planner
    }

    @Test
    fun travelByTaxiMethodTest() {
        val plan = getTravelByTaxiTestPlanner().findPlan()
        assertPlanActionStatusEquals(PlanObj(actions = mutableListOf(
                CallTaxi(london),
                RideTaxi(london, edinburgh),
                PayDriver())), plan)
    }

    @Test
    fun shouldReturnTwoPlans() {
        val axel = Person(london, 534.0 * 10000, 0.0)
        val initialState = SimpleTravelState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement<SimpleTravelState>>(listOf(Travel(london, manchester)))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plans = planner.getAllPlans()
        val expectedPlan1 = PlanObj(actions = mutableListOf(Walk(london, manchester)))
        val expectedPlan2 = PlanObj(actions = mutableListOf(
                CallTaxi(london),
                RideTaxi(london, manchester),
                PayDriver()))
        var passesTest = true
        for (plan in plans)
            passesTest = plan.customEquals(expectedPlan1) || plan.customEquals(expectedPlan2)
        assertTrue(passesTest)
        assertEquals(2, plans.size)
    }

    @Test
    fun planShouldReturnSameStateAsPlanExecution() {
        val planners = listOf(getTravelByFootTestPlanner(), getTravelByTaxiTestPlanner())
        for (planner in planners) {
            val plan = planner.findPlan()
            assertEquals(plan.state, planner.executePlan(plan))
        }
    }
}