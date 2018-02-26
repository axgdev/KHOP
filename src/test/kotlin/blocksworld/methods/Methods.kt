package blocksworld.methods

import blocksworld.operators.*
import blocksworld.*
import khop.Method
import khop.NetworkElement

data class MoveBlocks(val goal: BlocksState): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return true
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        for (block in state.clear.keys) {
            val status = status(block, state, goal)
            if (status == Status.MOVE_TO_TABLE)
                return listOf(MoveOne(block, table), MoveBlocks(goal))
            else if (status == Status.MOVE_TO_BLOCK) {
                checkDictionaryEntries(goal, block)
                return listOf(MoveOne(block, goal.pos[block]!!), MoveBlocks(goal))
            }
            else
                continue
        }
//        val block = state.clear.keys.filter { blocksworld.methods.status(it, state, goal) == blocksworld.Status.WAITING }
        var foundBlock = ""
        for (block in state.clear.keys)
            if (status(block, state, goal) == Status.WAITING) {
                foundBlock = block
                break
            }
        if (foundBlock.isNotEmpty())
            return listOf(MoveOne(foundBlock, table), MoveBlocks(goal))
        return emptyList()
    }

}

data class MoveOne(val block1: String, val dest: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return true
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        return listOf(Get_m(block1), Put(block1, dest))
    }

}

data class Get_m(val block1: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state, block1)
        return state.clear[block1] == true
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        if (state.pos[block1] == table)
            return listOf(Pickup(block1))
        else
            return listOf(Unstack(block1, state.pos[block1]!!))
    }
}

data class Put(val block1: String, val block2: String): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return state.holding == block1
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        if (block2 == table)
            return listOf(Putdown(block1))
        else
            return listOf(StackOp(block1, block2))
    }
}