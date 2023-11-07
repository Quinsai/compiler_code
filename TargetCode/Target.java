package TargetCode;


import InterCode.Operation;
import InterCode.Quaternion;
import InterCode.SingleQuaternion;
import TargetCode.DataSection.Data;
import TargetCode.TextSection.SingleFunc;
import TargetCode.TextSection.Text;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 目标代码生成单例
 */
public class Target {

    private static Target instance;

    private String targetCode;

    private LinkedList<SingleQuaternion> interCode;

    private Target() {
        this.interCode = Quaternion.getInstance().getQuaternions();
        this.targetCode = "";
    }

    static {
        Target.instance = new Target();
    }

    public static Target getInstance() {
        return instance;
    }

    public void generateTargetCode() {

        int index = 0;
        int length = this.interCode.size();
        Data data = new Data();
        Text text = new Text();
        SingleFunc singleFunc = new SingleFunc();
        StringBuilder mips = new StringBuilder();

        while (true) {
            SingleQuaternion singleQuaternion = this.interCode.get(index);
            Operation operation = singleQuaternion.getOperation();
            if (operation == Operation.MAIN_FUNC_BEGIN || operation == Operation.FUNC_BEGIN) {
                break;
            }

            data.addIntoQuaternions(singleQuaternion);
            index ++;
        }
        data.generateDataCode();
        mips.append(data.getDataCode());

        mips.append("\n");

        while (index < length) {
            SingleQuaternion singleQuaternion = this.interCode.get(index);
            Operation operation = singleQuaternion.getOperation();
            singleFunc.addIntoQuaternions(singleQuaternion);
            if (operation == Operation.MAIN_FUNC_END || operation == Operation.FUNC_END) {
                text.addIntoFuncs(singleFunc);
                singleFunc = new SingleFunc();
            }

            index ++;
        }
        text.generateTextCode();
        mips.append(text.getTextCode());

        this.targetCode = mips.toString();
    }
}
