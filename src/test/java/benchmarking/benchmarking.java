package benchmarking;

import com.visma.of.rp.routeevaluator.evaluation.constraints.OvertimeConstraint;
import com.visma.of.rp.routeevaluator.evaluation.constraints.StrictTimeWindowConstraint;
import com.visma.of.rp.routeevaluator.evaluation.objectives.*;
import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.IShift;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.results.RouteEvaluatorResult;
import com.visma.of.rp.routeevaluator.results.Visit;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTask;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.*;

public class benchmarking extends JUnitTestAbstract {

    public static void main(String[] args) {

        int numberOfTasks = 200;
        int gridMaxDistanceWidth = 200;
        int minDistanceToCenter = 25;
        int maxDistanceToCenter = 200;

        List<TestLocation> locationsCircle = createLocationsOnCircle(numberOfTasks, minDistanceToCenter, maxDistanceToCenter);
        List<TestLocation> locationsGrid = createGridLocations(numberOfTasks, gridMaxDistanceWidth);
        List<TestLocation> locationsGridCircle = createLocationsGridCircle(numberOfTasks, locationsCircle, locationsGrid);

        long startTime = System.currentTimeMillis();
        test(locationsCircle, 0, 0);
        test(locationsGrid, 0, 0);
        test(locationsGridCircle, 0, 0);
        test(locationsCircle, -150, -150);
        test(locationsGrid, -200, 0);
        test(locationsGridCircle, 100, 100);
        long endTime = System.currentTimeMillis();
        System.out.println("Runtime: " + (endTime - startTime));
    }

    private static void test(List<TestLocation> locations, int officeInteger, int officeLat) {

        TestLocation office = addOffice(locations, officeInteger, officeLat);
        List<ITask> tasks = createTasksFromLocations(locations);
        List<TestTravelTimeMatrix> travelTimeMatrices = getTestTravelTimeMatrices(locations);
        RouteEvaluator<ITask> routeEvaluator1 = new RouteEvaluator(travelTimeMatrices.get(0), tasks, office);
        RouteEvaluator<ITask> routeEvaluator2 = new RouteEvaluator<ITask>(travelTimeMatrices.get(1), tasks, office);
        RouteEvaluator<ITask> routeEvaluator3 = new RouteEvaluator(travelTimeMatrices.get(2), tasks, office);
        RouteEvaluator<ITask> routeEvaluator4 = new RouteEvaluator(travelTimeMatrices.get(2), tasks, office);
        List<ITask> newTasks = new ArrayList<>();
        List<ITask> newTasks2 = new ArrayList<>();
        List<ITask> mergeTasks = new ArrayList<>();

        TestTask testTask = (TestTask) tasks.get(125);
        testTask.setRequirePhysicalAppearance(false);
        testTask = (TestTask) tasks.get(155);
        testTask.setRequirePhysicalAppearance(false);
        newTasks.add(tasks.get(160));
        newTasks.add(tasks.get(77));
        newTasks.add(tasks.get(85));
        newTasks.add(tasks.get(120));
        newTasks.add(tasks.get(1));
        newTasks.add(tasks.get(2));
        newTasks.add(tasks.get(195));
        newTasks.add(tasks.get(125));
        newTasks.add(tasks.get(39));
        newTasks.add(tasks.get(133));
        newTasks.add(tasks.get(155));

        newTasks2.add(tasks.get(21));
        newTasks2.add(tasks.get(145));
        newTasks2.add(tasks.get(115));
        newTasks2.add(tasks.get(31));
        newTasks2.add(tasks.get(33));
        newTasks2.add(tasks.get(75));

        mergeTasks.add(tasks.get(84));
        mergeTasks.add(tasks.get(28));
        mergeTasks.add(tasks.get(1));
        mergeTasks.add(tasks.get(182));
        mergeTasks.add(tasks.get(3));


        routeEvaluator1.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator1.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        routeEvaluator1.addConstraint(new OvertimeConstraint());
        routeEvaluator1.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(1));

        routeEvaluator2.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator2.addObjectiveIntraShift(new OvertimeObjectiveFunction());
        routeEvaluator2.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        routeEvaluator2.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(1));

        routeEvaluator3.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator3.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        routeEvaluator3.addObjectiveIntraShift(new CustomCriteriaObjectiveFunction(objectiveInfo -> !objectiveInfo.isDestination() && objectiveInfo.isStrict(),
                (objectiveInfo -> (double) Math.max(0, objectiveInfo.getVisitEnd() -
                        objectiveInfo.getTask().getEndTime()))));

        routeEvaluator3.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(1));

        routeEvaluator4.addObjectiveIntraShift(new TravelTimeObjectiveFunction());
        routeEvaluator4.addObjectiveIntraShift(new TimeWindowObjectiveFunction());
        routeEvaluator4.addObjectiveIntraShift(new OvertimeObjectiveFunction());
        routeEvaluator4.addObjectiveIntraShift(new SyncedTaskStartTimeObjectiveFunction(1));
        routeEvaluator4.addConstraint(new StrictTimeWindowConstraint());

        Map<ITask, Integer> syncedTasksAllStartTime = new HashMap<>();
        Map<ITask, Integer> syncedTasksNewStartTime = new HashMap<>();
        syncedTasksNewStartTime.put(tasks.get(120), (3 * 60) * 60);
        syncedTasksNewStartTime.put(tasks.get(160), 15 * 60);
        for (ITask task : newTasks)
            if (task.isSynced()) {
                syncedTasksAllStartTime.put(task, task.getStartTime());
            }
        for (ITask task : mergeTasks)
            if (task.isSynced())
                syncedTasksAllStartTime.put(task, task.getStartTime());

        IShift shift = new TestShift(0, 3600 * 10);
        for (int i = 0; i < 20000; i++) {
            for (int j = 0; j < 100; j++) {
                routeEvaluator1.evaluateRouteObjective(newTasks, syncedTasksNewStartTime, shift);

                routeEvaluator2.evaluateRouteObjective(newTasks, syncedTasksNewStartTime, shift);

                routeEvaluator3.evaluateRouteObjective(newTasks, syncedTasksNewStartTime, shift);

                ITask reInsert = tasks.get(120);
                List<ITask> removeOneTask = new ArrayList<>(newTasks);
                removeOneTask.remove(tasks.get(120));
                routeEvaluator1.evaluateRouteByTheOrderOfTasksInsertTask(removeOneTask, reInsert, syncedTasksNewStartTime, shift);

                reInsert = tasks.get(133);
                removeOneTask = new ArrayList<>(newTasks);
                removeOneTask.remove(tasks.get(133));
                routeEvaluator2.evaluateRouteByTheOrderOfTasksInsertTask(removeOneTask, reInsert, syncedTasksNewStartTime, shift);

                reInsert = tasks.get(125);
                removeOneTask = new ArrayList<>(newTasks);
                removeOneTask.remove(tasks.get(125));
                routeEvaluator3.evaluateRouteByTheOrderOfTasksInsertTask(removeOneTask, reInsert, syncedTasksNewStartTime, shift);
            }
            routeEvaluator2.evaluateRouteByTheOrderOfTasksInsertTasks(newTasks, mergeTasks, syncedTasksAllStartTime, shift);

            routeEvaluator3.evaluateRouteByTheOrderOfTasksInsertTasks(newTasks2, mergeTasks, syncedTasksAllStartTime, shift);

            List<ITask> removeOneTask = new ArrayList<>(newTasks);
            removeOneTask.remove(tasks.get(125));
            routeEvaluator4.evaluateRouteByTheOrderOfReInsertBasedOnCriteriaTasks(removeOneTask, syncedTasksNewStartTime, shift, ITask::isStrict);
        }
    }

    private static List<TestLocation> createLocationsGridCircle(int numberOfTasks, List<TestLocation> locationsCircle, List<TestLocation> locationsGrid) {
        List<TestLocation> locationsGridCircle = new ArrayList<>();
        for (int i = 0; i < numberOfTasks; i++) {
            if (i % 2 == 0)
                locationsGridCircle.add(locationsCircle.get(i));
            else
                locationsGridCircle.add(locationsGrid.get(i));
        }
        return locationsGridCircle;
    }

    private static List<TestLocation> createGridLocations(int numberOfTasks, int gridMaxDistanceWidth) {
        List<TestLocation> locations = new ArrayList<>();
        int diff = (int) Math.sqrt(numberOfTasks);
        int increment = gridMaxDistanceWidth / diff;
        int i = 0;
        int lat = 0;
        while (i < numberOfTasks) {
            for (int lon = 0; lon < gridMaxDistanceWidth; lon += increment) {
                TestLocation location = new TestLocation(false, lon, lat);
                locations.add(location);
                lat += increment;
                i++;
                if (i >= numberOfTasks)
                    break;
            }
        }

        return locations;
    }


    private static List<ITask> createTasksFromLocations(List<TestLocation> locations) {
        int duration = 10 * 60;
        int startTime;
        int endTime;
        List<ITask> tasks = new ArrayList<>();
        int taskId = 0;
        for (ILocation location : locations) {
            boolean synced = false;
            boolean strict = false;
            if (taskId % 10 == 0)
                synced = true;
            if (taskId % 10 == 5)
                strict = true;
            if (taskId % 3 == 1)
                duration = 20 * 60;
            else if (taskId % 3 == 2)
                duration = 30 * 60;

            if (taskId % 4 == 0) {
                startTime = 0;
                endTime = (30 + 2 * 60) * 60;
            } else if (taskId % 4 == 1) {
                startTime = (30 + 2 * 60) * 60;
                endTime = 5 * 60 * 60;
            } else if (taskId % 4 == 2) {
                startTime = 5 * 60 * 60;
                endTime = (30 + 7 * 60) * 60;
            } else {
                startTime = (30 + 7 * 60) * 60;
                endTime = 10 * 60 * 60;
            }

            tasks.add(new TestTask(duration, startTime, endTime, strict, synced, true, 0, 0, location, "t-" + (taskId++)));
        }
        return tasks;
    }

    private static TestLocation addOffice(List<TestLocation> locations, int lon, int lat) {
        TestLocation office = new TestLocation(true, lon, lat);
        locations.add(office);
        return office;
    }

    public static void printResult(RouteEvaluatorResult<ITask> result) {
        if (result == null) {
            System.out.println();
            System.out.println("No result!");
            System.out.println();
            return;
        }

        System.out.println("\nObjective: " + result.getObjectiveValue() + "\tReturn to office at: " + getFormattedTime(result.getTimeOfArrivalAtDestination()));
        if (result.getObjective() instanceof WeightedObjectiveWithValues) {
            WeightedObjectiveWithValues objectiveWithValues = (WeightedObjectiveWithValues) result.getObjective();
            for (Map.Entry<String, Double> stringDoubleEntry : objectiveWithValues.getObjectiveFunctionValues())
                System.out.println(stringDoubleEntry.getKey() + "\t\t:" + stringDoubleEntry.getValue());
        }

        String visitString = "Visits: " + result.getVisitSolution().size() + "\n";
        for (Visit<ITask> visit : result.getVisitSolution()) {
            visitString += "\t" + printVisit(visit, 0);
            visitString += visit.getTask().getLocation() + "\n";
        }
        System.out.println(visitString);
    }


    private static String printVisit(Visit visit, int robustnessTime) {
        return "[Strict=" + visit.getTask().isStrict() + ", Synced=" + visit.getTask().isSynced() + ", Appearance=" + visit.getTask().getRequirePhysicalAppearance() + "\t"
                + getFormattedTime(visit.getTravelTime() + robustnessTime) + " -> " + getFormattedTime(visit.getStartTime()) + "-" + getFormattedTime(visit.getStartTime() + visit.getTask().getDuration()) + "(" + getFormattedTime(visit.getTask().getStartTime()) + "-" + getFormattedTime(visit.getTask().getEndTime()) + ")" + "]"
                + "\tTaskId=: " + visit.getTask().getId();
    }

    private static String getFormattedTime(int time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm.ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date visitStart = new Date((time) * 1000L);
        return simpleDateFormat.format(visitStart);
    }

    private static List<TestTravelTimeMatrix> getTestTravelTimeMatrices(List<TestLocation> locations) {
        List<TestTravelTimeMatrix> travelTimeMatrices = new ArrayList<>();
        travelTimeMatrices.add(new TestTravelTimeMatrix());
        travelTimeMatrices.add(new TestTravelTimeMatrix());
        travelTimeMatrices.add(new TestTravelTimeMatrix());
        int taskNumber = 0;
        for (TestLocation from : locations)
            for (TestLocation to : locations) {
                travelTimeMatrices.get(0).addDirectedConnection(from, to, calculateEuclideanTravelTime(from, to) + taskNumber % 2);
                travelTimeMatrices.get(1).addDirectedConnection(from, to, calculateManhattanTravelTime(from, to) + taskNumber % 2);
                travelTimeMatrices.get(2).addDirectedConnection(from, to, calculateEuclideanManhattanCombinationTravelTime(from, to) + taskNumber % 2);
            }
        return travelTimeMatrices;
    }

    private static List<TestLocation> createLocationsOnCircle(int numberOfLocations, double minStraightLineToOffice, double maxStraightLineToOffice) {
        List<TestLocation> locations = new ArrayList<>();
        int i = 0;
        int degreeIncrease = 30;
        double initialStraightLineToOffice = minStraightLineToOffice;
        double straightLineToOfficeIncrease = (maxStraightLineToOffice - minStraightLineToOffice) / numberOfLocations;
        while (i < numberOfLocations) {
            for (double degree = 0; degree <= 360; degree += degreeIncrease) {
                i++;
                double angle = degree * PI / 180;
                initialStraightLineToOffice += straightLineToOfficeIncrease;
                TestLocation location = createTestLocationOnCircle(0, 0, initialStraightLineToOffice, angle);
                locations.add(location);
                if (i >= numberOfLocations)
                    break;
            }
            degreeIncrease = max(2, degreeIncrease - 5);
        }
        return locations;
    }

    private static TestLocation createTestLocationOnCircle(double centerLat, double centerInteger, double radius, double angle) {
        int lat = (int) (centerLat + radius * cos(angle));
        int lon = (int) (centerInteger + radius * sin(angle));
        return new TestLocation(false, lon, lat);
    }

    public static int calculateEuclideanTravelTime(TestLocation from, TestLocation to) {
        int lat1 = from.getLatitude();
        int lat2 = to.getLatitude();
        int lon1 = from.getLongitude();
        int lon2 = to.getLatitude();
        return euclidean(lat1, lat2, lon1, lon2);
    }

    private static int euclidean(int lat1, int lat2, int lon1, int lon2) {
        int latDistance = (lat2 - lat1) * (lat2 - lat1);
        int lonDistance = (lon2 - lon1) * (lon2 - lon1);
        return (int) Math.sqrt(latDistance + lonDistance);
    }

    public static int calculateManhattanTravelTime(TestLocation from, TestLocation to) {
        return manhattan(from.getLatitude(), to.getLatitude(), from.getLongitude(), to.getLongitude());
    }

    private static int manhattan(int lat1, int lat2, int lon1, int lon2) {
        return abs(lat1 - lat2) + abs(lon1 - lon2);
    }

    public static int calculateEuclideanManhattanCombinationTravelTime(TestLocation from, TestLocation to) {
        int travelTimeManhattan = manhattan(from.getLatitude(), to.getLatitude(), from.getLongitude(), to.getLongitude());
        int travelTimeEuclidean = euclidean(from.getLatitude(), to.getLatitude(), from.getLongitude(), to.getLongitude());
        return (travelTimeEuclidean + travelTimeManhattan) / 2;
    }
}