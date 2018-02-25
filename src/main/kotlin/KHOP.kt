import java.util.*

class KHOP<ExtendedState: State<ExtendedState>>(val domain: Domain<ExtendedState>, val verboseLevel: Int = 0) {

    fun findPlan() =
            TFD_Without_SideEffects(domain.initialState, domain.initialNetwork, PlanObj<ExtendedState>())

    fun executePlan(plan: Plan<ExtendedState>): ExtendedState {
        return executePlan(plan, domain.initialState)
    }

    /**
     * TFD Algorithm:
     * sigma: State-variable planning Domain
     * Sigma is defined as (S [Strips States], A
     * methods: set of methods
     * tasks: list of tasks
     * initialState: initial state
     */
    fun TFD_Without_SideEffects(state: ExtendedState, tasks: Deque<NetworkElement>, plan: Plan<ExtendedState>, depth: Int = 0): Plan<ExtendedState> {
        if (verboseLevel > 1)
            println("depth: $depth tasks: $tasks")
        if (tasks.isEmpty()) {
            if (verboseLevel > 2)
                println("depth $depth returns plan $plan")
            return plan //Empty plan
        }
        while(tasks.isNotEmpty()) {
            val task = tasks.pop()
            if (isPrimitive(task)) {
                    //The following code would be for the case that there are several operators per primitive task
//                val candidates = findApplicableOperator(task, state)
//                if (candidates.isEmpty()) {
//                    if (verboseLevel > 2)
//                        println("depth: $depth returns failure")
//                    plan.failed = true
//                    Exception("Cannot find a suitable task for: " + task::class.java.simpleName)
//                }
//                val operator = chooseOneTask(candidates)
                val operator = task as Operator<ExtendedState>
                if (verboseLevel > 2)
                    println("depth $depth action $operator")
                if (!operator.satisfiesPreconditions(state)) {
                    if (verboseLevel > 2)
                        println("depth: $depth does not satisfy preconditions")
                    plan.failed = true
                    return plan
                }
                val newState = operator.applyEffects(state)
                if (verboseLevel > 2)
                    println("depth: $depth new state: $newState")
                val nextPlan = TFD_Without_SideEffects(newState, tasks, plan, depth + 1)
                if (nextPlan.failed)
                    throw Exception("Plan failed: " + nextPlan.toString())
                nextPlan.actions.add(0, operator)
                if (verboseLevel > 2)
                    println("Added operator: $operator to nextplan: $nextPlan and returning in depth: $depth")
                return nextPlan
            }
            else {
                var candidates: List<Method<ExtendedState>> = emptyList()
                if (task is MethodGroup<*>) {
                    candidates = findApplicableMethod(task as MethodGroup<ExtendedState>, state)
                    if (candidates.isEmpty()) {
                        plan.failed = true
                        throw Exception("Cannot find suitable method for: " + task::class.java.simpleName)
                    }
                }
                else {
                    candidates = listOf(task as Method<ExtendedState>)
                }
                for (method in candidates) {
                    if (!method.satisfiesPreconditions(state))
                        continue
                    if (verboseLevel > 2)
                        println("depth: $depth method instance: $method")
                    val subTasks = method.decompose(state)
                    if (verboseLevel > 2)
                        println("depth: $depth new tasks: $subTasks")
                    val decomposedTasks = method.decompose(state)
                    if (verboseLevel > 2)
                        println("depth: $depth decomposed tasks: $decomposedTasks")
                    for (decomposedTask in decomposedTasks.reversed())
                        tasks.push(decomposedTask)
                    val nextPlan = TFD_Without_SideEffects(state, tasks, plan, depth + 1)
                    if (nextPlan.failed)
                        throw Exception("Plan failed: " + nextPlan.toString())
                    if (verboseLevel > 2)
                        println("Added method to plan: $method to nextplan: $nextPlan and returning in depth: $depth")
                    return nextPlan
                }
            }
        }
        if (verboseLevel > 2)
            println("depth: $depth returns failure")
//        throw Exception("No plan found!")
        return plan
    }

    fun isPrimitive(task: NetworkElement): Boolean {
        return task is Operator<*>
    }

    private fun chooseOneMethod(candidates: List<Method<ExtendedState>>): Method<ExtendedState> {
        return candidates.first()
    }

//    fun findApplicableOperator(task: Operator, state: State): List<Operator> {
//        //return domain.operators.filter { it.task.name == task.name && it.satisfiesPreconditions(state) }
//    }

    fun findApplicableMethod(methodGroup: MethodGroup<ExtendedState>, state: ExtendedState): List<Method<ExtendedState>> {
        return methodGroup.methods.filter { it.satisfiesPreconditions(state) }
    }

//    fun chooseOneTask(tasks: List<Operator>): Action {
//
//    }


}

interface NetworkElement {

}

interface Method<ExtendedState: State<ExtendedState>>: NetworkElement {
    fun satisfiesPreconditions(state: ExtendedState): Boolean
    fun decompose(state: ExtendedState): List<NetworkElement>
}

interface MethodGroup<ExtendedState: State<ExtendedState>>: NetworkElement {
    val methods: List<Method<ExtendedState>>
}

interface Operator<ExtendedState: State<ExtendedState>>: NetworkElement {
    fun satisfiesPreconditions(state: ExtendedState): Boolean
    fun applyEffects(state: ExtendedState): ExtendedState
}


interface Plan<ExtendedState: State<ExtendedState>> {
    var failed: Boolean
    val actions: MutableList<Operator<ExtendedState>>
}

data class PlanObj<ExtendedState: State<ExtendedState>>(override var failed: Boolean = false,
                                                        override val actions: MutableList<Operator<ExtendedState>> =
                                                        mutableListOf()): Plan<ExtendedState> {
}

fun <ExtendedState: State<ExtendedState>> executePlan(plan: Plan<ExtendedState>,
                                                      initialState: ExtendedState): ExtendedState {
    if (plan.actions.isEmpty())
        throw Exception("There is no plan to Execute! (Plan is empty)")
    var finalState = initialState.deepCopy()
    for (element in plan.actions) {
        if (!element.satisfiesPreconditions(finalState))
            throw Exception("Operator: $element could not be applied because it does not satisfy preconditions")
        finalState = element.applyEffects(finalState)
    }
    return finalState
}

//interface Action: Operator

class Domain<ExtendedState: State<ExtendedState>>(val initialState: ExtendedState, val initialNetwork: Deque<NetworkElement>)
//interface Method<ExtendedState: State<ExtendedState>>
//interface Task {
//    val name: String
//}
//class ATask(override val name: String): Task


abstract class State<ExtendedState: State<ExtendedState>> {
    abstract fun deepCopy(): ExtendedState
}