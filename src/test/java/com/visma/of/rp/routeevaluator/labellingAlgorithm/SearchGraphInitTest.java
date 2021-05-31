package com.visma.of.rp.routeevaluator.labellingAlgorithm;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import com.visma.of.rp.routeevaluator.solver.algorithm.SearchGraph;
import org.junit.Assert;
import org.junit.Test;
import testInterfaceImplementationClasses.TestLocation;
import testInterfaceImplementationClasses.TestTask;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.ArrayList;
import java.util.Collections;

public class SearchGraphInitTest extends JUnitTestAbstract {

    /**
     * Tests if a graph can be build with a travel matrix containing a location, but with no tasks.
     * The graph should be able to use the location as origin and destination.
     */
    @Test
    public void buildsGraphWithLocationButNoTasks() {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        ILocation location = new TestLocation(false);
        travelTimeMatrix.addUndirectedConnection(location, location, 0);
        SearchGraph graph = new SearchGraph(travelTimeMatrix, new ArrayList<>(), null, null);
        Assert.assertNotNull("Should be able to build a graph.", graph);
        graph.updateOrigin(location);
        graph.updateDestination(location);
        Assert.assertEquals("Origin is updated correctly", graph.getOrigin().getLocationId(), graph.getLocationId(location));
        Assert.assertEquals("Destination is updated correctly", graph.getDestination().getLocationId(), graph.getLocationId(location));
    }


    /**
     * Tests if a graph can be build with a travel matrix containing a location not used by the tasks.
     * The graph should be able to use the location as origin and destination.
     */
    @Test
    public void buildsGraphWithLocationButNotInTasks() {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        ILocation location = new TestLocation(false);
        ITask task = createStandardTask(1, 1, 2);
        travelTimeMatrix.addUndirectedConnection(task.getLocation(), location, 2);
        SearchGraph graph = new SearchGraph(travelTimeMatrix, Collections.singletonList(task), null, null);
        Assert.assertNotNull("Should be able to build a graph.", graph);
        graph.updateOrigin(task.getLocation());
        graph.updateDestination(location);
        Assert.assertEquals("Origin is updated correctly", graph.getOrigin().getLocationId(), graph.getLocationId(task.getLocation()));
        Assert.assertEquals("Destination is updated correctly", graph.getDestination().getLocationId(), graph.getLocationId(location));
    }


    /**
     * Tests if a graph can be build with non-physical tasks having locations set to null.
     */
    @Test
    public void buildsGraphWithNonPhysicalLocationsEqualNull() {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        ILocation office = new TestLocation(true);
        travelTimeMatrix.addUndirectedConnection(office, office, 0);
        ITask task = new TestTask(10, 0, 100, false, false, false, 0, 0, null, "y");
        SearchGraph graph = new SearchGraph(travelTimeMatrix, Collections.singletonList(task), office, office);
        Assert.assertNotNull("Should be able to build a graph.", graph);
        Assert.assertEquals("Location id must be -1 as this represent not having a location.", -1, graph.getLocationId(task));

    }

    /**
     * Tests if a graph can be build with non-physical tasks having locations set to null and other normal tasks.
     */
    @Test
    public void buildsGraphWithNonPhysicalLocationsEqualNullAndNormalTasks() {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        ILocation office = new TestLocation(true);
        ITask task = new TestTask(10, 0, 100, false, false, false, 0, 0, null, "x");
        ITask standardTaskA = createStandardTask(1, 1, 2, "y");
        ITask standardTaskB = createStandardTask(1, 1, 2, "z");
        ArrayList<ITask> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(standardTaskA);
        tasks.add(standardTaskB);
        travelTimeMatrix.addUndirectedConnection(office, standardTaskA.getLocation(), 0);
        travelTimeMatrix.addUndirectedConnection(office, standardTaskB.getLocation(), 0);
        travelTimeMatrix.addUndirectedConnection(standardTaskA.getLocation(), standardTaskB.getLocation(), 0);

        SearchGraph graph = new SearchGraph(travelTimeMatrix, tasks, office, office);
        Assert.assertNotNull("Should be able to build a graph.", graph);
        Assert.assertEquals("Location id must be -1 as this represent not having a location.", -1, graph.getLocationId(task));

    }

    /**
     * Tests if a graph can be build with non-physical tasks having locations set to null.
     */
    @Test
    public void buildsGraphWithNonPhysicalLocationsEqualNullAndNoOffice() {
        TestTravelTimeMatrix travelTimeMatrix = new TestTravelTimeMatrix();
        ITask task = new TestTask(10, 0, 100, false, false, false, 0, 0, null, "y");
        SearchGraph graph = new SearchGraph(travelTimeMatrix, Collections.singletonList(task), null, null);
        Assert.assertNotNull("Should be able to build a graph.", graph);
        Assert.assertEquals("Location id must be -1 as this represent not having a location.", -1, graph.getLocationId(task));
    }
}
