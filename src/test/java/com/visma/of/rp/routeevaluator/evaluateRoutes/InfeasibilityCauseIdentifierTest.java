package com.visma.of.rp.routeevaluator.evaluateRoutes;

import com.visma.of.rp.routeevaluator.evaluation.cause.InfeasibilityCauseIdentifier;
import com.visma.of.rp.routeevaluator.evaluation.constraints.OvertimeConstraint;
import com.visma.of.rp.routeevaluator.evaluation.constraints.SyncedTasksConstraint;
import com.visma.of.rp.routeevaluator.evaluation.objectives.OvertimeObjectiveFunction;
import com.visma.of.rp.routeevaluator.evaluation.objectives.SyncedTasksObjective;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.interfaces.ITravelTimeMatrix;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTask;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfeasibilityCauseIdentifierTest extends JUnitTestAbstract {

    Map<Integer, ITravelTimeMatrix> travelTimeMatrices;
    List<ITask> tasks;
    ILocation office = new TestLocation(true);

    @Before()
    public void initialize() {
        tasks = createTasks();
        travelTimeMatrices = createTravelTimeMatrix(tasks, office);
    }

    @Test
    public void isFeasibleSingleConsObj() {
        TestShift shift = new TestShift(0, 30);
        InfeasibilityCauseIdentifier<ITask> ici = new InfeasibilityCauseIdentifier<>(tasks, travelTimeMatrices, office, office);
        String id = "Overtime";
        ici.addInfeasibilityTesterPair(id, new OvertimeObjectiveFunction(), new OvertimeConstraint());

        Map<String, Boolean> feasible = ici.isFeasible(tasks.subList(0, 2), null, shift);
        Assert.assertTrue(feasible.get(id));

        feasible = ici.isFeasible(tasks, null, shift);
        Assert.assertFalse(feasible.get(id));

        Assert.assertEquals(0, ici.objective(tasks.subList(0, 2), null, shift).get(id), DELTA);
        Assert.assertEquals(17, ici.objective(tasks, null, shift).get(id), DELTA);
    }

    @Test
    public void isFeasibleSingleConstraintNoObjective() {
        TestShift shift = new TestShift(0, 30);
        InfeasibilityCauseIdentifier<ITask> ici = new InfeasibilityCauseIdentifier<>(tasks, travelTimeMatrices, office, office);
        String id = "Overtime";
        ici.addInfeasibilityTesterPair(id, new OvertimeConstraint());

        Map<String, Boolean> feasible = ici.isFeasible(tasks.subList(0, 2), null, shift);
        Assert.assertTrue(feasible.get(id));

        feasible = ici.isFeasible(tasks, null, shift);
        Assert.assertFalse(feasible.get(id));
    }

    @Test
    public void isFeasibleSingleObjectiveNoConstraint() {
        TestShift shift = new TestShift(0, 30);
        InfeasibilityCauseIdentifier<ITask> ici = new InfeasibilityCauseIdentifier<>(tasks, travelTimeMatrices, office, office);
        String id = "Overtime";
        ici.addInfeasibilityTesterPair(id, new OvertimeObjectiveFunction());

        Assert.assertEquals(0, ici.objective(tasks.subList(0, 2), null, shift).get(id), DELTA);
        Assert.assertEquals(17, ici.objective(tasks, null, shift).get(id), DELTA);
    }

    @Test
    public void isFeasibleMultipleConsObj() {
        TestShift shift = new TestShift(0, 30);
        TestTask newSyncedTask = new TestTask(10, 100, 10, "5");
        newSyncedTask.setSynced(true);
        Map<ITask, Integer> syncedTasksStartTime = new HashMap<>();
        syncedTasksStartTime.put(newSyncedTask, 10);

        tasks.add(newSyncedTask);
        travelTimeMatrices = createTravelTimeMatrix(tasks, office);
        InfeasibilityCauseIdentifier<ITask> ici = new InfeasibilityCauseIdentifier<>(tasks, travelTimeMatrices, office, office);
        String overTimeId = "Overtime";
        ici.addInfeasibilityTesterPair(overTimeId, new OvertimeObjectiveFunction(), new OvertimeConstraint());

        String syncedId = "Synced";
        ici.addInfeasibilityTesterPair(syncedId, new SyncedTasksObjective(), new SyncedTasksConstraint());

        Map<String, Boolean> feasible = ici.isFeasible(tasks.subList(0, 2), syncedTasksStartTime, shift);
        Map<String, Double> objective = ici.objective(tasks.subList(0, 2), syncedTasksStartTime, shift);
        Assert.assertTrue(feasible.get(overTimeId));
        Assert.assertTrue(feasible.get(syncedId));
        Assert.assertEquals(0.0, objective.get(overTimeId), DELTA);
        Assert.assertEquals(0.0, objective.get(syncedId), DELTA);

        feasible = ici.isFeasible(tasks, syncedTasksStartTime, shift);
        objective = ici.objective(tasks, syncedTasksStartTime, shift);
        Assert.assertFalse(feasible.get(overTimeId));
        Assert.assertFalse(feasible.get(syncedId));
        Assert.assertEquals(28.0, objective.get(overTimeId), DELTA);
        Assert.assertEquals(36.0, objective.get(syncedId), DELTA);

        List<ITask> newTaskOrder = new ArrayList<>();
        newTaskOrder.add(tasks.get(0));
        newTaskOrder.add(newSyncedTask);
        feasible = ici.isFeasible(newTaskOrder, syncedTasksStartTime, shift);
        objective = ici.objective(newTaskOrder, syncedTasksStartTime, shift);
        Assert.assertTrue(feasible.get(overTimeId));
        Assert.assertFalse(feasible.get(syncedId));
        Assert.assertEquals(0.0, objective.get(overTimeId), DELTA);
        Assert.assertEquals(3.0, objective.get(syncedId), DELTA);

        newTaskOrder.remove(0);
        newTaskOrder.add(tasks.get(0));
        newTaskOrder.add(tasks.get(1));
        feasible = ici.isFeasible(newTaskOrder, syncedTasksStartTime, shift);
        objective = ici.objective(newTaskOrder, syncedTasksStartTime, shift);

        Assert.assertFalse(feasible.get(overTimeId));
        Assert.assertTrue(feasible.get(syncedId));
        Assert.assertEquals(14.0, objective.get(overTimeId), DELTA);
        Assert.assertEquals(0.0, objective.get(syncedId), DELTA);
    }


    private List<ITask> createTasks() {
        List<ITask> tasks = new ArrayList<>();
        tasks.add(new TestTask(0, 100, 10, "1"));
        tasks.add(new TestTask(0, 100, 10, "2"));
        tasks.add(new TestTask(0, 100, 10, "3"));
        tasks.add(new TestTask(0, 100, 10, "4"));
        return tasks;
    }

    protected Map<Integer, ITravelTimeMatrix> createTravelTimeMatrix(List<ITask> tasks, ILocation office) {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        for (ITask taskA : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, taskA.getLocation(), 2);
            for (ITask taskB : tasks) {
                if (taskA != taskB)
                    travelTimeMatrix.addUndirectedConnection(taskA.getLocation(), taskB.getLocation(), 1);
            }
        }
        Map<Integer, ITravelTimeMatrix> travelTimeMatrixMap = new HashMap<>();
        travelTimeMatrixMap.put(1, travelTimeMatrix);
        return travelTimeMatrixMap;
    }
}