package blocks_world

import khop.State

data class BlocksState(val pos: Map<String,String> = emptyMap(),
                       val clear: Map<String, Boolean> = emptyMap(),
                       val holding: String = "") : State<BlocksState>() {
    override fun deepCopy(): BlocksState {
        return copy()
    }
}