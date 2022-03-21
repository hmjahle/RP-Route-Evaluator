package com.visma.of.rp.routeevaluator.solver;

import com.visma.of.rp.routeevaluator.interfaces.IConstraintIntraRoute;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IObjectiveFunctionIntraRoute;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class RouteEvaluatorTest extends JUnitTestAbstract {

    public RouteEvaluator<ITask> routeEvaluator;

    @Mock
    public IConstraintIntraRoute constraint1;

    @Mock
    public IConstraintIntraRoute constraint2;

    @Mock
    public IConstraintIntraRoute constraint3;

    @Mock
    public IConstraintIntraRoute constraint4;

    @Mock
    public IObjectiveFunctionIntraRoute objective1;

    @Mock
    public IObjectiveFunctionIntraRoute objective2;

    @Mock
    public IObjectiveFunctionIntraRoute objective3;

    @Mock
    public IObjectiveFunctionIntraRoute objective4;

    public Map<String, IObjectiveFunctionIntraRoute> objectives;
    public Map<String, IConstraintIntraRoute> constraints;

    @Before
    public void setup() {
        List<ILocation> locations = createLocations();
        ILocation office = createOffice();
        TestTravelTimeMatrix travelTimeMatrix = createTravelTimeMatrix(locations, office);
        List<ITask> tasks = createTasksEqual(locations);
        routeEvaluator = new RouteEvaluator<>(travelTimeMatrix, tasks);

        constraints = Map.of(
                "constraint1", this.constraint1,
                "constraint2", constraint2,
                "constraint3", constraint3,
                "constraint4", constraint4
        );

        for (var entry : constraints.entrySet()) {
            routeEvaluator.getConstraints().addConstraint(entry.getKey(), entry.getValue());
        }

        objectives = Map.of(
                "objective1", objective1,
                "objective2", objective2,
                "objective3", objective3,
                "objective4", objective4
        );

        for (var entry : objectives.entrySet()) {
            routeEvaluator.getObjectiveFunctions().addIntraShiftObjectiveFunction(entry.getKey(), 1.0, entry.getValue());
        }
    }

    @Test
    public void copyConstructor() {
        RouteEvaluator<ITask> copy = new RouteEvaluator<>(routeEvaluator);

        assertNotEquals(copy.getConstraints(), routeEvaluator.getConstraints());
        assertNotEquals(copy.getObjectiveFunctions(), routeEvaluator.getObjectiveFunctions());

        var originalObjectives = routeEvaluator.getObjectiveFunctions();
        var copyObjectives = copy.getObjectiveFunctions();

        Set<String> foundActiveObjectives = copyObjectives.getActiveObjectiveFunctions().keySet();
        for (var entry : originalObjectives.getActiveObjectiveFunctions().entrySet()) {
            MatcherAssert.assertThat(foundActiveObjectives, hasItem(entry.getKey()));
            Assert.assertEquals(entry.getValue().getWeight(), copyObjectives.getWeightObjectivePair(entry.getKey()).getWeight(), 1e-4);
            Assert.assertEquals(entry.getValue().getWeight(), copyObjectives.getWeightObjectivePair(entry.getKey()).getWeight(), 1e-4);
        }

        var copyConstraints = copy.getConstraints();
        Set<String> foundActiveConstraints = copyConstraints.getActiveConstraintsMap().keySet();
        Set<String> actualActive = constraints.keySet();

        MatcherAssert.assertThat(foundActiveConstraints, is(actualActive));
    }


    @Test
    public void updateEvaluatorConstraintsUpdated() {
        var originalConstraints = routeEvaluator.getConstraints();

        originalConstraints.deactivateConstraint("constraint4");
        originalConstraints.deactivateConstraint("constraint1");

        RouteEvaluator<ITask> copy = new RouteEvaluator<>(routeEvaluator);
        var copyConstraints = copy.getConstraints();

        originalConstraints.deactivateConstraint("constraint2");
        MatcherAssert.assertThat(copyConstraints.getInactiveConstraintsMap().keySet(), CoreMatchers.hasItem(not("constraint2")));

        copy.update(routeEvaluator);
        MatcherAssert.assertThat(copyConstraints.getInactiveConstraintsMap().keySet(), CoreMatchers.hasItem("constraint2"));
    }

    @Test
    public void updateEvaluatorObjectivesUpdated() {
        var originalObjectives = routeEvaluator.getObjectiveFunctions();

        originalObjectives.deactivateObjective("objective4");
        originalObjectives.deactivateObjective("objective1");

        RouteEvaluator<ITask> copy = new RouteEvaluator<>(routeEvaluator);
        var copyObjectives = copy.getObjectiveFunctions();

        originalObjectives.deactivateObjective("objective2");
        MatcherAssert.assertThat(copyObjectives.getInactiveObjectiveFunctions().keySet(), CoreMatchers.hasItem(not("objective2")));

        copy.update(routeEvaluator);
        MatcherAssert.assertThat(copyObjectives.getInactiveObjectiveFunctions().keySet(), CoreMatchers.hasItem("objective2"));
    }


}