import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import blocks_world.*
import blocks_world.methods.*
import blocks_world.operators.*

class BlocksWorldSimpleTests {
    fun getInitialNetworkProblemsSimpleTest(): BlocksState {
        return BlocksState(pos = mapOf(Pair("a", "b"), Pair("b", table), Pair("c", table)),
                clear = mapOf(Pair("c", true), Pair("b", false), Pair("a", true)), holding = falseHolding)
    }

    @Test
    fun failBlockWorldProblem1() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Pickup("a")))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(true), plan)
    }

    @Test
    fun failBlockWorldProblem2() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Pickup("b")))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(true), plan)
    }

    @Test
    fun succeedBlockWorldProblem1() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Pickup("c")))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Pickup("c"))), plan)
    }

    @Test
    fun succeedBlockWorldProblem2() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Unstack("a", "b")))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Unstack("a", "b"))), plan)
    }

    @Test
    fun succeedBlockWorldProblem3() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Get_m("a")))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Unstack("a", "b"))), plan)
    }

    @Test
    fun failBlockWorldProblem3() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Get_m("b")))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf()), plan)
    }

    @Test
    fun succeedBlockWorldProblem4() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val initialNetwork = LinkedList<NetworkElement>(listOf(Get_m("c")))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(PlanObj<BlocksState>(false, mutableListOf(Pickup("c"))), plan)
    }

    fun getComplexGoal_1_Solution(): PlanObj<BlocksState> {
        return PlanObj<BlocksState>(false, mutableListOf(
                Unstack("a", "b"),
                Putdown("a"),
                Pickup("b"),
                StackOp("b", "a"),
                Pickup("c"),
                StackOp("c", "b")))
    }

    @Test
    fun ComplexGoal_1a() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val goal_1a = BlocksState(pos = mapOf(Pair("c", "b"), Pair("b", "a"), Pair("a", table)),
                clear = mapOf(Pair("c", true), Pair("b", false), Pair("a", false)), holding = falseHolding)
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal_1a)))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(getComplexGoal_1_Solution(), plan)
        assertTrue(isGoalStateSatisfied(khop.executePlan(plan), goal_1a))
    }

    @Test
    fun ComplexGoal_1b() {
        val initialState = getInitialNetworkProblemsSimpleTest()
        val goal_1b = BlocksState(pos = mapOf(Pair("c", "b"), Pair("b", "a")))
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal_1b)))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertEquals(getComplexGoal_1_Solution(), plan)
        assertTrue(isGoalStateSatisfied(khop.executePlan(plan), goal_1b))
    }

}