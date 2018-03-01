package khop

import java.util.*

interface NetworkElement

interface Method<ExtendedState: State<ExtendedState>>: NetworkElement {
    fun satisfiesPreconditions(state: ExtendedState): Boolean
    fun decompose(state: ExtendedState): List<NetworkElement>
}

data class SingleOpMethod<ExtendedState: State<ExtendedState>>(private val element: Operator<ExtendedState>): Method<ExtendedState> {
    override fun satisfiesPreconditions(state: ExtendedState): Boolean {
        return element.satisfiesPreconditions(state)
    }

    override fun decompose(state: ExtendedState): List<NetworkElement> {
        return listOf(element)
    }
}

class PassthroughOperator<ExtendedState: State<ExtendedState>>: Operator<ExtendedState> {
    override fun satisfiesPreconditions(state: ExtendedState) = true
    override fun applyEffects(state: ExtendedState) = state
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
    fun createCopy(): Plan<ExtendedState>
}

data class PlanObj<ExtendedState: State<ExtendedState>>(override var failed: Boolean = false,
                                                             override val actions: MutableList<Operator<ExtendedState>> =
                                                        mutableListOf()): Plan<ExtendedState> {
    override fun createCopy(): Plan<ExtendedState> {
        return this.copy(actions = actions.toMutableList())
    }
}

data class Domain<ExtendedState: State<ExtendedState>>(val initialState: ExtendedState, val initialNetwork: Deque<NetworkElement>)

abstract class State<ExtendedState: State<ExtendedState>> {
    abstract fun deepCopy(): ExtendedState
}

data class MethodPlan<ExtendedState: State<ExtendedState>>(val method: Method<ExtendedState>, val plan: Plan<ExtendedState>)

typealias MethodChooserFunction<ExtendedState> = (methodsStatesPlans: List<MethodPlan<ExtendedState>>) -> MethodPlan<ExtendedState>