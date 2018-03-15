import blocksworld.*
import blocksworld.methods.*
import blocksworld.operators.*
import khop.*
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class BlockWorldComplexTests {
    private fun getInitialNetwork2(): BlocksState {
        return BlocksState(pos = mapOf(Pair("a", "c"), Pair("b", "d"), Pair("c", table), Pair("d", table)),
                clear = mapOf(Pair("a", true), Pair("c", false), Pair("b", true), Pair("d", false)),
                holding = falseHolding)
    }

    private fun getComplexGoal_2_Solutions(): List<PlanObj<BlocksState>> {
        val alternativePlan1 = PlanObj(false, mutableListOf(
                Unstack("b", "d"),
                PutDown("b"),
                Unstack("a", "c"),
                StackOp("a", "d"),
                Pickup("b"),
                StackOp("b", "c"))
        )
        val alternativePlan2 = PlanObj(false, mutableListOf(
                Unstack("a", "c"),
                PutDown("a"),
                Unstack("b", "d"),
                StackOp("b", "c"),
                Pickup("a"),
                StackOp("a", "d"))
        )
        return listOf(alternativePlan1,alternativePlan2)
    }

    private fun <ExtendedState: State<ExtendedState>> satisfiesOneOfTheSolutions(
            expected: List<Plan<ExtendedState>>, actual: Plan<ExtendedState>): Boolean {
        var satisfiesAny = false
        for (alternativeSolution in expected)
            satisfiesAny = satisfiesAny || ((alternativeSolution.failed == actual.failed) && (alternativeSolution.actions == actual.actions))
        return satisfiesAny
    }

    fun getComplexGoal2aPlanner(): KHOP<BlocksState> {
        val initialState = getInitialNetwork2()
        val goal = getComplex2aGoal()
        val initialNetwork = ArrayDeque<NetworkElement<BlocksState>>(listOf(MoveBlocks(goal)))
        return KHOP(Domain(initialState, initialNetwork), 1)
    }

    fun getComplex2aGoal(): BlocksState {
        return BlocksState(pos = mapOf(Pair("b", "c"), Pair("a", "d"), Pair("c", table), Pair("d", table)),
                clear = mapOf(Pair("a", true), Pair("c", false), Pair("b", true), Pair("d", false)),
                holding = falseHolding)
    }

    @Test
    fun complexGoal_2a() {
        val planner = getComplexGoal2aPlanner()
        val plan = planner.findPlan()
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_2_Solutions(),plan))
        assertTrue(isGoalStateSatisfied(planner.executePlan(plan), getComplex2aGoal()))
    }

    fun getComplexGoal2bPlanner(): KHOP<BlocksState> {
        val initialState = getInitialNetwork2()
        val goal = getComplex2bGoal()
        val initialNetwork = ArrayDeque<NetworkElement<BlocksState>>(listOf(MoveBlocks(goal)))
        return KHOP(Domain(initialState, initialNetwork), 1)
    }

    fun getComplex2bGoal(): BlocksState {
        return BlocksState(pos = mapOf(Pair("b", "c"), Pair("a", "d")))
    }

    @Test
    fun complexGoal_2b() {
        val planner = getComplexGoal2bPlanner()
        val plan = planner.findPlan()
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_2_Solutions(),plan))
        assertTrue(isGoalStateSatisfied(planner.executePlan(plan), getComplex2bGoal()))
    }

    private fun getMapWithAllKeys(vararg keyValues: String): Map<String, String> {
        val resultingMap = mutableMapOf<String,String>()
        for (keyValue in keyValues) {
            val split = keyValue.split(":")
            if (split.size < 2)
                throw RuntimeException("Wrong keyValue: " + keyValue)
            resultingMap[split[0]] = split[1]
        }
        return resultingMap.toMap()
    }

    private fun getInitialNetwork3(): BlocksState {
        val clearS = mutableMapOf<String,Boolean>()
        for (i in 1..19)
            clearS[i.toString()] = false
        clearS.replace("1", true)
        clearS.replace("11", true)
        clearS.replace("9", true)
        clearS.replace("19", true)
        return BlocksState(pos = getMapWithAllKeys("1:12", "12:13", "13:table", "11:10", "10:5", "5:4",
                "4:14", "14:15", "15:table", "9:8", "8:7", "7:6", "6:table",
                "19:18", "18:17", "17:16", "16:3", "3:2", "2:table"),
                clear = clearS.toMap(),
                holding = falseHolding)
    }

    private fun getComplexGoal_3_solutions(): List<PlanObj<BlocksState>> {
        val alternative1 = PlanObj(false, mutableListOf(
                Unstack("1", "12"), PutDown("1"),
                Unstack("19", "18"), PutDown("19"),
                Unstack("18", "17"), PutDown("18"),
                Unstack("17", "16"), PutDown("17"),
                Unstack("9", "8"), PutDown("9"),
                Unstack("8", "7"), PutDown("8"),
                Unstack("11", "10"), StackOp("11", "7"),
                Unstack("10", "5"), PutDown("10"),
                Unstack("5", "4"), PutDown("5"),
                Unstack("4", "14"), PutDown("4"),
                Pickup("9"), StackOp("9", "4"),
                Pickup("8"), StackOp("8", "9"),
                Unstack("14", "15"), PutDown("14"),
                Unstack("16", "3"), StackOp("16", "11"),
                Unstack("3", "2"), StackOp("3", "16"),
                Pickup("2"), StackOp("2", "3"),
                Unstack("12", "13"), StackOp("12", "2"),
                Pickup("13"), StackOp("13", "8"),
                Pickup("15"), StackOp("15", "13")))
        val alternative2 = PlanObj(false, mutableListOf(
                Unstack("1", "12"), PutDown("1"),
                Unstack("19", "18"), PutDown("19"),
                Unstack("18", "17"), PutDown("18"),
                Unstack("17", "16"), PutDown("17"),
                Unstack("9", "8"), PutDown("9"),
                Unstack("8", "7"), PutDown("8"),
                Unstack("11", "10"), StackOp("11", "7"),
                Unstack("10", "5"), PutDown("10"),
                Unstack("5", "4"), PutDown("5"),
                Unstack("4", "14"), PutDown("4"),
                Unstack("14", "15"), PutDown("14"),
                Unstack("16", "3"), StackOp("16", "11"),
                Unstack("3", "2"), StackOp("3", "16"),
                Pickup("2"), StackOp("2", "3"),
                Unstack("12", "13"), StackOp("12", "2"),
                Pickup("9"), StackOp("9", "4"),
                Pickup("8"), StackOp("8", "9"),
                Pickup("13"), StackOp("13", "8"),
                Pickup("15"), StackOp("15", "13")))
        return listOf(alternative1,alternative2)
    }

    fun getComplexGoal3aPlanner(): KHOP<BlocksState> {
        val initialState = getInitialNetwork3()
        val goal = getComplex3aGoal()
        val initialNetwork = ArrayDeque<NetworkElement<BlocksState>>(listOf(MoveBlocks(goal)))
        return KHOP(Domain(initialState, initialNetwork), 1)
    }

    fun getComplex3aGoal(): BlocksState {
        return BlocksState(pos = getMapWithAllKeys("15:13", "13:8", "8:9", "9:4", "4:table",
                "12:2", "2:3", "3:16", "16:11", "11:7", "7:6", "6:table"),
                clear = mapOf(Pair("17", true), Pair("15", true), Pair("12", true)))
    }

    @Test
    fun complexGoal_3a() {
        val goal = getComplex3aGoal()
        val planner = getComplexGoal3aPlanner()
        val plan = planner.findPlan()
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_3_solutions(),plan))
        val finalState = planner.executePlan(plan)
        assertTrue(isGoalStateSatisfied(finalState, goal))
    }

    @Test
    fun planShouldReturnSameStateAsPlanExecution() {
        val planners = listOf(getComplexGoal2aPlanner(), getComplexGoal2bPlanner(), getComplexGoal3aPlanner())
        for (planner in planners) {
            val plan = planner.findPlan()
            assertEquals(plan.state, planner.executePlan(plan))
        }
    }

}