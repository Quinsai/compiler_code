package TargetCode.DataSection;

import InterCode.*;
import TargetCode.Target;
import TargetCode.TextSection.GenerateText;

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

    /**
     * 在生成.text段的时候，有时会需要把打印输出的字符串的部分添加到.data段里面
     * 那时候要使用这个函数
     */
    public void addIntoDataCode(String code) {
        this.dataCode += code;
    }

    // TODO 明天记得把这两个函数写了，今天累P了(2023/11/07)
    private void generateInt(StringBuilder mips, SingleQuaternion quaternion) {

        mips.append("\t");

        String name = quaternion.getResult().getValue();
        quaternion.getResult().setType(QuaternionIdentifyType.GLOBAL);
        mips.append(name).append(": ");
        GenerateText generateText = new GenerateText();

        mips.append(".word ");
        // 接下来的是赋值，而不是下一个变量/常量的定义
        if (index + 1 < length &&
            quaternions.get(index + 1).getOperation() != Operation.VAR_INT_DECLARE &&
            quaternions.get(index + 1).getOperation() != Operation.CONST_INT_DECLARE
        ) {
            // 直接使用数字写明，最简单的赋值方式
            if (quaternions.get(index + 1).getOperation() == Operation.SET_VALUE &&
                quaternions.get(index + 1).getParam2().getValue().matches("^-?\\d+$")
            ) {
                index ++;
                SingleQuaternion assignQuaternion = this.quaternions.get(index);
                mips.append(assignQuaternion.getParam2().getValue());
            }
            // 需要进过.text中的一些运算才能赋上值
            else {

                // 在赋上值之前，先给它一个0填充一下
                mips.append("0");

                index ++;
                while (index < length) {
                    SingleQuaternion setQuaternion = quaternions.get(index);
                    Target.getInstance().getText().addGlobalDataIntoTextCode(generateText.generateText(setQuaternion));
                    if (setQuaternion.getOperation() == Operation.SET_VALUE) {
                        break;
                    }
                    index ++;
                }
            }
        }
        // 没有显式地赋值，则默认值为0
        else {
            mips.append("0");
        }

        mips.append("\n");
    }

    private void generateArray(StringBuilder mips, SingleQuaternion quaternion) {

        mips.append("\t");

        String name = quaternion.getResult().getValue();
        quaternion.getResult().setType(QuaternionIdentifyType.GLOBAL);
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
