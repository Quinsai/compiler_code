package Optimize.Register.Graph;

import InterCode.QuaternionIdentify;

import java.util.LinkedList;

public class VariableNode {

    /**
     * 对应的四元式变量
     */
    public QuaternionIdentify identify;

    /**
     * 有冲突的边
     */
    private LinkedList<ConflictEdge> conflictEdge;

    /**
     * 冲突的数量
     */
    private int countOfConflict;

    /**
     * 分配的全局寄存器的编号，从1~9（都包含），-1表示不分配
     */
    public int idOfRegister;

    public VariableNode(QuaternionIdentify identify) {
        this.identify = identify;
        this.conflictEdge = new LinkedList<>();
        this.countOfConflict = 0;
    }

    public void addConflict(ConflictEdge edge) {
        conflictEdge.addLast(edge);
        this.countOfConflict ++;
    }

    public void removeConflict(ConflictEdge edge) {
        conflictEdge.remove(edge);
        this.countOfConflict --;
    }

    public LinkedList<ConflictEdge> getConflictEdge() {
        return conflictEdge;
    }

    public int getCountOfConflict() {
        return countOfConflict;
    }
}
