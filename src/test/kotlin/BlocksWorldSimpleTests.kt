import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import blocksworld.*
import blocksworld.methods.*
import blocksworld.operators.*
import khop.*

class BlocksWorldSimpleTests {
    private fun getInitialNetworkProblemsSimpleTest(): BlocksState {
        return BlocksState(pos = mapOf(Pair("a", "b"), Pair("b", table), Pair("c", table)),
                clear = mapOf(Pair("c", true), Pair("b", false), Pair("a", true)), holding = falseHolding)
    }

    @Test
    fun failBlockWorldProblem1() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Pickup("a")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(true), plan)
    }

    @Test
    fun failBlockWorldProblem2() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Pickup("b")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(true), plan)
    }

    @Test
    fun succeedBlockWorldProblem1() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Pickup("c")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Pickup("c"))), plan)
    }

    @Test
    fun succeedBlockWorldProblem2() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Unstack("a", "b")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Unstack("a", "b"))), plan)
    }

    @Test
    fun succeedBlockWorldProblem3() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(GetM("a")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Unstack("a", "b"))), plan)
    }

    @Test
    fun failBlockWorldProblem3() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(GetM("b")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf()), plan)
    }

    @Test
    fun succeedBlockWorldProblem4() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(GetM("c")))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Pickup("c"))), plan)
    }

    private fun getComplexGoal_1_Solution(): PlanObj<BlocksState> {
        return PlanObj(false, mutableListOf(
                Unstack("a", "b"),
                PutDown("a"),
                Pickup("b"),
                StackOp("b", "a"),
                Pickup("c"),
                StackOp("c", "b")))
    }

    @Test
    fun complexGoal_1a() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val goal = BlocksState(pos = mapOf(Pair("c", "b"), Pair("b", "a"), Pair("a", table)),
                clear = mapOf(Pair("c", true), Pair("b", false), Pair("a", false)), holding = falseHolding)
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal)))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(getComplexGoal_1_Solution(), plan)
        assertTrue(isGoalStateSatisfied(planner.executePlan(plan), goal))
    }

    @Test
    fun complexGoal_1b() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val goal = BlocksState(pos = mapOf(Pair("c", "b"), Pair("b", "a")))
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal)))
        val planner = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = planner.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(getComplexGoal_1_Solution(), plan)
        assertTrue(isGoalStateSatisfied(planner.executePlan(plan), goal))
    }

}