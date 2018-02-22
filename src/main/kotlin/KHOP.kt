import java.util.*

class KHOP<ExtendedState: State<ExtendedState>>(val domain: Domain<ExtendedState>, val verboseLevel: Int = 0) {

    fun findPlan() =
            TFD_Without_SideEffects(domain.initialState, domain.initialNetwork, PlanObj<ExtendedState>())

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
                val newState = operator.applyEffects(state)
                if (verboseLevel > 2)
                    println("depth: $depth new state: $newState")
                val nextPlan = TFD_Without_SideEffects(newState, tasks, plan, depth + 1)
                if (nextPlan.failed)
                    Exception("Plan failed: " + nextPlan.toString())
                nextPlan.actions.add(operator)
                if (verboseLevel > 2)
                    println("Added operator: $operator to nextplan: $nextPlan and returning in depth: $depth")
                return nextPlan
            }
            else {
                val method: Method<ExtendedState>
                if (task is MethodGroup<*>) {
                    val candidates = findApplicableMethod(task as MethodGroup<ExtendedState>, state)
                    if (candidates.isEmpty()) {
                        plan.failed = true
                        Exception("Cannot find suitable method for: " + task::class.java.simpleName)
                    }
                    method = chooseOneMethod(candidates)
                }
                else {
                    method = task as Method<ExtendedState>
                }
                if (verboseLevel > 2)
                    println("depth: $depth method instance: $method")
                val subTasks = method.decompose()
                if (verboseLevel > 2)
                    println("depth: $depth new tasks: $subTasks")
                for (decomposedTask in method.decompose())
                    tasks.push(decomposedTask)
                return TFD_Without_SideEffects(state, tasks, plan, depth + 1)
            }
        }
        if (verboseLevel > 2)
            println("depth: $depth returns failure")
        Exception("No plan found!")
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
    fun decompose(): List<NetworkElement>
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