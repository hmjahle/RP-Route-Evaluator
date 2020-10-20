package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import probleminstance.ProblemInstance;
import probleminstance.entities.timeinterval.EmployeeWorkShift;
import probleminstance.entities.timeinterval.task.Task;
import probleminstance.entities.transport.Transport;
import routeplanner.individual.Individual;
import routeplanner.solvers.fitness.RouteSimulatorResult;
import routeplanner.solvers.fitness.RoutesimulatorGraphSolver.hardConstraints.HardConstraintsIncremental;
import routeplanner.solvers.fitness.entities.TravelInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The route simulator calculates the fitness of a route.
 * When it is used it will only evaluate fitness' and hard constraints,
 * that relates to things that happens within a route. Hence fitness' like Visit history, work balance,
 * heavy tasks, etc. is not evaluated.
 * W.r.t. hard constraints the same assumption applies. Hence constraints like overtime.
 * Max travel distance on bike / walk, avoid overtime is handled within the route simulator.
 * Where constraints like two incompatible tasks on same route and heavy tasks will be ignored.
 * It is therefore assumed that input to the simulator is feasible w.r.t. to these types of constraints.
 */
public class RouteSimulator {

    private SearchGraph graph;
    private LabellingAlgorithm algorithm;
    private NodeList firstNodeList;
    private NodeList secondNodeList;
    private long[] syncedNodesStartTime;
    private long[] syncedNodesLatestStartTime;

    public SearchGraph getGraph() {
        return graph;
    }

    public RouteSimulator(ProblemInstance problemInstance) {
        this.graph = new SearchGraph(problemInstance);
        this.algorithm = new LabellingAlgorithm(graph);
        this.firstNodeList = new NodeList(graph.getNodes().size());
        this.secondNodeList = new NodeList(graph.getNodes().size());
        this.syncedNodesStartTime = new long[graph.getNodes().size()];
        this.syncedNodesLatestStartTime = new long[graph.getNodes().size()];
    }

    public void replaceHardConstraintsEvaluator(HardConstraintsIncremental hardConstraintsEvaluator) {
        this.algorithm.replaceHardConstraintsEvaluator(hardConstraintsEvaluator);
    }

    public Double simulateRouteFitness(Individual individual, EmployeeWorkShift employeeWorkShift) {
        List<Task> tasks = getTasksForEmployee(individual, employeeWorkShift);
        updateFirstTaskList(individual, tasks);
        NodeExtendInfoOneElement nodeExtendInfoOneElement = new NodeExtendInfoOneElement();
        nodeExtendInfoOneElement.update(firstNodeList);
        return algorithm.solveFitness(nodeExtendInfoOneElement, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift);
    }

    private RouteSimulatorResult simulateRouteReInsertTasksOnCriteria(Individual individual, EmployeeWorkShift employeeWorkShift, Function<Task, Boolean> criteriaFunction) {

        List<Task> regularTasks = new ArrayList<>();
        List<Task> tasksThatPassesCriteria = new ArrayList<>();
        for (Task task : getTasksForEmployee(individual, employeeWorkShift)) {
            if (criteriaFunction.apply(task))
                tasksThatPassesCriteria.add(task);
            else
                regularTasks.add(task);
        }
        return simulateRouteMergingTaskLists(individual, regularTasks, tasksThatPassesCriteria, employeeWorkShift);
    }

    public RouteSimulatorResult simulateRouteReInsertSyncedTasks(Individual individual, EmployeeWorkShift employeeWorkShift) {
        return simulateRouteReInsertTasksOnCriteria(individual, employeeWorkShift, Task::isSynced);
    }

    public RouteSimulatorResult simulateRouteReInsertStrictTasks(Individual individual, EmployeeWorkShift employeeWorkShift) {
        return simulateRouteReInsertTasksOnCriteria(individual, employeeWorkShift, Task::isStrict);
    }

    public RouteSimulatorResult simulateRouteInsertTaskIntoCurrentRoute(Individual individual, Task insertTaskId, EmployeeWorkShift employeeWorkShift) {
        List<Task> tasksForEmployee = getTasksForEmployee(individual, employeeWorkShift);
        return simulateRouteInsertTasksIntoRoute(individual, tasksForEmployee, insertTaskId, employeeWorkShift);
    }

    public RouteSimulatorResult simulateRouteInsertTasksIntoRoute(Individual individual, List<Task> tasksForEmployee, Task insertTask, EmployeeWorkShift employeeWorkShift) {
        updateFirstTaskList(individual, tasksForEmployee);
        updateSecondTaskList(individual, insertTask);
        NodeExtendInfoTwoElements nodeExtendInfoTwoElements = new NodeExtendInfoTwoElements(firstNodeList, secondNodeList);

        return algorithm.solveRouteSimulatorResult(nodeExtendInfoTwoElements, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift);
    }


    public RouteSimulatorResult simulateRouteMergingTaskLists(Individual individual, List<Task> taskSetOne,
                                                              List<Task> taskSetTwo, EmployeeWorkShift employeeWorkShift) {
        updateFirstTaskList(individual, taskSetOne);
        updateSecondTaskList(individual, taskSetTwo);

        NodeExtendInfoTwoElements nodeExtendSetsTwoElements = new NodeExtendInfoTwoElements(firstNodeList, secondNodeList);

        return algorithm.solveRouteSimulatorResult(nodeExtendSetsTwoElements, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift);
    }

    /**
     * Simulates the route given by the tasks input, the order of the tasks is the order of the route.
     *
     * @param individual        The solution for which this route belongs to.
     * @param tasks             The route to be simulated, the order of the list is the order of the route.
     * @param employeeWorkShift Employee the route applies to.
     * @return A routeSimulator result for the simulated route.
     */
    public RouteSimulatorResult simulateRouteByTheOrderOfTasks(Individual individual, List<Task> tasks, EmployeeWorkShift employeeWorkShift) {
        updateFirstTaskList(individual, tasks);
        NodeExtendInfoOneElement nodeExtendInfoOneElement = new NodeExtendInfoOneElement();
        nodeExtendInfoOneElement.update(firstNodeList);
        return algorithm.solveRouteSimulatorResult(nodeExtendInfoOneElement, syncedNodesStartTime, syncedNodesLatestStartTime, employeeWorkShift);
    }

    public RouteSimulatorResult simulateRoute(Individual individual, EmployeeWorkShift employeeWorkShift) {
        List<Task> tasksForEmployee = getTasksForEmployee(individual, employeeWorkShift);
        if (tasksForEmployee.isEmpty()) {
            Transport employeeTransport = employeeWorkShift.getTransport();
            RouteSimulatorResult simulatorResult = new RouteSimulatorResult(employeeWorkShift);
            long employeeShiftStartTime = employeeWorkShift.getStartTime();
            simulatorResult.updateTimeOfOfficeReturn(employeeShiftStartTime, new TravelInfo(employeeTransport, 0, 0));
            return simulatorResult;
        }
        return simulateRouteByTheOrderOfTasks(individual, tasksForEmployee, employeeWorkShift);
    }

    private List<Task> getTasksForEmployee(Individual individual, EmployeeWorkShift employeeWorkShift) {
        return individual.getChromosome().getOrderedTasksForEmployee(employeeWorkShift);
    }

    private void updateFirstTaskList(Individual individual, List<Task> tasks) {
        setFirstNodeList(tasks);
        setSyncedNodesStartTime(individual, tasks);
    }

    private void updateSecondTaskList(Individual individual, List<Task> taskSetTwo) {
        setSecondNodeList(taskSetTwo);
        setSyncedNodesStartTime(individual, taskSetTwo);
    }

    private void updateSecondTaskList(Individual individual, Task taskId) {
        setSecondNodeList(taskId);
        setStartTime(individual, taskId);
    }

    private void setFirstNodeList(List<Task> tasks) {
        firstNodeList.setNodes(graph, tasks);
    }

    private void setSecondNodeList(Task taskId) {
        secondNodeList.setNode(graph, taskId);
    }

    private void setSecondNodeList(List<Task> syncedTasks) {
        secondNodeList.setNodes(graph, syncedTasks);
        graph.updateNodeType(syncedTasks);
    }

    private void setSyncedNodesStartTime(Individual individual, List<Task> tasks) {
        for (Task taskId : tasks) {
            setStartTime(individual, taskId);
        }
    }

    private void setStartTime(Individual individual, Task task) {
        graph.updateNodeType(task);
        if (task.isSynced()) {
            Node node = graph.getNode(task);
            syncedNodesStartTime[node.getId()] = individual.getStartTimeOfTask(task);
            syncedNodesLatestStartTime[node.getId()] = individual.getStartTimeOfTask(task)
                    + task.getSyncedWithIntervalDiffSeconds();
        }
    }
}
