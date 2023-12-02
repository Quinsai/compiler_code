package Optimize.Register;

import InterCode.*;
import Other.ParamResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
        if (identify == null || hasOccurred.containsKey(identify) || identify.getType() == QuaternionIdentifyType.NUMBER || identify.getType() == QuaternionIdentifyType.GLOBAL || identify.getType() == QuaternionIdentifyType.LABEL) {
            return;
        }
        hasOccurred.put(identify, true);
        def.add(identify);
    }

    private void handleUse(QuaternionIdentify identify, HashMap<QuaternionIdentify, Boolean> hasOccurred) {
        if (identify == null || hasOccurred.containsKey(identify) || identify.getType() == QuaternionIdentifyType.NUMBER || identify.getType() == QuaternionIdentifyType.GLOBAL || identify.getType() == QuaternionIdentifyType.LABEL) {
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
                    FUNC_CALL_END, BLOCK_BEGIN, BLOCK_END, LABEL, SKIP, BRANCH_IF_FALSE,
                    BRANCH_IF_TRUE -> {}
                case SET_VALUE -> {
                    handleDef(param2, hasOccurred);
                    handleUse(param1, hasOccurred);
                }
                case FORMAL_PARA_INT, FORMAL_PARA_ARRAY -> {
                    handleDef(param1, hasOccurred);
                }
                case RETURN -> {
                    handleUse(param2, hasOccurred);
                }
                case REAL_PARA -> {
                    handleUse(param1, hasOccurred);
                }
                case VAR_INT_DECLARE, VAR_ARRAY_DECLARE, CONST_INT_DECLARE, CONST_ARRAY_DECLARE -> {
                    handleDef(result, hasOccurred);
                }
                case GETINT -> {
                    handleDef(param1, hasOccurred);
                }
                case ARRAY_INIT -> {
                    handleDef(param1, hasOccurred);
                    int dimension = 1;
                    if (!param2.arrayValue.get(0).arrayValue.isEmpty()) {
                        dimension ++;
                    }
                    int size1 = param2.arrayValue.size();
                    if (dimension == 1) {
                        for (int j = 0; j < size1; j++) {
                            QuaternionIdentify identify = param2.arrayValue.get(j);
                            handleUse(identify, hasOccurred);
                        }
                    }
                    else {
                        int size2 = param2.arrayValue.get(0).arrayValue.size();
                        for (int j = 0; j < size1; j++) {
                            QuaternionIdentify firstDimensionValue = param2.arrayValue.get(j);
                            for (int k = 0; k < size2; k++) {
                                QuaternionIdentify identify = firstDimensionValue.arrayValue.get(k);
                                handleUse(identify, hasOccurred);
                            }
                        }
                    }
                }
                default -> {
                    handleUse(param1, hasOccurred);
                    handleUse(param2, hasOccurred);
                    handleDef(result, hasOccurred);
                }
            }
        }
    }
}
