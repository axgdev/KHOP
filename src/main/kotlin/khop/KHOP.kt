package khop

import java.util.*

class KHOP<ExtendedState: State<ExtendedState>>(private val domain: Domain<ExtendedState>, private val verboseLevel: Int = 0) {

    fun findPlan(methodChooser: MethodChooserFunction<ExtendedState> = ::firstPlanWithLeastSteps): Plan<ExtendedState> {
        debugMessage("initialState: ${domain.initialState}" + System.lineSeparator() + "initialNetwork: ${domain.initialNetwork}, methodChooser: $methodChooser", 0)
        val plan = tfd(domain.initialState, domain.initialNetwork, PlanObj(), methodChooser = methodChooser)
        debugMessage("returnedPlan: $plan", 0)
        return plan
    }

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
    private fun tfd(state: ExtendedState, tasks: Deque<NetworkElement>, plan: Plan<ExtendedState>, depth: Int = 0,
                    methodChooser: MethodChooserFunction<ExtendedState> = ::firstPlanWithLeastSteps, additionalMessage: String = ""): Plan<ExtendedState> {
        if (additionalMessage.isNotBlank())
            debugMessage(additionalMessage, 2)
        debugMessage("depth: $depth tasks: $tasks",1)
        if (tasks.isEmpty()) {
            debugMessage("depth $depth returns plan $plan",2)
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
                debugMessage("depth $depth action $operator", 2)
                if (!operator.satisfiesPreconditions(state)) {
                    debugMessage("depth: $depth, operator: $operator does not satisfy preconditions", 2)
                    return failedPlan(plan)
                }
                plan.failed = false
                val newState = operator.applyEffects(state)
                debugMessage("depth: $depth new state: $newState",2)
                plan.actions.add(operator)
                debugMessage("Added operator: $operator to nextPlan: $plan",2)
                val nextPlan = tfd(newState, tasks, plan, depth + 1)
                if (nextPlan.failed)
                    throw Exception("khop.Plan failed: " + nextPlan.toString())
//                nextPlan.actions.add(0, operator)
//                debugMessage("Added operator: $operator to nextPlan: $nextPlan and returning in depth: $depth",2)
                return nextPlan
            }
            else {
                val chosenMethod: Method<ExtendedState>
                when (task) {
                    is Method<*> -> chosenMethod = task as Method<ExtendedState>
                    is MethodGroup<*> ->
                        try {
                            val methodStatePlan = chooseOneMethodPlan((task as MethodGroup<ExtendedState>).methods,
                                    state, tasks, plan, depth, methodChooser)
                            return methodStatePlan.plan
                        } catch (exception: Exception) {
                            //plan.failed = true
                            throw Exception("No applicable method found for: $task")
                        }
                    else -> throw Exception("Non supported task type: $task")
                }
                if (!chosenMethod.satisfiesPreconditions(state)) {
                    debugMessage("depth: $depth, method: $chosenMethod does not satisfy preconditions",2)
                    return failedPlan(plan)
                }
                debugMessage("depth: $depth method instance: $chosenMethod",2)
                val subTasks = chosenMethod.decompose(state)
                debugMessage("depth: $depth new tasks (decomposed): $subTasks",2)
                for (decomposedTask in subTasks.reversed())
                    tasks.push(decomposedTask)
                val nextPlan = tfd(state, tasks, plan, depth + 1)
                if (nextPlan.failed) {
                    debugMessage("depth: $depth, plan fails when trying to apply method: $chosenMethod", 2)
                    return nextPlan
                }
                    //throw Exception("khop.Plan failed: " + nextPlan.toString())
                debugMessage("Added method to plan: $chosenMethod to nextPlan: $nextPlan and returning in depth: $depth",2)
                return nextPlan
            }
        }
        if (verboseLevel > 2)
            println("depth: $depth returns failure")
//        throw Exception("No plan found!")
        return plan
    }

    private fun failedPlan(plan: Plan<ExtendedState>): Plan<ExtendedState> {
        plan.failed = true
        return plan
    }

    private fun isPrimitive(task: NetworkElement): Boolean {
        return task is Operator<*>
    }

    private fun chooseOneMethodPlan(candidates: List<Method<ExtendedState>>,
                                    state: ExtendedState,
                                    tasks: Deque<NetworkElement>,
                                    plan: Plan<ExtendedState>,
                                    depth: Int,
                                    methodChooser : MethodChooserFunction<ExtendedState>): MethodPlan<ExtendedState> {
        return methodChooser(candidates.map { MethodPlan(it,
                tfd(state,LinkedList(listOf(it) + tasks),plan.createCopy(),depth + 1, methodChooser, "Trying out method: $it")) })
    }

//    fun findApplicableOperator(task: khop.Operator, state: khop.State): List<khop.Operator> {
//        //return domain.operators.filter { it.task.name == task.name && it.satisfiesPreconditions(state) }
//    }

    private fun findApplicableMethod(methodGroup: MethodGroup<ExtendedState>, state: ExtendedState): List<Method<ExtendedState>> {
        return methodGroup.methods.filter { it.satisfiesPreconditions(state) }
    }

//    fun chooseOneTask(tasks: List<khop.Operator>): Action {
//
//    }

    companion object {
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
    }

    fun firstPlanWithLeastSteps(methodsStatesPlans: List<MethodPlan<ExtendedState>>): MethodPlan<ExtendedState> {
        val filteredAndSorted = methodsStatesPlans.filter { !it.plan.failed }.sortedBy { it.plan.actions.size }
        if (filteredAndSorted.isEmpty())
            throw Exception("No applicable method found!")
        val chosenMethodPlan = filteredAndSorted.first()
        debugMessage("Chosen MethodPlan: $chosenMethodPlan",2)
        return chosenMethodPlan
    }

    fun debugMessage(message: String, minVerboseLevel: Int) {
        if (verboseLevel > minVerboseLevel)
            println(message)
    }

}


