package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ILocation;
import com.visma.of.rp.routeevaluator.interfaces.ITask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import testInterfaceImplementationClasses.TestTravelTimeMatrix;
import testSupport.JUnitTestAbstract;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchGraphTest extends JUnitTestAbstract {

    public TestTravelTimeMatrix travelTimeMatrix;
    public List<ILocation> locations;
    public ILocation office;
    public List<ITask> tasks;

    public SearchGraph searchGraph;

    @Before
    public void setup() {
        locations = createLocations();
        tasks = createTasksEqual(locations);
        travelTimeMatrix = createTravelTimeMatrix(locations, createOffice());
        office = createOffice();

        for (ITask task : tasks) {
            travelTimeMatrix.addUndirectedConnection(office, task.getLocation(), 10);
        }

        searchGraph = new SearchGraph(travelTimeMatrix, tasks, office, office);
    }

    @Test
    public void copyConstructor() {
        SearchGraph copy = new SearchGraph(searchGraph);

        validateEqual(copy, searchGraph);
    }


    @Test
    public void copyConstructorOpenRoutes() {
        searchGraph.useOpenStartRoutes();
        searchGraph.useOpenEndedRoutes();
        SearchGraph copy = new SearchGraph(searchGraph);

        validateEqual(copy, searchGraph);
    }

    private void validateEqual(SearchGraph copy, SearchGraph original) {
        assertEquals(copy.getOrigin(), original.getOrigin());
        assertEquals(copy.getDestination(), original.getDestination());

        for (ITask task : tasks) {
            assertEquals(copy.getLocationId(task), original.getLocationId(task));
        }

        for (ITask task : tasks) {
            assertEquals(copy.getNode(task), original.getNode(task));
        }

        assertEquals(copy.getNodes(), original.getNodes());

        for (ITask taskA : tasks) {
            for (ITask taskB : tasks) {
                Integer travelTimeCopy = copy.getTravelTime(copy.getLocationId(taskA), copy.getLocationId(taskB));
                Integer travelTimeOriginal = original.getTravelTime(original.getLocationId(taskA), original.getLocationId(taskB));
                assertEquals(travelTimeCopy, travelTimeOriginal);
            }
        }
    }
}