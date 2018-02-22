import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class TestsWithProblems {

    @Test
    fun travelByFootOperatorTest() {
        //given
        //initial state, initial network
        val london = Location("London", 51.507351, -0.127758)
        val manchester = Location("Manchester", 53.480759, -2.242631)
        val edinburgh = Location("Edinburgh", 55.953252, -3.188267)
        val locations = arrayListOf(london, manchester, edinburgh)
        val axel = Person(london, 0.0,0.0)
        val taxiDriver = SimplePerson(manchester)
        val initialState = MyState(axel, taxiDriver, locations)
        val initialNetwork = LinkedList<NetworkElement>(listOf(Travel(london, manchester)))
        val khop = KHOP<MyState>(Domain(initialState,initialNetwork), 10)
        val plan = khop.findPlan()
        println("out of planner, plan: " + plan)
        val expectedActions = mutableListOf(Walk(london, manchester) as Operator<MyState>)
        val expectedPlan = PlanObj<MyState>(actions = expectedActions)
        assertEquals(expectedPlan, plan)
    }
}