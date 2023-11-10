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
     * 默认它的值现在在$t0里面
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

    private String generateArrayInit(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        QuaternionIdentify value = quaternion.getParam2();
        QuaternionIdentify array = quaternion.getParam1();
        int dimension = 1;
        int size1 = 0;
        int size2 = 0;

        if (!value.arrayValue.get(0).arrayValue.isEmpty()) {
            dimension ++;
        }
        size1 = value.arrayValue.size();
        if (dimension == 2) {
            size2 = value.arrayValue.get(0).arrayValue.size();
        }

        mips.append("\tla $t0, ").append(array.getValue()).append("\n");

        int index = 0;
        for (int i = 0; i < size1; i++) {
            // 一维数组
            if (dimension == 1) {
                QuaternionIdentify identify = value.arrayValue.get(i);
                String v = getIdentify(mips, identify, 2);
                mips.append("\tsw ").append(v).append(", ").append(index * 4).append("($t0)\n");
                index ++;
            }
            // 二维数组
            else {
                QuaternionIdentify firstDimensionValue = value.arrayValue.get(i);
                for (int j = 0; j < size2; j++) {
                    QuaternionIdentify identify = firstDimensionValue.arrayValue.get(j);
                    String v = getIdentify(mips, identify, 2);
                    mips.append("\tsw ").append(v).append(", ").append(index * 4).append("($t0)\n");
                    index ++;
                }
            }
        }

        return mips.toString();
    }

    private String generateGetValue(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String address = quaternion.getParam1().getValue();
        String offset = getIdentify(mips, quaternion.getParam2(), 1);

        mips.append("\tmul ").append(offset).append(", ").append(offset).append(", 4\n");
        mips.append("\tlw $t0, ").append(address).append("(").append(offset).append(")\n");

        setIdentifyIntoStack(mips, quaternion.getResult());

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
            case ARRAY_INIT -> mips.append(generateArrayInit(quaternion));
            // ADDRESS和GET_VALUE一定是接连出现的，因此可以把它们写在一起
            case GET_VALUE -> mips.append(generateGetValue(quaternion));
        }
        return mips.toString();
    }
}
