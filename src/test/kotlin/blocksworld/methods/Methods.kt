package blocksworld.methods

import blocksworld.operators.*
import blocksworld.*
import khop.Method
import khop.NetworkElement

data class MoveBlocks(private val goal: BlocksState): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return true
    }

    override fun decompose(state: BlocksState): List<NetworkElement<BlocksState>> {
        for (block in allBlocks(state)) {
            val status = status(block, state, goal)
            if (status == Status.MOVE_TO_TABLE)
                return listOf(MoveOne(block, table), MoveBlocks(goal))
            else if (status == Status.MOVE_TO_BLOCK) {
                checkDictionaryEntries(goal, block)
                return listOf(MoveOne(block, goal.pos[block]!!), MoveBlocks(goal))
            }
        }
        val foundBlock = allBlocks(state).firstOrNull { status(it, state, goal) == Status.WAITING } ?: ""
        if (foundBlock.isNotEmpty())
            return listOf(MoveOne(foundBlock, table), MoveBlocks(goal))
        return emptyList()
    }

}

data class MoveOne(private val block1: String, private val dest: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return true
    }

    override fun decompose(state: BlocksState): List<NetworkElement<BlocksState>> {
        return listOf(GetM(block1), Put(block1, dest))
    }

}

data class GetM(private val block1: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state, block1)
        return state.clear[block1] == true
    }

    override fun decompose(state: BlocksState): List<NetworkElement<BlocksState>> {
        return if (state.pos[block1] == table)
            listOf(Pickup(block1))
        else
            listOf(Unstack(block1, state.pos[block1]!!))
    }
}

data class Put(private val block1: String, private val block2: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.holding == block1
    }

    override fun decompose(state: BlocksState): List<NetworkElement<BlocksState>> {
        return if (block2 == table)
            listOf(PutDown(block1))
        else
            listOf(StackOp(block1, block2))
    }
}