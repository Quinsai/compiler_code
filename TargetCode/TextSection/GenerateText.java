package TargetCode.TextSection;

import InterCode.Operation;
import InterCode.QuaternionIdentify;
import InterCode.QuaternionIdentifyType;
import InterCode.SingleQuaternion;

public class GenerateText implements IGenerateText {

    /**
     * 获取一个四元式变量
     * @param builder 调用它的那个generateXXX中的那个StringBuilder
     * @param identify 要获取的四元式变量
     * @param paramIndex 是四元式中的第几个操作做数
     * @return 最终保存了这个四元式变量的值的寄存器
     */
    private String getIdentify(StringBuilder builder, QuaternionIdentify identify, int paramIndex) {
        // 分配了寄存器，直接返回寄存器号就行
        if (identify.isUseRegister) {
            return identify.getRegister();
        }
        // 是数字
        else if (identify.getType() == QuaternionIdentifyType.NUMBER) {
            // 如果是第一个操作数，还tm要先存在一个寄存器里面
            if (paramIndex == 1) {
                builder.append("\tli $t3, ").append(identify.getValue()).append("\n");
                return "$t3";
            }
            // 如果是第二个操作数，直接输出值就行
            else if (paramIndex == 2) {
                return identify.getValue();
            }
        }
        // 是全局变量
        else if (identify.getType() == QuaternionIdentifyType.GLOBAL) {
            String getCode = "\tlw $t" + paramIndex + ", " + identify.getValue() + "\n";
            builder.append(getCode);
            return "$t" + paramIndex;
        }
        // 是局部变量，要从栈里取
        else if (identify.getType() == QuaternionIdentifyType.LOCAL) {
            String getCode = "\tlw $t" + paramIndex + ", " + identify.getAddress() + "($sp)\n";
            builder.append(getCode);
            return "$t" + paramIndex;
        }
        return "ON-NO!";
    }

    /**
     * 把某一个四元式变量的值存进栈里面
     */
    private void setIdentifyIntoStack(StringBuilder builder, QuaternionIdentify identify) {
        identify.pushIntoStack();
        int address = identify.getAddress();
        builder.append("\tsw $t0, ").append(address).append("($sp)\n");
    }

    private String generatePlus(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2);

        mips.append("\tadd $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateMinu(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2);

        mips.append("\tsub $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateMult(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2);

        mips.append("\tmul $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateDiv(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2);

        mips.append("\tdiv $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateSetValue(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String variable;
        String value;

        // 是全局数据.data段的
        if (quaternion.getParam1().getType() == QuaternionIdentifyType.GLOBAL) {
            value = getIdentify(mips, quaternion.getParam2(), 2);
            mips.append("\tsw ").append(value).append(", ").append(quaternion.getParam1().getValue()).append("\n");
        }
        // 使用寄存器的局部变量，用li指令
        else if (quaternion.getParam1().isUseRegister) {
            variable = getIdentify(mips, quaternion.getParam1(), 1);
            value = getIdentify(mips, quaternion.getParam2(), 2);
            mips.append("\tli ").append(variable).append(", ").append(value);
        }
        // 存在栈里的局部变量，用sw指令
        else if (quaternion.getParam1().getType() == QuaternionIdentifyType.LOCAL) {
            variable = getIdentify(mips, quaternion.getParam1(), 1);
            value = getIdentify(mips, quaternion.getParam2(), 2);
            mips.append("\tsw ").append(value).append(", ").append(variable);
        }

        return mips.toString();
    }

    @Override
    public String generateText(SingleQuaternion quaternion) {
        Operation operation = quaternion.getOperation();
        StringBuilder mips = new StringBuilder();
        switch (operation) {
            case PLUS -> mips.append(generatePlus(quaternion));
            case MINU -> mips.append(generateMinu(quaternion));
            case MULT -> mips.append(generateMult(quaternion));
            case DIV -> mips.append(generateDiv(quaternion));
            case SET_VALUE -> mips.append(generateSetValue(quaternion));
        }
        return mips.toString();
    }
}
