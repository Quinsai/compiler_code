package TargetCode.DataSection;

import InterCode.Operation;
import InterCode.Quaternion;
import InterCode.QuaternionIdentify;
import InterCode.SingleQuaternion;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * MIPS中的.data段
 */
public class Data {

    private String dataCode;

    /**
     * 中间代码四元式
     */
    private final LinkedList<SingleQuaternion> quaternions;

    /**
     * 最新一个要被翻译的四元式的下标
     */
    private int index;

    /**
     * 中间代码的长度
     */
    private int length;

    public Data() {
        this.dataCode = ".data\n";
        this.quaternions = new LinkedList<>();
        this.index = 0;
    }

    public void addIntoQuaternions(SingleQuaternion quaternion) {
        this.quaternions.addLast(quaternion);
    }

    public String getDataCode() {
        return dataCode;
    }

    // TODO 明天记得把这两个函数写了，今天累P了(2023/11/07)
    private void generateInt(StringBuilder mips, SingleQuaternion quaternion) {

        mips.append("\t");

        String name = quaternion.getResult().getValue();
        mips.append(name).append(": ");

        mips.append(".word ");
        if (index + 1 < length && quaternions.get(index + 1).getOperation() == Operation.SET_VALUE) {
            SingleQuaternion assignQuaternion = this.quaternions.get(index + 1);
            mips.append(assignQuaternion.getParam2().getValue());
            index ++;
        }
        else {
            mips.append("0");
        }

        mips.append("\n");
    }

    private void generateArray(StringBuilder mips, SingleQuaternion quaternion) {

        mips.append("\t");

        String name = quaternion.getResult().getValue();
        mips.append(name).append(": ");

        int dimension = 1;
        if (quaternion.getParam2() != null) {
            dimension ++;
        }

        mips.append(".word ");
        if (index + 1 < length && quaternions.get(index + 1).getOperation() == Operation.ARRAY_INIT) {

            SingleQuaternion assignQuaternion = this.quaternions.get(index + 1);

            StringBuilder initialValueString = new StringBuilder();
            QuaternionIdentify initialValue = assignQuaternion.getParam2();

            int sizeOne = initialValue.arrayValue.size();
            for (int i = sizeOne - 1; i >= 0; i--) {
                QuaternionIdentify oneDimension = initialValue.arrayValue.get(i);
                // 二维数组
                if (dimension == 2) {
                    int sizeTwo = oneDimension.arrayValue.size();
                    for (int j = sizeTwo - 1; j >= 0; j--) {
                        QuaternionIdentify twoDimension = oneDimension.arrayValue.get(j);
                        mips.append(twoDimension.getValue()).append(", ");
                    }
                }
                // 一维数组
                else {
                    mips.append(oneDimension.getValue()).append(", ");
                }
            }

            index ++;
        }
        else {

            int numOfZero = 0;
            if (dimension == 2) {
                numOfZero += Integer.parseInt(quaternion.getParam1().getValue()) * Integer.parseInt(quaternion.getParam2().getValue());
            }
            else {
                numOfZero += Integer.parseInt(quaternion.getParam1().getValue());
            }

            mips.append("0, ".repeat(Math.max(0, numOfZero)));
        }

        mips.append("\n");
    }

    /**
     * 生成.data段的方法
     */
    public void generateDataCode() {

        this.length = this.quaternions.size();
        StringBuilder mips = new StringBuilder(this.dataCode);

        for (;index < length; index ++) {
            SingleQuaternion quaternion = this.quaternions.get(index);
            switch (quaternion.getOperation()) {
                case VAR_INT_DECLARE, CONST_INT_DECLARE -> generateInt(mips, quaternion);
                case VAR_ARRAY_DECLARE, CONST_ARRAY_DECLARE -> generateArray(mips, quaternion);
            }
        }

        this.dataCode = mips.toString();

    }
}
