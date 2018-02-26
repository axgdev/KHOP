package khop

import java.util.*

class KHOP<ExtendedState: State<ExtendedState>>(val domain: Domain<ExtendedState>, val verboseLevel: Int = 0) {

    fun findPlan() =
            tfd(domain.initialState, domain.initialNetwork, PlanObj())

    fun executePlan(plan: Plan<ExtendedState>): ExtendedState {
        return executePlan(plan, domain.initialState)
    }

    /**
     * TFD Algorithm:
     * sigma: khop.State-variable planning khop.Domain
     * Sigma is defined as (S [Strips States], A
     * methods: set of methods
     * tasks: list of tasks
     * initialState: initial state
     */
    fun tfd(state: ExtendedState, tasks: Deque<NetworkElement>, plan: Plan<ExtendedState>, depth: Int = 0): Plan<ExtendedState> {
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
                val nextPlan = tfd(newState, tasks, plan, depth + 1)
                if (nextPlan.failed)
                    throw Exception("khop.Plan failed: " + nextPlan.toString())
                nextPlan.actions.add(0, operator)
                if (verboseLevel > 2)
                    println("Added operator: $operator to nextPlan: $nextPlan and returning in depth: $depth")
                return nextPlan
            }
            else {
                var candidates: List<Method<ExtendedState>>
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
                    val nextPlan = tfd(state, tasks, plan, depth + 1)
                    if (nextPlan.failed)
                        throw Exception("khop.Plan failed: " + nextPlan.toString())
                    if (verboseLevel > 2)
                        println("Added method to plan: $method to nextPlan: $nextPlan and returning in depth: $depth")
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

//    fun findApplicableOperator(task: khop.Operator, state: khop.State): List<khop.Operator> {
//        //return domain.operators.filter { it.task.name == task.name && it.satisfiesPreconditions(state) }
//    }

    fun findApplicableMethod(methodGroup: MethodGroup<ExtendedState>, state: ExtendedState): List<Method<ExtendedState>> {
        return methodGroup.methods.filter { it.satisfiesPreconditions(state) }
    }

//    fun chooseOneTask(tasks: List<khop.Operator>): Action {
//
//    }


}

fun <ExtendedState: State<ExtendedState>> executePlan(plan: Plan<ExtendedState>,
                                                           initialState: ExtendedState): ExtendedState {
    if (plan.actions.isEmpty())
        throw Exception("There is no plan to Execute! (khop.Plan is empty)")
    var finalState = initialState.deepCopy()
    for (element in plan.actions) {
        if (!element.satisfiesPreconditions(finalState))
            throw Exception("khop.Operator: $element could not be applied because it does not satisfy preconditions")
        finalState = element.applyEffects(finalState)
    }
    return finalState
}


