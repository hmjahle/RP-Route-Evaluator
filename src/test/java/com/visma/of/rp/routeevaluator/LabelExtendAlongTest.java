package com.visma.of.rp.routeevaluator;

import com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.plugin2.message.transport.Transport;


public class LabelExtendAlongTest extends JunitTest {


    private String problemInstanceFolder;

    @Before
    public void createProblemInstance() {
        problemInstanceFolder = "src/test/resources/testdata/test_route_simulator/walking_employee/";
        ResetAndInsertChromosome.setConfiguration(configuration);
        super.initializeIndividualAndSolverAndProblemInstance(problemInstanceFolder);
    }

    @Test
    public void labelDominanceTestResourceAndFitness() {

        RouteSimulator routeSimulator = this.routeSimulator;

        SearchGraph graph = routeSimulator.getGraph();
        SearchInfo searchInfo = new SearchInfo(graph);
        graph.setEdgesTransportMode(Transport.DRIVE);
        searchInfo.update(null, null, Long.MAX_VALUE,getEmployeeWorkShift(""));
        Node currentNode = graph.getNodes().get(1);

        CostFunction costA = new CostFunction(0);
        ResourceInterface resourcesA = new ResourceTwoElements(2, 2);
        ResourceInterface resourcesB = new ResourceTwoElements(3, 2);

        Label label = new Label(searchInfo, null, currentNode, currentNode, null, costA, 3, 2, resourcesA,0);
        Label newLabel = label.extendAlong(new ExtendToInfo(graph.getNodes().get(2), 1));
        Assert.assertNotNull(newLabel);
        Assert.assertEquals("Fitness", 27.39, newLabel.getCost().getFitness(), 0.0001);
        Assert.assertEquals("CurrentTime", "08:00:00", getDateHandler().getTimeStringFromSeconds(newLabel.getCurrentTime()));
        Assert.assertEquals("Position", "2", newLabel.getPhysicalPosition().toString());
        Assert.assertEquals("Resources", resourcesB.toString(), newLabel.getResources().toString());
    }
}
