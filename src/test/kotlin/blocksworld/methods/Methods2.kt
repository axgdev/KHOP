package blocksworld.methods

import blocksworld.operators.*
import blocksworld.*
import khop.Method
import khop.MethodGroup
import khop.NetworkElement

data class GetByUnstack(private val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(UnstackM(block))
    }
}

data class GetByPickup(private val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(PickupM(block))
    }
}

data class PickupM(private val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(Pickup(block))
    }
}

data class UnstackM(private val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        checkDictionaryEntries(state, block)
        return listOf(Unstack(block, state.pos[block]!!))
    }
}


data class GetMG(private val block1: String): MethodGroup<BlocksState> {
    override val methods: List<Method<BlocksState>> = getTheMethods()

    private fun getTheMethods(): List<Method<BlocksState>> {
        return listOf(GetByPickup(block1), GetByUnstack(block1))
    }
}

