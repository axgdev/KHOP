package khop

import java.util.*

class KHOP<ExtendedState: State<ExtendedState>>
    @JvmOverloads constructor(private val domain: Domain<ExtendedState>, private val verboseLevel: Int = 0) {

    fun findPlan(): PlanObj<ExtendedState> {
        debugMessage(System.lineSeparator() + "initialState: ${domain.initialState}" + System.lineSeparator() + "initialNetwork: ${domain.initialNetwork}", 0)
        val plan = iterativeExecuteTaskNetwork(domain.initialState, domain.initialNetwork)
        debugMessage("returnedPlan: $plan", 0)
        return plan
    }

    @JvmOverloads fun getAllPlans(fromPlans: List<PlanObj<ExtendedState>> = emptyList()): List<PlanObj<ExtendedState>> {
        return iterativeGetAllPlansFromNetwork(domain.initialState, domain.initialNetwork, fromPlans)
    }

    fun executePlan(plan: PlanObj<ExtendedState>): ExtendedState {
        return executePlan(plan, domain.initialState)
    }

    private fun iterativeExecuteTaskNetwork(initialState: ExtendedState, initialNetwork: Deque<NetworkElement<ExtendedState>>): PlanObj<ExtendedState> {
        val initialPlan = PlanObj<ExtendedState>(state = initialState)

        val completeStack: Deque<MyStack<ExtendedState>> = ArrayDeque()
        completeStack.push(MyStack(initialPlan, initialNetwork))

        var updatedPlan = initialPlan
        while (completeStack.isNotEmpty()) {
            updatedPlan = getPlanFromNetwork(completeStack)
            if (!updatedPlan.failed)
                return updatedPlan
        }
        return updatedPlan
    }

    private fun iterativeGetAllPlansFromNetwork(initialState: ExtendedState,
                                                initialNetwork: Deque<NetworkElement<ExtendedState>>,
                                                fromPlans: List<PlanObj<ExtendedState>>): List<PlanObj<ExtendedState>> {
        val initialPlan = PlanObj(state = initialState)

        val completeStack: Deque<MyStack<ExtendedState>> = ArrayDeque()
        if (fromPlans.isEmpty())
            completeStack.push(MyStack(initialPlan, ArrayDeque(initialNetwork)))
        else
            fromPlans.reversed().forEach { completeStack.push(MyStack(it, ArrayDeque(initialNetwork))) }

        var updatedPlan: PlanObj<ExtendedState>
        var allPlans: List<PlanObj<ExtendedState>> = emptyList()
        while (completeStack.isNotEmpty()) {
            updatedPlan = getPlanFromNetwork(completeStack)
            if (!updatedPlan.failed)
                allPlans += updatedPlan
        }
        return allPlans
    }

    private fun getPlanFromNetwork(completeStack: Deque<MyStack<ExtendedState>>): PlanObj<ExtendedState> {
        val poppedElement = completeStack.pop() ?: throw Exception("Stack element null!")
        var currentPlan = poppedElement.currentPlan
        while (poppedElement.tasks.isNotEmpty()) {
            val poppedTask = poppedElement.tasks.pop() ?: throw Exception("poppedElement was null")
            val state = currentPlan.state ?: throw Exception("State is null!")
            when (poppedTask) {
                is Operator<ExtendedState> -> {
                    if (!poppedTask.satisfiesPreconditions(state))
                        return getFailedEmptyPlan(poppedTask)
                    val newState = poppedTask.applyEffects(state)
                    currentPlan = PlanObj(false, currentPlan.actions + poppedTask, newState)
                    debugMessage("Added operator: $poppedTask to plan: $currentPlan",2)
                }
                is Method<ExtendedState> -> {
                    if (!poppedTask.satisfiesPreconditions(state))
                        return getFailedEmptyPlan(poppedTask)
                    val decomposedTasks = poppedTask.decompose(state)
                    decomposedTasks.reversed().forEach { poppedElement.tasks.push(it) }
                    debugMessage("Decomposing method: $poppedTask",2)
                }
                is OperatorGroup<ExtendedState>, is MethodGroup<ExtendedState> -> {
                    //Only decide between methods or operators that satisfy preconditions
                    if (!processOfElementsSucceeds(poppedTask, state, poppedElement, completeStack, currentPlan))
                        return getFailedEmptyPlan(poppedTask)
                }
                else -> {
                    throw Exception("Unknown type of Network element")
                }
            }
        }
        return currentPlan
    }

    private fun processOfElementsSucceeds(poppedTask: NetworkElement<ExtendedState>, state: ExtendedState,
                                          poppedElement: MyStack<ExtendedState>, completeStack: Deque<MyStack<ExtendedState>>,
                                          currentPlan: PlanObj<ExtendedState>): Boolean {
        debugMessage("Finding out alternatives for: $poppedTask",2)
        var filteredElements = listOf<NetworkElement<ExtendedState>>()
        when (poppedTask) {
            is OperatorGroup<ExtendedState> -> filteredElements = poppedTask.operators.filter { it.satisfiesPreconditions(state) }
            is MethodGroup<ExtendedState> -> filteredElements = poppedTask.methods.filter { it.satisfiesPreconditions(state) }
        }
        //Here is a choosing Point, we will deal with it later on, for now choose first
        if (filteredElements.isEmpty()) {
            debugMessage("Alternative was not found for: $poppedTask",2)
            return false
        }
        debugMessage("Found out applicable alternatives: $filteredElements",2)

        if (filteredElements.size > 1) {
            filteredElements.drop(1).reversed().forEach {
                val updatedStack = ArrayDeque(poppedElement.tasks)
                updatedStack.push(it)
                completeStack.push(MyStack(currentPlan.createCopy(), updatedStack))
            }
        }
        poppedElement.tasks.push(filteredElements.first())
        debugMessage("Trying out alternative element: ${filteredElements.first()}", 1)
        return true
    }

    private fun getFailedEmptyPlan(poppedTask: NetworkElement<ExtendedState>): PlanObj<ExtendedState> {
        debugMessage("Task failed: $poppedTask", 5)
        return PlanObj<ExtendedState>(true)
    }

    private fun debugMessage(message: String, minVerboseLevel: Int) {
        if (verboseLevel > minVerboseLevel)
            println(message)
    }

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
}

