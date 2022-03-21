package com.visma.of.rp.routeevaluator.evaluation.constraints;

import com.visma.of.rp.routeevaluator.interfaces.IConstraintIntraRoute;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(MockitoJUnitRunner.class)
public class ConstraintsIntraRouteHandlerTest {

    @Mock
    public IConstraintIntraRoute constraint1;

    @Mock
    public IConstraintIntraRoute constraint2;

    @Mock
    public IConstraintIntraRoute constraint3;

    @Mock
    public IConstraintIntraRoute constraint4;

    public ConstraintsIntraRouteHandler oldHandler;
    public Map<String, IConstraintIntraRoute> constraints;

    @Before
    public void setup() {
        oldHandler = new ConstraintsIntraRouteHandler();
        constraints = Map.of(
                "constraint1", this.constraint1,
                "constraint2", constraint2,
                "constraint3", constraint3,
                "constraint4", constraint4
        );

        for (var entry : constraints.entrySet()) {
            oldHandler.addConstraint(entry.getKey(), entry.getValue());
        }
    }


    @Test
    public void copyConstructorShouldHaveAllConstraintsActive() {
        var handlerCopy = new ConstraintsIntraRouteHandler(oldHandler);
        Set<String> foundActiveConstraints = handlerCopy.getActiveConstraintsMap().keySet();
        Set<String> actualActive = constraints.keySet();

        MatcherAssert.assertThat(foundActiveConstraints, is(actualActive));
    }

    @Test
    public void copyConstructorSomeConstraintsDeactivated() {
        oldHandler.deactivateConstraint("constraint4");
        oldHandler.deactivateConstraint("constraint1");

        var handlerCopy = new ConstraintsIntraRouteHandler(oldHandler);

        Set<String> foundActiveConstraints = handlerCopy.getActiveConstraintsMap().keySet();
        Set<String> actualActive = Set.of("constraint2", "constraint3");
        MatcherAssert.assertThat(foundActiveConstraints, is(actualActive));

        Set<String> foundInactive = handlerCopy.getInactiveConstraintsMap().keySet();
        Set<String> actualInactive = Set.of("constraint1", "constraint4");
        MatcherAssert.assertThat(foundInactive, is(actualInactive));
    }

    @Test
    public void updateActivateInactiveConstraint() {
        oldHandler.deactivateConstraint("constraint4");
        oldHandler.deactivateConstraint("constraint1");

        var handlerCopy = new ConstraintsIntraRouteHandler(oldHandler);

        oldHandler.activateConstraint("constraint4");
        MatcherAssert.assertThat(handlerCopy.getActiveConstraintsMap().keySet(), CoreMatchers.hasItem(not("constraint4")));

        handlerCopy.update(oldHandler);
        MatcherAssert.assertThat(handlerCopy.getActiveConstraintsMap().keySet(), CoreMatchers.hasItem("constraint4"));
    }

    @Test
    public void updateDisableActiveConstraint() {
        oldHandler.deactivateConstraint("constraint4");
        oldHandler.deactivateConstraint("constraint1");

        var handlerCopy = new ConstraintsIntraRouteHandler(oldHandler);

        oldHandler.deactivateConstraint("constraint2");
        MatcherAssert.assertThat(handlerCopy.getInactiveConstraintsMap().keySet(), CoreMatchers.hasItem(not("constraint2")));

        handlerCopy.update(oldHandler);
        MatcherAssert.assertThat(handlerCopy.getInactiveConstraintsMap().keySet(), CoreMatchers.hasItem("constraint2"));
    }
}