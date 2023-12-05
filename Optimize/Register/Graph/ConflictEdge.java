package Optimize.Register.Graph;

/**
 * 冲突边
 */
public class ConflictEdge {

    public VariableNode node1;

    public VariableNode node2;

    public ConflictEdge(VariableNode node1, VariableNode node2) {
        this.node1 = node1;
        this.node2 = node2;
    }
}
