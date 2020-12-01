package com.visma.of.rp.routeevaluator.solver.algorithm;

import com.visma.of.rp.routeevaluator.interfaces.ITask;

import java.util.List;

public class NodeList {
    private Node[] nodes;
    private int nodesCnt;

    public NodeList(int size) {
        nodes = new Node[size];
        nodesCnt = 0;
    }

    public Node getNode(int i) {
        return i > nodesCnt - 1 ? null : nodes[i];
    }

    public int size() {
        return nodesCnt;
    }

    public void clear() {
        nodesCnt = 0;
    }

    public void initializeWithNodes(SearchGraph graph, List<? extends ITask> tasks) {
        nodesCnt = tasks.size();
        for (int i = 0; i < tasks.size(); i++) {
            nodes[i] = graph.getNode(tasks.get(i));
        }
    }

    public void initializeWithNode(SearchGraph graph, ITask task) {
        nodesCnt = 1;
        nodes[0] = graph.getNode(task);
    }

    public void addNode(SearchGraph graph, ITask task) {
        nodes[nodesCnt] = graph.getNode(task);
        nodesCnt++;
    }
}
