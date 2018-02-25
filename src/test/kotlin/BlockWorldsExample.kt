fun <K, V> Map<K, V>.withReplacedEntry(key: K, value: V): Map<K, V> {
    if (key !in this)
        throw Exception("Tried to replace a non existent key")
    return this - key + Pair(key, value)
}

fun <K, V> Map<K, V>.withReplacedEntries(vararg entries: Pair<K, V>): Map<K, V> {
    val result = this.toMutableMap()
    for (entry in entries)
        result[entry.first] = entry.second
    return result.toMap()
}

fun checkDictionaryEntries(state: BlocksState, vararg keys: String) {
    //I should be throwing the exception here but for some cases,
    //the code will run into this problem but still have an answer
    for (key in keys)
        if (!state.pos.containsKey(key) || !state.clear.containsKey(key))
            Exception("Key not found: $key in state: $state")
}

val table = "table"
val hand = "hand"
val falseHolding = "false"
val trueHolding = "true"
//val done = "done"
//val inaccessible = "inaccessible"
//val move_to_table = "move_to_table"
//val move_to_block = "move_to_block"
//val waiting = "waiting"

enum class Status {
    DONE, INACCESSIBLE, MOVE_TO_TABLE, MOVE_TO_BLOCK, WAITING
}

/*********************** STATE ***********************/

data class BlocksState(val pos: Map<String,String> = emptyMap(),
                       val clear: Map<String, Boolean> = emptyMap(),
                       val holding: String = "") : State<BlocksState>() {
    override fun deepCopy(): BlocksState {
        return copy()
    }
}

/*********************** OPERATORS ***********************/

data class Pickup(val block: String): Operator<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state, block)
        return state.pos[block] == table && state.clear[block] == true && state.holding == falseHolding
    }

    override fun applyEffects(state: BlocksState): BlocksState {
        val newPos = state.pos.withReplacedEntry(block, hand)
        val newClear = state.clear.withReplacedEntry(block, false)
        val newHolding = block
        return state.copy(pos = newPos, clear = newClear, holding = newHolding)
    }
}

data class Unstack(val blockB: String, val blockC: String): Operator<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state, blockB, blockC)
        return state.pos[blockB] == blockC && blockC != table && state.clear[blockB] == true && state.holding == falseHolding
    }

    override fun applyEffects(state: BlocksState): BlocksState {
        val newPos = state.pos.withReplacedEntry(blockB, hand)
        val newClear = state.clear.withReplacedEntries(Pair(blockB, false), Pair(blockC, true))
        val newHolding = blockB
        return state.copy(pos = newPos, clear = newClear, holding = newHolding)
    }
}

data class Putdown(val blockB: String): Operator<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state, blockB)
        return state.pos[blockB] == hand
    }

    override fun applyEffects(state: BlocksState): BlocksState {
        val newPos = state.pos.withReplacedEntry(blockB,table)
        val newClear = state.clear.withReplacedEntry(blockB, true)
        val newHolding = falseHolding
        return state.copy(pos = newPos, clear = newClear, holding = newHolding)
    }
}

data class Stack(val blockB: String, val blockC: String): Operator<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state,blockB,blockC)
        return state.pos[blockB] == hand && state.clear[blockC] == true
    }

    override fun applyEffects(state: BlocksState): BlocksState {
        val newPos = state.pos.withReplacedEntry(blockB, blockC)
        val newClear = state.clear.withReplacedEntries(Pair(blockB, true), Pair(blockC, false))
        val newHolding = falseHolding
        return state.copy(pos = newPos, clear = newClear, holding = newHolding)
    }
}

//class EmptyOperator : Operator<BlocksState> {
//    override fun satisfiesPreconditions(state: BlocksState): Boolean {
//        return true
//    }
//
//    override fun applyEffects(state: BlocksState): BlocksState {
//        return state
//    }
//}

/*********************** METHODS ***********************/

//def is_done(b1,state,goal):
//if b1 == 'table': return True
//if b1 in goal.pos and goal.pos[b1] != state.pos[b1]:
//return False
//if state.pos[b1] == 'table': return True
//return is_done(state.pos[b1],state,goal)

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
    checkDictionaryEntries(state,block1)
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

//for b1 in all_blocks(state):
//s = status(b1,state,goal)
//if s == 'move-to-table':
//return [('move_one',b1,'table'),('move_blocks',goal)]
//elif s == 'move-to-block':
//return [('move_one',b1,goal.pos[b1]), ('move_blocks',goal)]
//else:
//continue
//#
//# if we get here, no blocks can be moved to their final locations
//b1 = pyhop.find_if(lambda x: status(x,state,goal) == 'waiting', all_blocks(state))
//if b1 != None:
//return [('move_one',b1,'table'), ('move_blocks',goal)]
//#
//# if we get here, there are no blocks that need moving
//return []

data class MoveBlocks(val goal: BlocksState): Method<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        return true
    }

    override fun decompose(state: BlocksState): List<NetworkElement> {
        for (block in state.clear.keys) {
            val status = status(block,state,goal)
            if (status == Status.MOVE_TO_TABLE)
                return listOf(MoveOne(block, table), MoveBlocks(goal))
            else if (status == Status.MOVE_TO_BLOCK) {
                checkDictionaryEntries(goal, block)
                return listOf(MoveOne(block, goal.pos[block]!!), MoveBlocks(goal))
            }
            else
                continue
        }
//        val block = state.clear.keys.filter { status(it, state, goal) == Status.WAITING }
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
        return listOf(Get(block1), Put(block1, dest))
    }

}

data class Get(val block1: String): Method<BlocksState> {
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
            return listOf(Stack(block1,block2))
    }

}

fun isGoalStateSatisfied(comparingState: BlocksState, goalState: BlocksState): Boolean {
    var result = true
    if (goalState.holding.isNotBlank())
        result = result && comparingState.holding == goalState.holding
    for (pos in goalState.pos)
        result = result && comparingState.pos[pos.key] == pos.value
    for (clear in goalState.clear)
        result = result && comparingState.clear[clear.key] == clear.value
    return result
}