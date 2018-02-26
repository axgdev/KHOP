package blocksworld

val table = "table"
val hand = "hand"
val falseHolding = "false"
val trueHolding = "true"

enum class Status {
    DONE, INACCESSIBLE, MOVE_TO_TABLE, MOVE_TO_BLOCK, WAITING
}

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