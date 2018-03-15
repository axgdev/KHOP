import khop.Plan
import khop.State
import org.junit.Assert.assertTrue

fun <ExtendedState: State<ExtendedState>> assertPlanActionStatusEquals(plan1: Plan<ExtendedState>, plan2: Plan<ExtendedState>) {
    assertTrue(plan1.customEquals(plan2))
}

fun <ExtendedState: State<ExtendedState>> Plan<ExtendedState>.customEquals(other: Plan<ExtendedState>): Boolean {
    return failed == other.failed && actions == other.actions
}