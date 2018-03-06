import org.junit.Test
import java.util.*
import blocksworld.*
import blocksworld.methods.*
import blocksworld.operators.*
import khop.*

class BlocksWorldBacktrackTest {
    private fun getInitialNetworkProblemsSimpleTest(): BlocksState {
        return BlocksState(pos = mapOf(Pair("a", "b"), Pair("b", table), Pair("c", table)),
                clear = mapOf(Pair("c", true), Pair("b", false), Pair("a", true)), holding = falseHolding)
    }

    @Test
    fun shouldBacktrackOnce() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(GetMG("a")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        assertPlanActionStatusEquals(PlanObj<BlocksState>(false, mutableListOf(Unstack("a", "b"))), plan)
    }

    @Test
    fun shouldNotBacktrack() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(GetMG("c")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        assertPlanActionStatusEquals(PlanObj<BlocksState>(false, mutableListOf(Pickup("c"))), plan)
    }

    @Test//(expected = Exception::class)
    fun shouldFail() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(GetMG("b")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        planner.findPlan()
    }
}