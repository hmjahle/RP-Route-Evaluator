package com.visma.of.rp.routeevaluator.RoutesimulatorGraphSolver;

import probleminstance.entities.timeinterval.task.Task;

import java.util.List;

class NodeList {
    private Node[] nodes;
    private int nodesCnt;

    NodeList(int size) {
        nodes = new Node[size];
        nodesCnt = 0;
    }

    Node getNode(int i) {
        return i > nodesCnt - 1 ? null : nodes[i];
    }

    int size() {
        return nodesCnt;
    }

    void clear() {
        nodesCnt = 0;
    }


    void setNodes(SearchGraph graph, List<Task> tasks) {
        nodesCnt = tasks.size();
        for (int i = 0; i < tasks.size(); i++) {
            nodes[i] = graph.getNode(tasks.get(i));
        }
    }

    void setNode(SearchGraph graph, Task task) {
        nodesCnt = 1;
        nodes[0] = graph.getNode(task);

    }

    void addNode(SearchGraph graph, Task task) {
        nodes[nodesCnt] = graph.getNode(task);
        nodesCnt++;
    }
}
