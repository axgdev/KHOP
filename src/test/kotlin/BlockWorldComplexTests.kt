import blocksworld.*
import blocksworld.methods.*
import blocksworld.operators.*
import khop.Domain
import khop.KHOP
import khop.NetworkElement
import khop.PlanObj
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class BlockWorldComplexTests {
    fun getInitialNetwork2(): BlocksState {
        return BlocksState(pos = mapOf(Pair("a", "c"), Pair("b", "d"), Pair("c", table), Pair("d", table)),
                clear = mapOf(Pair("a", true), Pair("c", false), Pair("b", true), Pair("d", false)),
                holding = falseHolding)
    }

    fun getComplexGoal_2_Solutions(): List<PlanObj<BlocksState>> {
        val alternativePlan1 = PlanObj(false, mutableListOf(
                Unstack("b", "d"),
                Putdown("b"),
                Unstack("a", "c"),
                StackOp("a", "d"),
                Pickup("b"),
                StackOp("b", "c"))
        )
        val alternativePlan2 = PlanObj(false, mutableListOf(
                Unstack("a", "c"),
                Putdown("a"),
                Unstack("b", "d"),
                StackOp("b", "c"),
                Pickup("a"),
                StackOp("a", "d"))
        )
        return listOf(alternativePlan1,alternativePlan2)
    }

    fun satisfiesOneOfTheSolutions(expected: List<Any>, actual: Any): Boolean {
        var satisfiesAny = false
        for (alternativeSolution in expected)
            satisfiesAny = satisfiesAny || (alternativeSolution == actual)
        return satisfiesAny
    }

    @Test
    fun complexGoal_2a() {
        val initialState = getInitialNetwork2()
        val goal = BlocksState(pos = mapOf(Pair("b", "c"), Pair("a", "d"), Pair("c", table), Pair("d", table)),
                clear = mapOf(Pair("a", true), Pair("c", false), Pair("b", true), Pair("d", false)),
                holding = falseHolding)
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal)))
        val mkhop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = mkhop.findPlan()
        println("Out of planner, plan: " + plan)
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_2_Solutions(),plan))
        assertTrue(isGoalStateSatisfied(mkhop.executePlan(plan), goal))
    }

    @Test
    fun complexGoal_2b() {
        val initialState = getInitialNetwork2()
        val goal = BlocksState(pos = mapOf(Pair("b", "c"), Pair("a", "d")))
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal)))
        val mkhop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = mkhop.findPlan()
        println("Out of planner, plan: " + plan)
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_2_Solutions(),plan))
        assertTrue(isGoalStateSatisfied(mkhop.executePlan(plan), goal))
    }

    fun getMapWithAllKeys(vararg keyValues: String): Map<String, String> {
        val resultingMap = mutableMapOf<String,String>()
        for (keyValue in keyValues) {
            val splitted = keyValue.split(":")
            if (splitted.size < 2)
                throw RuntimeException("Wrong keyValue: " + keyValue)
            resultingMap[splitted[0]] = splitted[1]
        }
        return resultingMap.toMap()
    }

    fun getInitialNetwork3(): BlocksState {
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

    fun getComplexGoal_3_solutions(): List<PlanObj<BlocksState>> {
        val alternative1 = PlanObj(false, mutableListOf(
                Unstack("1", "12"), Putdown("1"),
                Unstack("19", "18"), Putdown("19"),
                Unstack("18", "17"), Putdown("18"),
                Unstack("17", "16"), Putdown("17"),
                Unstack("9", "8"), Putdown("9"),
                Unstack("8", "7"), Putdown("8"),
                Unstack("11", "10"), StackOp("11", "7"),
                Unstack("10", "5"), Putdown("10"),
                Unstack("5", "4"), Putdown("5"),
                Unstack("4", "14"), Putdown("4"),
                Pickup("9"), StackOp("9", "4"),
                Pickup("8"), StackOp("8", "9"),
                Unstack("14", "15"), Putdown("14"),
                Unstack("16", "3"), StackOp("16", "11"),
                Unstack("3", "2"), StackOp("3", "16"),
                Pickup("2"), StackOp("2", "3"),
                Unstack("12", "13"), StackOp("12", "2"),
                Pickup("13"), StackOp("13", "8"),
                Pickup("15"), StackOp("15", "13")))
        val alternative2 = PlanObj(false, mutableListOf(
                Unstack("1", "12"), Putdown("1"),
                Unstack("19", "18"), Putdown("19"),
                Unstack("18", "17"), Putdown("18"),
                Unstack("17", "16"), Putdown("17"),
                Unstack("9", "8"), Putdown("9"),
                Unstack("8", "7"), Putdown("8"),
                Unstack("11", "10"), StackOp("11", "7"),
                Unstack("10", "5"), Putdown("10"),
                Unstack("5", "4"), Putdown("5"),
                Unstack("4", "14"), Putdown("4"),
                Unstack("14", "15"), Putdown("14"),
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

    @Test
    fun complexGoal_3a() {
        val initialState = getInitialNetwork3()
        val goal = BlocksState(pos = getMapWithAllKeys("15:13", "13:8", "8:9", "9:4", "4:table",
                "12:2", "2:3", "3:16", "16:11", "11:7", "7:6", "6:table"),
                clear = mapOf(Pair("17", true), Pair("15", true), Pair("12", true)))
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal)))
        val mkhop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = mkhop.findPlan()
        println("Out of planner, plan: " + plan)
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_3_solutions(),plan))
        val finalState = mkhop.executePlan(plan)
        assertTrue(isGoalStateSatisfied(finalState, goal))
    }

}