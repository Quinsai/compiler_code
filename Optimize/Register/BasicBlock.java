package Optimize.Register;

import InterCode.*;
import Optimize.Register.Graph.ConflictEdge;
import Optimize.Register.Graph.ConflictGraph;
import Optimize.Register.Graph.VariableNode;

import java.util.*;

/**
 * 基本块类
 */
public class BasicBlock {

    /**
     * 是否是程序的出口块
     */
    public boolean isExit;

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
    private LinkedList<SingleQuaternion> quaternions;

    public ArrayList<QuaternionIdentify> def;

    public ArrayList<QuaternionIdentify> use;

    public ArrayList<QuaternionIdentify> in;

    public ArrayList<QuaternionIdentify> out;

    /**
     * 后续基本块
     */
    public ArrayList<BasicBlock> follow;

    private int idOfTempRegister;

    public BasicBlock(int beginIndex, int endIndex) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.quaternions = Quaternion.getInstance().getQuaternions();
        this.def = new ArrayList<>();
        this.use = new ArrayList<>();
        this.follow = new ArrayList<>();
        this.isExit = false;
        this.in = new ArrayList<>();
        this.out = new ArrayList<>();
        this.idOfTempRegister = 5;
    }

    public BasicBlock(boolean isExit) {
        this.isExit = isExit;
        this.in = new ArrayList<>();
        this.out = new ArrayList<>();
    }

    public void addIntoFollow(BasicBlock block) {
        this.follow.add(block);
    }

    private void handleDef(QuaternionIdentify identify, HashMap<QuaternionIdentify, Boolean> hasOccurred) {
        if (identify == null || hasOccurred.containsKey(identify) || identify.getType() != QuaternionIdentifyType.LOCAL) {
            return;
        }
        if (identify.isArrayIdentify || identify.getValue().isEmpty()) {
            return;
        }
        hasOccurred.put(identify, true);
        def.add(identify);
    }

    private void handleUse(QuaternionIdentify identify, HashMap<QuaternionIdentify, Boolean> hasOccurred) {
        if (identify == null || hasOccurred.containsKey(identify) || identify.getType() != QuaternionIdentifyType.LOCAL) {
            return;
        }
        if (identify.isArrayIdentify || identify.getValue().isEmpty()) {
            return;
        }
        hasOccurred.put(identify, true);
        use.add(identify);
    }

    public void analyzeDefAndUse() {
        HashMap<QuaternionIdentify, Boolean> hasOccurred = new HashMap<>();
        for (int i = beginIndex; i <= endIndex; i++) {
            Operation operation = quaternions.get(i).getOperation();
            QuaternionIdentify param1 = quaternions.get(i).getParam1();
            QuaternionIdentify param2 = quaternions.get(i).getParam2();
            QuaternionIdentify result = quaternions.get(i).getResult();
            switch (operation) {
                case MAIN_FUNC_BEGIN, MAIN_FUNC_END, FUNC_BEGIN, FUNC_END, FUNC_CALL_BEGIN,
                    BLOCK_BEGIN, BLOCK_END, LABEL, SKIP, BRANCH_IF_FALSE,
                    BRANCH_IF_TRUE, CONST_ARRAY_DECLARE, VAR_ARRAY_DECLARE,
                    FORMAL_PARA_ARRAY, ARRAY_INIT,
                    GET_ADDRESS, STORE_TO_ADDRESS, GETINT_TO_ADDRESS -> {}
                case SET_VALUE -> {
                    handleDef(param2, hasOccurred);
                    handleUse(param1, hasOccurred);
                }
                case RETURN -> {
                    handleUse(param2, hasOccurred);
                }
                case REAL_PARA -> {
                    handleUse(param1, hasOccurred);
                }
                case VAR_INT_DECLARE, CONST_INT_DECLARE -> {
                    handleDef(result, hasOccurred);
                }
                case GETINT -> {
                    handleDef(param1, hasOccurred);
                }
                case FORMAL_PARA_INT -> {
                    handleDef(param1, hasOccurred);
                }
                case FUNC_CALL_END -> {
                    if (result != null) {
                        handleDef(result, hasOccurred);
                    }
                }
//                case  -> {
//                    handleDef(param1, hasOccurred);
//                    int dimension = 1;
//                    if (!param2.arrayValue.get(0).arrayValue.isEmpty()) {
//                        dimension ++;
//                    }
//                    int size1 = param2.arrayValue.size();
//                    if (dimension == 1) {
//                        for (int j = 0; j < size1; j++) {
//                            QuaternionIdentify identify = param2.arrayValue.get(j);
//                            handleUse(identify, hasOccurred);
//                        }
//                    }
//                    else {
//                        int size2 = param2.arrayValue.get(0).arrayValue.size();
//                        for (int j = 0; j < size1; j++) {
//                            QuaternionIdentify firstDimensionValue = param2.arrayValue.get(j);
//                            for (int k = 0; k < size2; k++) {
//                                QuaternionIdentify identify = firstDimensionValue.arrayValue.get(k);
//                                handleUse(identify, hasOccurred);
//                            }
//                        }
//                    }
//                }
                default -> {
                    handleUse(param1, hasOccurred);
                    handleUse(param2, hasOccurred);
                    handleDef(result, hasOccurred);
                }
            }
        }
    }

    private void handleTempVariable(int time, QuaternionIdentify identify, HashMap<QuaternionIdentify, Integer> birthTime, HashMap<QuaternionIdentify, Integer> deathTime) {
        if (identify == null || identify.getType() != QuaternionIdentifyType.LOCAL || !identify.getValue().isEmpty()) {
            return;
        }
        if (!birthTime.containsKey(identify)) {
            birthTime.put(identify, time);
            deathTime.put(identify, time);
        }
        else {
            deathTime.replace(identify, time);
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

    public void allocateTempRegister() {
        HashMap<Integer, ArrayList<QuaternionIdentify>> liveTempVariableEachTime = new HashMap<>();
        for (int i = beginIndex; i <= endIndex; i++) {
            liveTempVariableEachTime.put(i, new ArrayList<>());
        }

        HashMap<QuaternionIdentify, Integer> birthTime = new HashMap<>();
        HashMap<QuaternionIdentify, Integer> deathItem = new HashMap<>();
        for (int i = beginIndex; i <= endIndex; i++) {
            SingleQuaternion quaternion = this.quaternions.get(i);
            handleTempVariable(i, quaternion.getParam1(), birthTime, deathItem);
            handleTempVariable(i, quaternion.getParam2(), birthTime, deathItem);
            handleTempVariable(i, quaternion.getResult(), birthTime, deathItem);
        }

        Set<QuaternionIdentify> allIdentify = birthTime.keySet();
        ConflictGraph tempConflictGraph = new ConflictGraph(new ArrayList<>(allIdentify));
        for (QuaternionIdentify identify :
            allIdentify) {
            int birth = birthTime.get(identify);
            int death = deathItem.get(identify);
            for (int i = birth; i <= death; i++) {
                ArrayList<QuaternionIdentify> conflictNodes = liveTempVariableEachTime.get(i);
                for (QuaternionIdentify conflictNode :
                    conflictNodes) {
                    tempConflictGraph.addEdge(conflictNode, identify);
                }
                liveTempVariableEachTime.get(i).add(identify);
            }
        }

        PriorityQueue<VariableNode> orderedNodes = new PriorityQueue<>(new BasicBlock.NodeComparator());
        Stack<VariableNode> removedNodes = new Stack<>();
        orderedNodes.addAll(tempConflictGraph.getNodes());

        while (true) {
            if (orderedNodes.size() <= 1) {
                break;
            }

            while (orderedNodes.size() > 1 && orderedNodes.peek().getCountOfConflict() < 5) {
                VariableNode node = orderedNodes.poll();
                tempConflictGraph.removeNode(node);
                removedNodes.push(node);
            }

            if (orderedNodes.size() == 1) {
                break;
            }

            VariableNode notAllocatedNode = orderedNodes.poll();
            notAllocatedNode.idOfRegister = -1;
            tempConflictGraph.removeNode(notAllocatedNode);
            removedNodes.push(notAllocatedNode);
        }

        if (orderedNodes.isEmpty()) {
            return;
        }

        orderedNodes.peek().idOfRegister = 5;
        while (!removedNodes.isEmpty()) {
            VariableNode addedNode = removedNodes.pop();
            tempConflictGraph.addNode(addedNode);

            if (addedNode.idOfRegister == -1) {
                continue;
            }
            HashMap<Integer, Boolean> allocatedRegister = getAllocatedRegister(addedNode);

            int register;
            for (register = 5; register <= 9; register++) {
                if (!allocatedRegister.containsKey(register)) {
                    break;
                }
            }

            addedNode.idOfRegister = register;
        }

        LinkedList<VariableNode> allNodes = tempConflictGraph.getNodes();
        for (VariableNode node :
            allNodes) {
            if (node.idOfRegister != -1) {
                node.identify.setRegister("$t" + node.idOfRegister);
            }
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
}
