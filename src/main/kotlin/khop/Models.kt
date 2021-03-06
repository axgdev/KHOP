package khop

import java.util.*

interface NetworkElement<ExtendedState: State<ExtendedState>>

interface Method<ExtendedState: State<ExtendedState>>: NetworkElement<ExtendedState> {
    fun satisfiesPreconditions(state: ExtendedState): Boolean
    fun decompose(state: ExtendedState): List<NetworkElement<ExtendedState>>
}

data class SingleOpMethod<ExtendedState: State<ExtendedState>>(private val element: Operator<ExtendedState>): Method<ExtendedState> {
    override fun satisfiesPreconditions(state: ExtendedState): Boolean {
        return element.satisfiesPreconditions(state)
    }

    override fun decompose(state: ExtendedState): List<NetworkElement<ExtendedState>> {
        return listOf(element)
    }
}

class PassthroughOperator<ExtendedState: State<ExtendedState>>: Operator<ExtendedState> {
    override fun satisfiesPreconditions(state: ExtendedState) = true
    override fun applyEffects(state: ExtendedState) = state
}

interface MethodGroup<ExtendedState: State<ExtendedState>>: NetworkElement<ExtendedState> {
    val methods: List<Method<ExtendedState>>
}

class CompoundTask<ExtendedState: State<ExtendedState>>(
        val name: String, override val methods: List<Method<ExtendedState>>): MethodGroup<ExtendedState>

interface Operator<ExtendedState: State<ExtendedState>>: NetworkElement<ExtendedState> {
    fun satisfiesPreconditions(state: ExtendedState): Boolean
    fun applyEffects(state: ExtendedState): ExtendedState
}

interface OperatorGroup<ExtendedState: State<ExtendedState>>: NetworkElement<ExtendedState> {
    val operators: List<Operator<ExtendedState>>
}

class PrimitiveTask<ExtendedState: State<ExtendedState>>(
        val name: String, override val operators: List<Operator<ExtendedState>>): OperatorGroup<ExtendedState>

interface Plan<ExtendedState: State<ExtendedState>> {
    val failed: Boolean
    val actions: List<Operator<ExtendedState>>
    val state: ExtendedState?
    fun createCopy(): Plan<ExtendedState>
}

class PlanObj<ExtendedState: State<ExtendedState>>(override val failed: Boolean = false,
                                                        override val actions: List<Operator<ExtendedState>> = listOf(),
                                                        override val state: ExtendedState? = null): Plan<ExtendedState> {
    override fun createCopy(): PlanObj<ExtendedState> {
        return PlanObj(failed, actions.toList(), state?.deepCopy())
    }
}

data class Domain<ExtendedState: State<ExtendedState>>(val initialState: ExtendedState, val initialNetwork: Deque<NetworkElement<ExtendedState>>)

abstract class State<ExtendedState: State<ExtendedState>> {
    abstract fun deepCopy(): ExtendedState
}

data class MyStack<ExtendedState: State<ExtendedState>>(val currentPlan: PlanObj<ExtendedState>, val tasks: Deque<NetworkElement<ExtendedState>>)
