package blocksworld.methods

import blocksworld.BlocksState
import blocksworld.Status
import blocksworld.checkDictionaryEntries
import blocksworld.table

//Helper functions
fun isDone(block1: String, state: BlocksState, goal: BlocksState): Boolean {
    checkDictionaryEntries(state, block1)
    if (block1 == table)
        return true
    if (block1 in goal.pos && goal.pos[block1] != state.pos[block1])
        return false
    if (state.pos[block1] == table)
        return true
    return isDone(state.pos[block1]!!, state, goal)
}

fun status(block1: String, state: BlocksState, goal: BlocksState): Status {
    checkDictionaryEntries(state, block1)
    return if (isDone(block1, state, goal))
        Status.DONE
    else if (!state.clear[block1]!!)
        Status.INACCESSIBLE
    else if ((block1 !in goal.pos) || goal.pos[block1] == table)
        Status.MOVE_TO_TABLE
    else if (isDone(goal.pos[block1]!!, state, goal) && state.clear[goal.pos[block1]]!!)
        Status.MOVE_TO_BLOCK
    else
        Status.WAITING
}

fun allBlocks(state: BlocksState) = state.clear.keys