package benchmarking;

import com.visma.of.rp.routeevaluator.Interfaces.ILocation;
import com.visma.of.rp.routeevaluator.Interfaces.IShift;
import com.visma.of.rp.routeevaluator.Interfaces.ITask;
import com.visma.of.rp.routeevaluator.routeResult.RouteSimulatorResult;
import com.visma.of.rp.routeevaluator.routeResult.Visit;
import com.visma.of.rp.routeevaluator.solver.RouteEvaluator;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestShift;
import testInterfaceImplementationClasses.TestTask;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Math.*;

public class benchmarking extends JUnitTestAbstract {

    public static void main(String[] args) {

        int numberOfTasks = 200;
        int gridMaxDistanceWidth = 500;
        int minDistanceToCenter = 100;
        int maxDistanceToCenter = 500;

        List<TestLocation> locationsCircle = createLocationsOnCircle(numberOfTasks, minDistanceToCenter, maxDistanceToCenter);
        List<TestLocation> locationsGrid = createGridLocations(numberOfTasks, gridMaxDistanceWidth);
        List<TestLocation> locationsGridCircle = createLocationsGridCircle(numberOfTasks, locationsCircle, locationsGrid);

        long startTime = System.currentTimeMillis();
        test(locationsCircle, 0, 0);
        test(locationsGrid, 0, 0);
        test(locationsGridCircle, 0, 0);
        test(locationsCircle, -200, -300);
        test(locationsGrid, -400, 0);
        test(locationsGridCircle, 200, 200);
        long endTime = System.currentTimeMillis();
        System.out.println("Runtime: " + (endTime - startTime));
    }

    private static void test(List<TestLocation> locations, long officeLong, long officeLat) {

        TestLocation office = addOffice(locations, officeLong, officeLat);
        List<ITask> tasks = createTasks(locations);
        List<TestTravelTimeMatrix> travelTimeMatrices = getTestTravelTimeMatrices(locations);
        RouteEvaluator routeEvaluator1 = new RouteEvaluator(0, travelTimeMatrices.get(0), tasks, office);
        RouteEvaluator routeEvaluator2 = new RouteEvaluator(0, travelTimeMatrices.get(1), tasks, office);
        RouteEvaluator routeEvaluator3 = new RouteEvaluator(0, travelTimeMatrices.get(2), tasks, office);
        List<ITask> newTasks = new ArrayList<>();

        TestTask testTask = (TestTask) tasks.get(125);
        testTask.setRequirePhysicalAppearance(false);
        testTask = (TestTask) tasks.get(155);
        testTask.setRequirePhysicalAppearance(false);
        newTasks.add(tasks.get(1));
        newTasks.add(tasks.get(85));
        newTasks.add(tasks.get(125));
        newTasks.add(tasks.get(195));
        newTasks.add(tasks.get(2));
        newTasks.add(tasks.get(133));
        newTasks.add(tasks.get(111));
        newTasks.add(tasks.get(39));
        newTasks.add(tasks.get(45));
        newTasks.add(tasks.get(155));
        newTasks.add(tasks.get(77));

        IShift shift = new TestShift(3600 * 8, 3600 * 8, 3600 * 16);
        for (int i = 0; i < 1000000; i++) {

            RouteSimulatorResult result1 = routeEvaluator1.simulateRouteByTheOrderOfTasks(newTasks, null, shift);
            RouteSimulatorResult result2 = routeEvaluator2.simulateRouteByTheOrderOfTasks(newTasks, null, shift);
            RouteSimulatorResult result3 = routeEvaluator3.simulateRouteByTheOrderOfTasks(newTasks, null, shift);
        }
//        printResult(result1);
//        printResult(result2);
//        printResult(result3);
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
        long diff = (long) Math.sqrt(numberOfTasks);
        long increment = gridMaxDistanceWidth / diff;
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

    private static List<ITask> createTasks(List<TestLocation> locations) {
        long duration = 3600;
        long startTime = 3600 * 8;
        long endTime = 3600 * 16;
        List<ITask> tasks = new ArrayList<>();
        int taskId = 0;
        for (ILocation location : locations)
            tasks.add(new TestTask(duration, startTime, endTime, false, false, true, 0, 0, location, "t-" + (taskId++)));
        return tasks;
    }

    private static TestLocation addOffice(List<TestLocation> locations, long lon, long lat) {
        TestLocation office = new TestLocation(true, lon, lat);
        locations.add(office);
        return office;
    }

    public static void printResult(RouteSimulatorResult result) {
        String visitString = "Visits: " + result.getVisitSolution().size() + "\n";
        for (Visit visit : result.getVisitSolution()) {
            visitString += "\t" + printVisit(visit, 0);
            visitString += "\n";
        }
        System.out.println("\n" + visitString);
    }


    private static String printVisit(Visit visit, long robustnessTime) {
        return "[Strict=" + visit.getTask().isStrict() + ", Synced=" + visit.getTask().isSynced() + ", Appearance=" + visit.getTask().getRequirePhysicalAppearance() + "\t"
                + getFormattedTime(visit.getTravelTimeWithParking() + robustnessTime) + " -> " + getFormattedTime(visit.getStart()) + "-" + getFormattedTime(visit.getEnd()) + "(" + getFormattedTime(visit.getTask().getStartTime()) + "-" + getFormattedTime(visit.getTask().getEndTime()) + ")" + "]"
                + "\tTaskId=: " + visit.getTask().getId();
    }

    private static String getFormattedTime(long time) {
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

    private static TestLocation createTestLocationOnCircle(double centerLat, double centerLong, double radius, double angle) {
        long lat = (long) (centerLat + radius * cos(angle));
        long lon = (long) (centerLong + radius * sin(angle));
        return new TestLocation(false, lon, lat);
    }

    public static long calculateEuclideanTravelTime(TestLocation from, TestLocation to) {
        long lat1 = from.getLatitude();
        long lat2 = to.getLatitude();
        long lon1 = from.getLongitude();
        long lon2 = to.getLongitude();
        return euclidean(lat1, lat2, lon1, lon2);
    }

    private static long euclidean(long lat1, long lat2, long lon1, long lon2) {
        long latDistance = (lat2 - lat1) * (lat2 - lat1);
        long lonDistance = (lon2 - lon1) * (lon2 - lon1);
        return (long) Math.sqrt(latDistance + lonDistance);
    }

    public static long calculateManhattanTravelTime(TestLocation from, TestLocation to) {
        return manhattan(from.getLatitude(), to.getLatitude(), from.getLongitude(), to.getLongitude());
    }

    private static long manhattan(long lat1, long lat2, long lon1, long lon2) {
        return abs(lat1 - lat2) + abs(lon1 - lon2);
    }

    public static long calculateEuclideanManhattanCombinationTravelTime(TestLocation from, TestLocation to) {
        long travelTimeManhattan = manhattan(from.getLatitude(), to.getLatitude(), from.getLongitude(), to.getLongitude());
        long travelTimeEuclidean = euclidean(from.getLatitude(), to.getLatitude(), from.getLongitude(), to.getLongitude());
        return (travelTimeEuclidean + travelTimeManhattan) / 2;
    }
}