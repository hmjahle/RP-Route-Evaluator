package com.visma.of.rp.routeevaluator.searchGraph;


import com.visma.of.rp.routeevaluator.Interfaces.ITask;

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

    int size() {
        return nodesCnt;
    }

    void clear() {
        nodesCnt = 0;
    }


    public void setNodes(SearchGraph graph, List<ITask> tasks) {
        nodesCnt = tasks.size();
        for (int i = 0; i < tasks.size(); i++) {
            nodes[i] = graph.getNode(tasks.get(i));
        }
    }

    public void setNode(SearchGraph graph, ITask task) {
        nodesCnt = 1;
        nodes[0] = graph.getNode(task);

    }

    void addNode(SearchGraph graph, ITask task) {
        nodes[nodesCnt] = graph.getNode(task);
        nodesCnt++;
    }
}
