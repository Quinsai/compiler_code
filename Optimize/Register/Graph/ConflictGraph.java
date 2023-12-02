package Optimize.Register.Graph;

import InterCode.QuaternionIdentify;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 冲突图类
 */
public class ConflictGraph {

    private LinkedList<VariableNode> nodes;

    private LinkedList<ConflictEdge> edges;

    public ConflictGraph(ArrayList<QuaternionIdentify> identifies) {
        this.nodes = new LinkedList<>();
        for (QuaternionIdentify identify :
            identifies) {
            this.nodes.add(new VariableNode(identify));
        }
        this.edges = new LinkedList<>();
    }

    public void addEdge(QuaternionIdentify identify1, QuaternionIdentify identify2) {
        for (ConflictEdge edge :
            edges) {
            if ((edge.node1.identify == identify1 && edge.node2.identify == identify2)
                || (edge.node2.identify == identify1 && edge.node1.identify == identify2)
            ) {
                return;
            }
        }

        VariableNode node1 = null, node2 = null;
        for (VariableNode node :
            nodes) {
            if (node.identify == identify1) {
                node1 = node;
            }
            else if (node.identify == identify2) {
                node2 = node;
            }
        }

        ConflictEdge edge = new ConflictEdge(node1, node2);
        if (node1 != null) {
            node1.addConflict(edge);
        }
        if (node2 != null) {
            node2.addConflict(edge);
        }
        edges.addLast(edge);
    }

    public LinkedList<VariableNode> getNodes() {
        return nodes;
    }

    public LinkedList<ConflictEdge> getEdges() {
        return edges;
    }

    public void removeNode(VariableNode node) {
        this.nodes.remove(node);
        LinkedList<ConflictEdge> edgeOfNode = node.getConflictEdge();
        for (ConflictEdge edge :
            edgeOfNode) {
            VariableNode otherNode = edge.node1;
            if (edge.node1 == node) {
                otherNode = edge.node2;
            }
            otherNode.removeConflict(edge);
            this.edges.remove(edge);
        }
    }

    public void addNode(VariableNode node) {
        this.nodes.add(node);
        LinkedList<ConflictEdge> edgeOfNode = node.getConflictEdge();
        for (ConflictEdge edge :
            edgeOfNode) {
            VariableNode otherNode = edge.node1;
            if (edge.node1 == node) {
                otherNode = edge.node2;
            }
            if (this.nodes.contains(otherNode)) {
                otherNode.addConflict(edge);
                this.edges.add(edge);
            }
        }
    }
}
