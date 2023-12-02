package Optimize.Register;

import InterCode.Operation;
import InterCode.Quaternion;
import InterCode.SingleQuaternion;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 寄存器分配器，单例
 */
public class RegisterAllocator {

    /**
     * 寄存器分配器唯一单例
     */
    static RegisterAllocator registerAllocator;

    /**
     * 四元式们
     */
    private final LinkedList<SingleQuaternion> quaternions;

    /**
     * 四元式的数目
     */
    private final int numberOfQuaternions;

    private RegisterAllocator() {
        this.quaternions = Quaternion.getInstance().getQuaternions();
        this.numberOfQuaternions = quaternions.size();
    }

    static {
        RegisterAllocator.registerAllocator = new RegisterAllocator();
    }

    public static RegisterAllocator getInstance() {
        return registerAllocator;
    }

    public void allocate() {
        ArrayList<FunctionBlock> functionBlocks = divideFunctionBlock();
        for (FunctionBlock function :
            functionBlocks) {
            function.liveVariableAnalyze();
            function.allocateRegister();
            function.setRegister();
        }
    }

    /**
     * 划分函数块
     */
    private ArrayList<FunctionBlock> divideFunctionBlock() {

        ArrayList<FunctionBlock> blocks = new ArrayList<>();

        int currentBeginIndex = 0;
        for (int i = 0; i < numberOfQuaternions; i++) {
            Operation operation = quaternions.get(i).getOperation();

            if (operation == Operation.FUNC_BEGIN || operation == Operation.MAIN_FUNC_BEGIN) {
                currentBeginIndex = i;
            }
            else if (operation == Operation.FUNC_END || operation == Operation.MAIN_FUNC_END) {
                blocks.add(new FunctionBlock(currentBeginIndex, i));
            }
        }

        return blocks;
    }
}
