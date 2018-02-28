package blocksworld.methods

import blocksworld.operators.*
import blocksworld.*
import khop.Method
import khop.MethodGroup
import khop.NetworkElement

data class MoveBlocks2(private val goal: BlocksState): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return true
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        for (block in allBlocks(state)) {
            val status = status(block, state, goal)
            if (status == Status.MOVE_TO_TABLE)
                return listOf(MoveOne2(block, table), MoveBlocks(goal))
            else if (status == Status.MOVE_TO_BLOCK) {
                checkDictionaryEntries(goal, block)
                return listOf(MoveOne2(block, goal.pos[block]!!), MoveBlocks(goal))
            }
        }
        val foundBlock = allBlocks(state).firstOrNull { status(it, state, goal) == Status.WAITING } ?: ""
        if (foundBlock.isNotEmpty())
            return listOf(MoveOne2(foundBlock, table), MoveBlocks(goal))
        return emptyList()
    }

}

data class MoveOne2(private val block1: String, private val dest: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return true
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(GetMG(block1), Put(block1, dest))
    }

}

data class GetByUnstack(val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(UnstackM(block))
    }
}

data class GetByPickup(val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(PickupM(block))
    }
}

data class PickupM(val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(Pickup(block))
    }
}

data class UnstackM(val block: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.clear[block]!!
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        checkDictionaryEntries(state, block)
        return listOf(Unstack(block, state.pos[block]!!))
    }
}


data class GetMG(val block1: String): MethodGroup<BlocksState> {
    override val methods: List<Method<BlocksState>> = getTheMethods()

    private fun getTheMethods(): List<Method<BlocksState>> {
        return listOf(GetByPickup(block1), GetByUnstack(block1))
    }
}

