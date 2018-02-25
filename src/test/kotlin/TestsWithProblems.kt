import blocks_world.BlocksState
import blocks_world.falseHolding
import blocks_world.isGoalStateSatisfied
import blocks_world.methods.Get_m
import blocks_world.methods.MoveBlocks
import blocks_world.operators.Pickup
import blocks_world.operators.Putdown
import blocks_world.operators.Stack
import blocks_world.operators.Unstack
import blocks_world.table
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import simple_travel_example.model.Location
import simple_travel_example.model.SimplePerson
import simple_travel_example.model.Person
import simple_travel_example.methods.Travel
import simple_travel_example.model.MyState
import simple_travel_example.operators.Walk
import simple_travel_example.operators.CallTaxi
import simple_travel_example.operators.RideTaxi
import simple_travel_example.operators.PayDriver

class TestsWithProblems {

    @Test
    fun travelByFootOperatorTest() {
        //given
        //initial state, initial network
        val london = Location("London", 51.507351, -0.127758)
        val manchester = Location("Manchester", 53.480759, -2.242631)
        val edinburgh = Location("Edinburgh", 55.953252, -3.188267)
        val locations = arrayListOf(london, manchester, edinburgh)
        val axel = Person(london, 0.0, 0.0)
        val taxiDriver = SimplePerson(manchester)
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
        //given
        //initial state, initial network
        val london = Location("London", 51.507351, -0.127758)
        val manchester = Location("Manchester", 53.480759, -2.242631)
        val edinburgh = Location("Edinburgh", 55.953252, -3.188267)
        val locations = arrayListOf(london, manchester, edinburgh)
        val axel = Person(london, 534.0 * 1000, 0.0)
        val taxiDriver = SimplePerson(manchester)
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
                Stack("b", "a"),
                Pickup("c"),
                Stack("c", "b")))
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

    fun getInitialNetwork2(): BlocksState {
        return BlocksState(pos = mapOf(Pair("a", "c"), Pair("b", "d"), Pair("c", table), Pair("d", table)),
                clear = mapOf(Pair("a", true), Pair("c", false), Pair("b", true), Pair("d", false)),
                holding = falseHolding)
    }

    fun getComplexGoal_2_Solutions(): List<PlanObj<BlocksState>> {
        val alternativePlan1 = PlanObj<BlocksState>(false, mutableListOf(
                Unstack("b", "d"),
                Putdown("b"),
                Unstack("a", "c"),
                Stack("a", "d"),
                Pickup("b"),
                Stack("b", "c"))
        )
        val alternativePlan2 = PlanObj<BlocksState>(false, mutableListOf(
                Unstack("a", "c"),
                Putdown("a"),
                Unstack("b", "d"),
                Stack("b", "c"),
                Pickup("a"),
                Stack("a", "d"))
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
    fun ComplexGoal_2a() {
        val initialState = getInitialNetwork2()
        val goal_2a = BlocksState(pos = mapOf(Pair("b", "c"), Pair("a", "d"), Pair("c", table), Pair("d", table)),
                clear = mapOf(Pair("a", true), Pair("c", false), Pair("b", true), Pair("d", false)),
                holding = falseHolding)
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal_2a)))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_2_Solutions(),plan))
        assertTrue(isGoalStateSatisfied(khop.executePlan(plan), goal_2a))
    }

    @Test
    fun ComplexGoal_2b() {
        val initialState = getInitialNetwork2()
        val goal_2b = BlocksState(pos = mapOf(Pair("b", "c"), Pair("a", "d")))
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal_2b)))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_2_Solutions(),plan))
        assertTrue(isGoalStateSatisfied(khop.executePlan(plan), goal_2b))
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
        return BlocksState(pos = getMapWithAllKeys("1:12", "12:13", "13:blocks_world.getTable", "11:10", "10:5", "5:4",
                "4:14", "14:15", "15:blocks_world.getTable", "9:8", "8:7", "7:6", "6:blocks_world.getTable",
                "19:18", "18:17", "17:16", "16:3", "3:2", "2:blocks_world.getTable"),
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
                Unstack("11", "10"), Stack("11", "7"),
                Unstack("10", "5"), Putdown("10"),
                Unstack("5", "4"), Putdown("5"),
                Unstack("4", "14"), Putdown("4"),
                Pickup("9"), Stack("9", "4"),
                Pickup("8"), Stack("8", "9"),
                Unstack("14", "15"), Putdown("14"),
                Unstack("16", "3"), Stack("16", "11"),
                Unstack("3", "2"), Stack("3", "16"),
                Pickup("2"), Stack("2", "3"),
                Unstack("12", "13"), Stack("12", "2"),
                Pickup("13"), Stack("13", "8"),
                Pickup("15"), Stack("15", "13")))
        val alternative2 = PlanObj(false, mutableListOf(
                Unstack("1", "12"), Putdown("1"),
                Unstack("19", "18"), Putdown("19"),
                Unstack("18", "17"), Putdown("18"),
                Unstack("17", "16"), Putdown("17"),
                Unstack("9", "8"), Putdown("9"),
                Unstack("8", "7"), Putdown("8"),
                Unstack("11", "10"), Stack("11", "7"),
                Unstack("10", "5"), Putdown("10"),
                Unstack("5", "4"), Putdown("5"),
                Unstack("4", "14"), Putdown("4"),
                Unstack("14", "15"), Putdown("14"),
                Unstack("16", "3"), Stack("16", "11"),
                Unstack("3", "2"), Stack("3", "16"),
                Pickup("2"), Stack("2", "3"),
                Unstack("12", "13"), Stack("12", "2"),
                Pickup("9"), Stack("9", "4"),
                Pickup("8"), Stack("8", "9"),
                Pickup("13"), Stack("13", "8"),
                Pickup("15"), Stack("15", "13")))
        return listOf(alternative1,alternative2)
    }

    @Test
    fun ComplexGoal_3a() {
        val initialState = getInitialNetwork3()
        val goal_2b = BlocksState(pos = getMapWithAllKeys("15:13", "13:8", "8:9", "9:4", "4:blocks_world.getTable",
                "12:2", "2:3", "3:16", "16:11", "11:7", "7:6", "6:blocks_world.getTable"),
                clear = mapOf(Pair("17", true), Pair("15", true), Pair("12", true)))
        val initialNetwork = LinkedList<NetworkElement>(listOf(MoveBlocks(goal_2b)))
        val khop = KHOP(Domain(initialState, initialNetwork), 1)
        val plan = khop.findPlan()
        println("Out of planner, plan: " + plan)
        assertTrue(satisfiesOneOfTheSolutions(getComplexGoal_3_solutions(),plan))
        val finalState = khop.executePlan(plan)
        assertTrue(isGoalStateSatisfied(finalState, goal_2b))
    }


}