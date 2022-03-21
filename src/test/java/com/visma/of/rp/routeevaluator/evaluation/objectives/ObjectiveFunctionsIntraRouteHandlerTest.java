package com.visma.of.rp.routeevaluator.evaluation.objectives;

import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionIntraRoute;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ObjectiveFunctionsIntraRouteHandlerTest {

    @Mock
    public IObjectiveFunctionIntraRoute objective1;

    @Mock
    public IObjectiveFunctionIntraRoute objective2;

    @Mock
    public IObjectiveFunctionIntraRoute objective3;

    @Mock
    public IObjectiveFunctionIntraRoute objective4;

    public ObjectiveFunctionsIntraRouteHandler oldHandler;
    public Map<String, IObjectiveFunctionIntraRoute> objectives;

    @Before
    public void setup() {
        oldHandler = new ObjectiveFunctionsIntraRouteHandler();
        objectives = Map.of(
                "objective1", objective1,
                "objective2", objective2,
                "objective3", objective3,
                "objective4", objective4
        );

        for (var entry : objectives.entrySet()) {
            oldHandler.addIntraShiftObjectiveFunction(entry.getKey(), 1.0, entry.getValue());
        }
    }


    @Test
    public void copyConstructorShouldHaveAllConstraintsActive() {
        var handlerCopy = new ObjectiveFunctionsIntraRouteHandler(oldHandler);

        Set<String> foundActive = handlerCopy.getActiveObjectiveFunctions().keySet();
        for (var entry : oldHandler.getActiveObjectiveFunctions().entrySet()) {
            MatcherAssert.assertThat(foundActive, hasItem(entry.getKey()));
            Assert.assertEquals(entry.getValue().getWeight(), handlerCopy.getWeightObjectivePair(entry.getKey()).getWeight(), 1e-4);
            Assert.assertEquals(entry.getValue().getWeight(), handlerCopy.getWeightObjectivePair(entry.getKey()).getWeight(), 1e-4);
        }
    }

    @Test
    public void copyConstructorSomeConstraintsDeactivated() {
        oldHandler.deactivateObjective("objective4");
        oldHandler.deactivateObjective("objective1");

        var handlerCopy = new ObjectiveFunctionsIntraRouteHandler(oldHandler);

        Set<String> foundActive = handlerCopy.getActiveObjectiveFunctions().keySet();
        Set<String> actualActive = Set.of("objective2", "objective3");
        MatcherAssert.assertThat(foundActive, is(actualActive));

        Set<String> foundInactive = handlerCopy.getInactiveObjectiveFunctions().keySet();
        Set<String> actualInactive = Set.of("objective1", "objective4");
        MatcherAssert.assertThat(foundInactive, is(actualInactive));
    }

    @Test
    public void updateActivateInactiveConstraint() {
        oldHandler.deactivateObjective("objective4");
        oldHandler.deactivateObjective("objective1");

        var handlerCopy = new ObjectiveFunctionsIntraRouteHandler(oldHandler);

        oldHandler.activateObjective("objective4");
        MatcherAssert.assertThat(handlerCopy.getActiveObjectiveFunctions().keySet(), CoreMatchers.hasItem(not("objective4")));

        handlerCopy.update(oldHandler);
        MatcherAssert.assertThat(handlerCopy.getActiveObjectiveFunctions().keySet(), CoreMatchers.hasItem("objective4"));
    }

    @Test
    public void updateDisableActiveConstraint() {
        oldHandler.deactivateObjective("objective4");
        oldHandler.deactivateObjective("objective1");

        var handlerCopy = new ObjectiveFunctionsIntraRouteHandler(oldHandler);

        oldHandler.deactivateObjective("objective2");
        MatcherAssert.assertThat(handlerCopy.getInactiveObjectiveFunctions().keySet(), CoreMatchers.hasItem(not("objective2")));

        handlerCopy.update(oldHandler);
        MatcherAssert.assertThat(handlerCopy.getInactiveObjectiveFunctions().keySet(), CoreMatchers.hasItem("objective2"));
    }

}