import khop.Plan
import khop.State
import org.junit.Assert.assertEquals

fun <ExtendedState: State<ExtendedState>> assertPlanActionStatusEquals(plan1: Plan<ExtendedState>, plan2: Plan<ExtendedState>) {
    assertEquals(plan1.failed, plan2.failed)
    assertEquals(plan1.actions, plan2.actions)
}