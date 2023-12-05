package Optimize.Register;

import InterCode.Operation;
import InterCode.Quaternion;
import InterCode.QuaternionIdentify;
import InterCode.SingleQuaternion;
import Optimize.Register.Graph.ConflictEdge;
import Optimize.Register.Graph.ConflictGraph;
import Optimize.Register.Graph.VariableNode;

import java.util.*;

/**
 * 函数块类
 */
public class FunctionBlock {

    /**
     * 开始的四元式（包括）
     */
    public int beginIndex;

    /**
     * 结束的四元式（包括）
     */
    public int endIndex;

    /**
     * 四元式们
     */
    private final LinkedList<SingleQuaternion> quaternions;

    private ArrayList<BasicBlock> basicBlocks;

    /**
     * 不包括代表exit的null
     */
    private int numberOfBasicBlocks;

    private HashMap<QuaternionIdentify, Integer> labelIndex;

    private ConflictGraph conflictGraph;

    public FunctionBlock(int beginIndex, int endIndex) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.quaternions = Quaternion.getInstance().getQuaternions();

        for (int i = beginIndex; i <= endIndex; i++) {
            quaternions.get(i).functionBlock = this;
        }
    }

    public void allocate() {
        this.liveVariableAnalyzeOfBasicBlock();
        this.liveVariableAnalyzeOfSingleQuaternion();
        this.allocateRegister();
        this.setRegister();
    }

    /**
     * 基本块的活跃变量分析
     */
    private void liveVariableAnalyzeOfBasicBlock() {
        this.basicBlocks = divideBasicBlock();
        // 这里表示最后一个代表出口的exit的代码块
        this.basicBlocks.add(new BasicBlock(true));
        this.numberOfBasicBlocks = this.basicBlocks.size() - 1;
        analyzeBlockFlow();

        for (int i = 0; i < numberOfBasicBlocks; i++) {
            this.basicBlocks.get(i).analyzeDefAndUse();
        }

        while (true) {

            boolean noChange = true;

            for (int i = numberOfBasicBlocks - 1; i >= 0; i--) {
                BasicBlock block = this.basicBlocks.get(i);
                ArrayList<ArrayList<QuaternionIdentify>> allFollowInSet = new ArrayList<>();
                for (BasicBlock followBlock :
                    block.follow) {
                    allFollowInSet.add(followBlock.in);
                }
                block.out = unionAll(allFollowInSet);
                ArrayList<QuaternionIdentify> newIn = union(block.use, difference(block.out, block.def));
                if (!isSame(block.in, newIn)) {
                    noChange = false;
                    block.in = newIn;
                }
            }

            if (noChange) {
                break;
            }
        }

        this.conflictGraph = new ConflictGraph(getAllVariable());

        for (int i = 0; i < numberOfBasicBlocks; i++) {

            BasicBlock block = this.basicBlocks.get(i);

            for (QuaternionIdentify defIdentify :
                block.def) {
                for (QuaternionIdentify outIdentify :
                    block.in) {
                    if (defIdentify == outIdentify) {
                        continue;
                    }
                    conflictGraph.addEdge(defIdentify, outIdentify);
                }
            }

            for (QuaternionIdentify outIdentify1 :
                block.out) {
                for (QuaternionIdentify outIdentify2 :
                    block.out) {
                    if (outIdentify1 == outIdentify2) {
                        continue;
                    }
                    conflictGraph.addEdge(outIdentify1, outIdentify2);
                }
            }

            for (QuaternionIdentify outIdentify1 :
                block.def) {
                for (QuaternionIdentify outIdentify2 :
                    block.def) {
                    if (outIdentify1 == outIdentify2) {
                        continue;
                    }
                    conflictGraph.addEdge(outIdentify1, outIdentify2);
                }
            }
        }

    }

    /**
     * 单一四元式的活跃变量分析
     */
    private void liveVariableAnalyzeOfSingleQuaternion() {
        return;
    }

    private ArrayList<QuaternionIdentify> union(ArrayList<QuaternionIdentify> set1, ArrayList<QuaternionIdentify> set2) {
        HashSet<QuaternionIdentify> unionSet = new HashSet<>(set1);
        unionSet.addAll(set2);
        return new ArrayList<>(unionSet);
    }

    private ArrayList<QuaternionIdentify> unionAll(ArrayList<ArrayList<QuaternionIdentify>> setList) {
        ArrayList<QuaternionIdentify> unionSet = new ArrayList<>();
        for (ArrayList<QuaternionIdentify> set :
            setList) {
            unionSet = union(unionSet, set);
        }
        return unionSet;
    }

    private ArrayList<QuaternionIdentify> difference(ArrayList<QuaternionIdentify> set1, ArrayList<QuaternionIdentify> set2) {
        HashSet<QuaternionIdentify> unionSet = new HashSet<>(set1);
        set2.forEach(unionSet::remove);
        return new ArrayList<>(unionSet);
    }

    private Boolean isSame(ArrayList<QuaternionIdentify> set1, ArrayList<QuaternionIdentify> set2) {
        return difference(set1, set2).isEmpty() && difference(set2, set1).isEmpty();
    }

    private ArrayList<QuaternionIdentify> getAllVariable() {
        ArrayList<ArrayList<QuaternionIdentify>> listOfAllSet = new ArrayList<>();
        for (int i = 0; i < this.numberOfBasicBlocks; i++) {
            BasicBlock block = this.basicBlocks.get(i);
            listOfAllSet.add(block.def);
            listOfAllSet.add(block.use);
        }
        return unionAll(listOfAllSet);
    }

    private ArrayList<BasicBlock> divideBasicBlock() {

        ArrayList<BasicBlock> blocks = new ArrayList<>();

        this.labelIndex = new HashMap<>();
        for (int i = beginIndex; i <= endIndex; i++) {
            Operation operation = quaternions.get(i).getOperation();
            if (operation == Operation.LABEL) {
                labelIndex.put(quaternions.get(i).getParam1(), i);
            }
        }

        HashMap<Integer, Boolean> startPoint = new HashMap<>();
        for (int i = beginIndex; i <= endIndex; i++) {
            Operation operation = quaternions.get(i).getOperation();
            if (operation == Operation.SKIP
                || operation == Operation.BRANCH_IF_FALSE
                || operation == Operation.BRANCH_IF_TRUE
            ) {
                startPoint.put(i + 1, true);
                if (operation == Operation.SKIP) {
                    int indexOfLabel = labelIndex.get(quaternions.get(i).getParam1());
                    startPoint.put(indexOfLabel, true);
                }
                else {
                    int indexOfLabel = labelIndex.get(quaternions.get(i).getParam2());
                    startPoint.put(indexOfLabel, true);
                }
            }
        }

        int currentStartIndex = beginIndex;
        for (int i = beginIndex + 1; i <= endIndex; i++) {
            if (startPoint.containsKey(i)) {
                blocks.add(new BasicBlock(currentStartIndex, i - 1));
                currentStartIndex = i;
            }
        }

        blocks.add(new BasicBlock(currentStartIndex, endIndex));

        return blocks;
    }

    private void analyzeBlockFlow() {
        HashMap<Integer, BasicBlock> startIndexOfBasicBlock = new HashMap<>();

        for (BasicBlock block:
            this.basicBlocks) {
            startIndexOfBasicBlock.put(block.beginIndex, block);
        }

        for (int i = 0; i < numberOfBasicBlocks; i ++) {
            BasicBlock block = this.basicBlocks.get(i);
            SingleQuaternion endQuaternion = this.quaternions.get(block.endIndex);
            if (endQuaternion.getOperation() == Operation.SKIP) {
                BasicBlock follow = startIndexOfBasicBlock.get(labelIndex.get(endQuaternion.getParam1()));
                block.addIntoFollow(follow);
            }
            else if (endQuaternion.getOperation() == Operation.BRANCH_IF_FALSE || endQuaternion.getOperation() == Operation.BRANCH_IF_TRUE) {
                BasicBlock follow = startIndexOfBasicBlock.get(labelIndex.get(endQuaternion.getParam2()));
                block.addIntoFollow(follow);
                block.addIntoFollow(this.basicBlocks.get(i + 1));
            }
            else {
                block.addIntoFollow(this.basicBlocks.get(i + 1));
            }
        }
    }

    static class NodeComparator implements Comparator<VariableNode> {

        @Override
        public int compare(VariableNode o1, VariableNode o2) {
            if (o1 == o2) {
                return 0;
            }
            else if (o1.getCountOfConflict() <= o2.getCountOfConflict()) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }

    private void allocateRegister() {

        PriorityQueue<VariableNode> orderedNodes = new PriorityQueue<>(new NodeComparator());
        Stack<VariableNode> removedNodes = new Stack<>();
        orderedNodes.addAll(this.conflictGraph.getNodes());

        while (true) {
            if (orderedNodes.size() <= 1) {
                break;
            }

            while (orderedNodes.size() > 1 && orderedNodes.peek().getCountOfConflict() < 8) {
                VariableNode node = orderedNodes.poll();
                conflictGraph.removeNode(node);
                removedNodes.push(node);
            }

            if (orderedNodes.size() == 1) {
                break;
            }

            VariableNode notAllocatedNode = orderedNodes.poll();
            notAllocatedNode.idOfRegister = -1;
            conflictGraph.removeNode(notAllocatedNode);
            removedNodes.push(notAllocatedNode);
        }

        if (orderedNodes.isEmpty()) {
            return;
        }

        orderedNodes.peek().idOfRegister = 0;
        while (!removedNodes.isEmpty()) {
            VariableNode addedNode = removedNodes.pop();
            conflictGraph.addNode(addedNode);

            if (addedNode.idOfRegister == -1) {
                continue;
            }
            HashMap<Integer, Boolean> allocatedRegister = getAllocatedRegister(addedNode);

            int register;
            for (register = 0; register <= 7; register++) {
                if (!allocatedRegister.containsKey(register)) {
                    break;
                }
            }

            addedNode.idOfRegister = register;
        }
    }

    private HashMap<Integer, Boolean> getAllocatedRegister(VariableNode addedNode) {
        HashMap<Integer, Boolean> allocatedRegister = new HashMap<>();
        LinkedList<ConflictEdge> allConflictEdge = addedNode.getConflictEdge();
        for (ConflictEdge edge :
            allConflictEdge) {
            VariableNode otherNode = edge.node1;
            if (edge.node1 == addedNode) {
                otherNode = edge.node2;
            }
            if (otherNode.idOfRegister == -1) {
                continue;
            }
            allocatedRegister.put(otherNode.idOfRegister, true);
        }
        return allocatedRegister;
    }

    private void setRegister() {
        LinkedList<VariableNode> allNodes = this.conflictGraph.getNodes();
        for (VariableNode node :
            allNodes) {
            if (node.idOfRegister != -1) {
                node.identify.setRegister("$s" + node.idOfRegister);
            }
        }
    }

    public ConflictGraph getConflictGraph() {
        return conflictGraph;
    }

    public ArrayList<Integer> getAllocatedRegister() {
        LinkedList<VariableNode> allNodes = this.conflictGraph.getNodes();
        HashMap<Integer, Boolean> allocated = new HashMap<>();
        ArrayList<Integer> res = new ArrayList<>();
        for (VariableNode node :
            allNodes) {
            if (node.idOfRegister != -1) {
                if (!allocated.containsKey(node.idOfRegister)) {
                    allocated.put(node.idOfRegister, true);
                }
            }
        }
        for (int i = 0; i <= 7; i++) {
            if (allocated.containsKey(i)) {
                res.add(i);
            }
        }
        return res;
    }
}
