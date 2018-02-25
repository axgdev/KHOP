package blocks_world.methods

import blocks_world.BlocksState
import blocks_world.Status
import blocks_world.checkDictionaryEntries
import blocks_world.table

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
    if (isDone(block1, state, goal))
        return Status.DONE
    else if (!state.clear[block1]!!)
        return Status.INACCESSIBLE
    else if ((block1 !in goal.pos) || goal.pos[block1] == table)
        return Status.MOVE_TO_TABLE
    else if (isDone(goal.pos[block1]!!, state, goal) && state.clear[goal.pos[block1]]!!)
        return Status.MOVE_TO_BLOCK
    else
        return Status.WAITING
}

fun allBlocks(state: BlocksState) = state.clear.keys