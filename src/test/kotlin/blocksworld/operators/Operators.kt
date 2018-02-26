package blocksworld.operators

import khop.Operator
import blocksworld.*

data class PutDown(private val blockB: String): Operator<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state, blockB)
        return state.pos[blockB] == hand
    }

    override fun applyEffects(state: BlocksState): BlocksState {
        val newPos = state.pos.withReplacedEntry(blockB, table)
        val newClear = state.clear.withReplacedEntry(blockB, true)
        val newHolding = falseHolding
        return state.copy(pos = newPos, clear = newClear, holding = newHolding)
    }
}

data class StackOp(private val blockB: String, private val blockC: String): Operator<BlocksState> {
    override fun satisfiesPreconditions(state: BlocksState): Boolean {
        checkDictionaryEntries(state, blockB, blockC)
        return state.pos[blockB] == hand && state.clear[blockC] == true
    }

    override fun applyEffects(state: BlocksState): BlocksState {
        val newPos = state.pos.withReplacedEntry(blockB, blockC)
        val newClear = state.clear.withReplacedEntries(Pair(blockB, true), Pair(blockC, false))
        val newHolding = falseHolding
        return state.copy(pos = newPos, clear = newClear, holding = newHolding)
    }
}

/*********************** OPERATORS ***********************/

data class Pickup(private val block: String): Operator<BlocksState> {
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

data class Unstack(private val blockB: String, private val blockC: String): Operator<BlocksState> {
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