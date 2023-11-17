package TargetCode.TextSection;

import InterCode.*;
import Other.ParamResult;
import TargetCode.Target;

public class GenerateText implements IGenerateText {

    private int numOfFuncCallBegin;

    private int numOfFuncCallEnd;

    private static final int spaceOfFunc = 504;

    public GenerateText() {
        this.numOfFuncCallBegin = 0;
        this.numOfFuncCallEnd = 0;
    }

    /**
     * 获取一个四元式变量
     * @param builder 调用它的那个generateXXX中的那个StringBuilder
     * @param identify 要获取的四元式变量
     * @param paramIndex 是四元式中的第几个操作做数
     * @return 最终保存了这个四元式变量的值的寄存器
     */
    private String getIdentify(StringBuilder builder, QuaternionIdentify identify, int paramIndex, boolean canBeNumber) {
        // 分配了寄存器，直接返回寄存器号就行
        if (identify.isUseRegister) {
            return identify.getRegister();
        }
        // 是数字
        else if (identify.getType() == QuaternionIdentifyType.NUMBER) {
            // 如果不能直接用数字，还tm要先存在一个寄存器里面
            if (!canBeNumber) {
                builder.append("\tli $t3, ").append(identify.getValue()).append("\n");
                return "$t3";
            }
            // 如果能直接用数字
            else if (canBeNumber) {
                return identify.getValue();
            }
        }
        // 是全局变量
        else if (identify.getType() == QuaternionIdentifyType.GLOBAL) {
            String getCode;
            if (identify.isArrayIdentify) {
                getCode = "\tla $t" + paramIndex + ", " + identify.getValue() + "\n";
            }
            else {
                getCode = "\tlw $t" + paramIndex + ", " + identify.getValue() + "\n";
            }
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
        if (!identify.isInStack) {
            identify.pushIntoStack();
        }
        int address = identify.getAddress();
        builder.append("\tsw $t0, ").append(address).append("($sp)\n");
    }

    private String generatePlus(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tadd $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateMinu(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tsub $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateMult(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tmul $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateDiv(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tdiv $t0, ").append(param1).append(", ").append(param2).append("\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateMod(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, false);

        mips.append("\tdiv ").append(param1).append(", ").append(param2).append("\n");
        mips.append("\tmfhi $t0\n");

        setIdentifyIntoStack(mips, quaternion.getResult());
        return mips.toString();
    }

    private String generateSetValue(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String variable;
        String value;

        // 是全局数据.data段的
        if (quaternion.getParam1().getType() == QuaternionIdentifyType.GLOBAL) {
            value = getIdentify(mips, quaternion.getParam2(), 2, false);
            mips.append("\tsw ").append(value).append(", ").append(quaternion.getParam1().getValue()).append("\n");
        }
        // 使用寄存器的局部变量，用li指令
        else if (quaternion.getParam1().isUseRegister) {
            variable = getIdentify(mips, quaternion.getParam1(), 1, false);
            value = getIdentify(mips, quaternion.getParam2(), 2, true);
            mips.append("\tli ").append(variable).append(", ").append(value);
        }
        // 存在栈里的局部变量，用sw指令
        else if (quaternion.getParam1().getType() == QuaternionIdentifyType.LOCAL) {
            value = getIdentify(mips, quaternion.getParam2(), 2, true);
            // 是数字
            if (value.matches("^-?\\d+$")) {
                mips.append("\tli $t0, ").append(value).append("\n");
            }
            else {
                mips.append("\tmove $t0, ").append(value).append("\n");
            }
            setIdentifyIntoStack(mips, quaternion.getParam1());
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

        String arrayHead = getIdentify(mips, array, 1, false);

        /*int index = 0;
        for (int i = 0; i < size1; i++) {
            // 一维数组
            if (dimension == 1) {

            }
            // 二维数组
            else {

                mips.append("\tlw $t4, ").append(i * 4).append("($t1)\n");

                QuaternionIdentify firstDimensionValue = value.arrayValue.get(i);
                for (int j = 0; j < size2; j++) {
                    QuaternionIdentify identify = firstDimensionValue.arrayValue.get(j);
                    String v = getIdentify(mips, identify, 2, false);
                    mips.append("\tsw ").append(v).append(", ").append(j * 4).append("($t4)\n");
                    index ++;
                }
            }
        }*/

        if (dimension == 1) {
            for (int i = 0; i < size1; i++) {
                QuaternionIdentify identify = value.arrayValue.get(i);
                String v = getIdentify(mips, identify, 2, false);
                mips.append("\tsw ").append(v).append(", ").append(i * 4).append("(").append(arrayHead).append(")\n");
            }
        }
        else {
            for (int i = 0; i < size1; i++) {
                QuaternionIdentify firstDimensionValue = value.arrayValue.get(i);
                for (int j = 0; j < size2; j++) {
                    QuaternionIdentify identify = firstDimensionValue.arrayValue.get(j);
                    String v = getIdentify(mips, identify, 2, false);
                    mips.append("\tsw ").append(v).append(", ").append(((i * size2) + j) * 4).append("(").append(arrayHead).append(")\n");
                }
            }
        }

        return mips.toString();
    }

    private String generateGetValue(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String address = getIdentify(mips, quaternion.getParam1(), 1, false);

        mips.append("\tlw $t0, 0(").append(address).append(")\n");

        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateMainFuncBegin(SingleQuaternion quaternion) {
        return "main_begin:\n";
    }

    private String generateMainFuncEnd(SingleQuaternion quaternion) {

        return """
            main_end:
            \tli $v0, 10
            \tsyscall
            """;
    }

    private String generateFuncBegin(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String funcName = quaternion.getParam1().getValue();

        QuaternionIdentify.updateStackIndexWhenEnterNewFunction();

        int levelOfFuncCallNest = numOfFuncCallBegin - numOfFuncCallEnd + 1;

        mips.append(funcName).append("_begin:\n");
        // 500-504字节的空间用来保存$ra返回位置寄存器的值，以防在递归调用中炸了
        mips.append("\tsw $ra, 0($sp)\n");
        // 记得要先+1，别把返回地址给覆盖了
        QuaternionIdentify.stackIndex ++;
        // 受不了了，我赌它一个函数里不会有超过30个参数！
        QuaternionIdentify.stackIndex += 30;
        return mips.toString();
    }

    private String generateFuncEnd(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String funcName = quaternion.getParam1().getValue();

        int levelOfFuncCallNest = numOfFuncCallBegin - numOfFuncCallEnd + 1;

        mips.append(funcName).append("_end:\n");
        // 它不得不迎来终结，不得不释放它的504个字节的岁月静好
        // 去直面它的负重前行
        // 记得回去的地址要先从500-504字节的空间中取到$ra中先
        mips.append("\tlw $ra, 0($sp)\n");
        mips.append("\tjr $ra\n");

        QuaternionIdentify.recoverStackIndexWhenQuitFucntion();

        return mips.toString();
    }

    private String generateFormalParaInt(SingleQuaternion quaternion) {

        int count = Integer.parseInt(quaternion.getResult().getValue());

        // 可以使用a系列寄存器来存参数
//        if (count <= 4) {
//            quaternion.getParam1().setType(QuaternionIdentifyType.PARAM);
//            quaternion.getParam1().setRegister("$a" + (count - 1));
//        }
        // 超出4个，必须使用栈来传递参数
//        else {
//            quaternion.getParam1().setType(QuaternionIdentifyType.PARAM);
//            quaternion.getParam1().setAddress((count - 4) * 4);
//        }

        quaternion.getParam1().setAddress(count * 4);

        return "";
    }

    private String generateFormalParaArray(SingleQuaternion quaternion) {

        int count = Integer.parseInt(quaternion.getResult().getValue());

        StringBuilder mips = new StringBuilder();
        int dimension = 1;

        if (quaternion.getParam2() != null) {
            dimension ++;
        }

        quaternion.getParam1().setType(QuaternionIdentifyType.PARAM);
        quaternion.getParam1().isArrayParam = true;

        // TODO 数组作为函数形参
        /*if (count <= 4) {
            quaternion.getParam1().setRegister("$a" + (count - 1));
        }
        else {
            quaternion.getParam1().setAddress((count - 4) * 4);
        }*/

        return mips.toString();
    }

    private String generateReturn(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String funcName = quaternion.getParam1().getValue();

        // 确有返回值
        if (quaternion.getParam2() != null) {
            String returnValue = getIdentify(mips, quaternion.getParam2(), 2, false);
            mips.append("\tmove $v0, ").append(returnValue).append("\n");
        }

        mips.append("\tj ").append(funcName).append("_end\n");

        return mips.toString();
    }

    private String generateFuncCallBegin(SingleQuaternion quaternion) {

//        if (quaternion.getResult() != null) {
//            quaternion.getResult().setRegister("$v0");
//        }

        this.numOfFuncCallBegin ++;

        return "";
    }

    private String generateRealPara(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        int count = Integer.parseInt(quaternion.getParam2().getValue());

        // 就是像这样
        // int a = func1(12, func2(10));
        // 这么写的话，在执行func1的时候，实际上的第一个参数是10，而不是12
        // 这里怎么办，只有天知道
        // 一个可能可以的办法是，在函数调用的时候就移动栈顶指针

        // 前4个参数，可以使用a系列寄存器来存参数
        // 全tm给我滚到栈里面去！

        int levelOfFuncCallNest = this.numOfFuncCallBegin - this.numOfFuncCallEnd;

        if (count <= -1) {
            // 不是数组，只是单纯的值
            if (!quaternion.getParam1().isArray()) {
                // 直接写一个数字
                if (quaternion.getParam1().getType() == QuaternionIdentifyType.NUMBER) {
                    mips.append("\tli $a").append(count - 1).append(", ").append(quaternion.getParam1().getValue()).append("\n");
                }
                // 是一个变量
                else {
                    String param = getIdentify(mips, quaternion.getParam1(), 1, false);
                    mips.append("\tmove $a").append(count - 1).append(", ").append(param).append("\n");
                }
            }
            // TODO 数组作为实参，保存在a系列寄存器中
            // 是数组
            else {

                String array = getIdentify(mips, quaternion.getParam1(), 1, false);

                mips.append("\tmove $a").append(count - 1).append(", ").append(array).append("\n");
            }
        }
        // 4个以外的参数，只能存在栈中
        else {
            // 不是数组，只是单纯的值
            if (!quaternion.getParam1().isArray()) {
                String param = getIdentify(mips, quaternion.getParam1(), 1, false);
                mips.append("\tsw ").append(param).append(", ").append(count * 4 - spaceOfFunc * levelOfFuncCallNest).append("($sp)\n");
            }
            // TODO 数组作为实参，保存在栈中
            // 是数组
            else {

                String array = getIdentify(mips, quaternion.getParam1(), 1, false);

                mips.append("\tsw ").append(array).append(", ").append(count * 4 - spaceOfFunc * levelOfFuncCallNest).append("($sp)\n");
            }
        }

        return mips.toString();
    }

    private String generateFuncCallEnd(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String funcName = quaternion.getParam1().getValue();

        int levelOfFuncCallNest = this.numOfFuncCallBegin - this.numOfFuncCallEnd;

        // 给接下来的这个函数开辟一个504字节的不会被打扰的空间
        // 让它岁月静好
        mips.append("\tsubi $sp, $sp, ").append(spaceOfFunc * levelOfFuncCallNest).append("\n");

        mips.append("\tjal ").append(funcName).append("_begin\n");

        mips.append("\taddi $sp, $sp, ").append(spaceOfFunc * levelOfFuncCallNest).append("\n");

        QuaternionIdentify result = quaternion.getResult();
        if (result != null) {
            if (!result.isInStack) {
                result.pushIntoStack();
            }
            int address = result.getAddress();
            mips.append("\tsw $v0, ").append(address).append("($sp)\n");
        }

        this.numOfFuncCallEnd ++;

        return mips.toString();
    }

    private String generateOppo(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param = getIdentify(mips, quaternion.getParam1(), 1, false);

        mips.append("\tneg $t0, ").append(param).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateOr(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tor $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateAnd(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tand $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateGreat(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, false);

        mips.append("\tsgt $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateGreatEqual(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, false);

        mips.append("\tsge $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateLittle(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, false);

        mips.append("\tslt $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateLittleEqual(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, false);

        mips.append("\tsle $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateNotEqual(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tsne $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateEqual(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param1 = getIdentify(mips, quaternion.getParam1(), 1, false);
        String param2 = getIdentify(mips, quaternion.getParam2(), 2, true);

        mips.append("\tseq $t0, ").append(param1).append(", ").append(param2).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateNot(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String param = getIdentify(mips, quaternion.getParam1(), 1, false);

        mips.append("\tnot $t0, ").append(param).append("\n");
        setIdentifyIntoStack(mips, quaternion.getResult());

        return mips.toString();
    }

    private String generateLabel(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String labelId = quaternion.getParam1().id;

        mips.append("label_").append(labelId).append(":\n");

        return mips.toString();
    }

    private String generateSkip(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String labelId = quaternion.getParam1().id;

        mips.append("\tj label_").append(labelId).append("\n");

        return mips.toString();
    }

    private String generateBranchIfFalse(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String condition = getIdentify(mips, quaternion.getParam1(), 1, false);
        String labelId = quaternion.getParam2().id;

        mips.append("\tbeqz ").append(condition).append(", label_").append(labelId).append("\n");
        return mips.toString();
    }

    private String generatePrintString(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String stringId = quaternion.getParam1().id;
        String stringContent = quaternion.getParam1().getValue();

        String stringData = "\tstring_" + stringId + ": " +
            ".asciiz \"" + stringContent + "\"\n";
        Target.getInstance().getData().addIntoDataCode(stringData);

        mips.append("\tla $a0, string_").append(stringId).append("\n");
        mips.append("\tli $v0, 4\n");
        mips.append("\tsyscall\n");

        return mips.toString();
    }

    private String generatePrintInt(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String output = getIdentify(mips, quaternion.getParam1(), 1, false);

        mips.append("\tmove $a0, ").append(output).append("\n");
        mips.append("\tli $v0, 1\n");
        mips.append("\tsyscall\n");

        return mips.toString();
    }

    private String generateGetint(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();

        mips.append("\tli $v0, 5\n");
        mips.append("\tsyscall\n");
        mips.append("\tmove $t0, $v0\n");

        setIdentifyIntoStack(mips, quaternion.getParam1());

        return mips.toString();
    }

    private String generateIntDeclare(SingleQuaternion quaternion) {
        quaternion.getResult().pushIntoStack();
        return "";
    }

    // TODO 数组定义
    private String generateArrayDeclare(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        int dimension = 1;
        int size1 = 0;
        int size2 = 0;

        if (quaternion.getParam2() != null) {
            dimension ++;
        }
        size1 = Integer.parseInt(quaternion.getParam1().getValue());
        if (dimension == 2) {
            size2 = Integer.parseInt(quaternion.getParam2().getValue());
        }

        if (dimension == 1) {

            mips.append("\tla $t0, ").append(4 * (QuaternionIdentify.stackIndex + 1)).append("($sp)\n");
            setIdentifyIntoStack(mips, quaternion.getResult());
            QuaternionIdentify.stackIndex ++;

            QuaternionIdentify.stackIndex += size1;
        }
        else {

            mips.append("\tla $t0, ").append(4 * (QuaternionIdentify.stackIndex + 1)).append("($sp)\n");
            setIdentifyIntoStack(mips, quaternion.getResult());
            QuaternionIdentify.stackIndex ++;

            QuaternionIdentify.stackIndex += size1 * size2;
        }

        return mips.toString();
    }

    private String generateGetAddress(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String offset = getIdentify(mips, quaternion.getParam2(), 2, false);

        // TODO 这里要改
        if (quaternion.getParam1().getType() == QuaternionIdentifyType.GLOBAL) {
            String name = quaternion.getParam1().getValue();
            mips.append("\tmul $t1, ").append(offset).append(", 4\n");
            mips.append("\tla $t2, ").append(name).append("\n");
            mips.append("\tadd $t0, $t1, $t2\n");
            setIdentifyIntoStack(mips, quaternion.getResult());
        }
        else if (quaternion.getParam1().getType() == QuaternionIdentifyType.LOCAL) {
            mips.append("\tmul $t2, ").append(offset).append(", 4\n");
            String address = getIdentify(mips, quaternion.getParam1(), 1, false);
            mips.append("\tadd $t0, $t2, ").append(address).append("\n");
            setIdentifyIntoStack(mips, quaternion.getResult());

        }

        return mips.toString();
    }

    private String generateStoreToAddress(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String value = getIdentify(mips, quaternion.getParam2(), 2, false);
        String address = getIdentify(mips, quaternion.getParam1(), 1, false);

        mips.append("\tsw ").append(value).append(", 0(").append(address).append(")\n");

        return mips.toString();
    }

    private String generateGetintToAddress(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String address = getIdentify(mips, quaternion.getParam1(), 1, false);

        mips.append("\tli $v0, 5\n");
        mips.append("\tsyscall\n");
        mips.append("\tsw $v0, 0(").append(address).append(")\n");

        return mips.toString();
    }

    private String generateGetArrayHeadAddress(SingleQuaternion quaternion) {

        StringBuilder mips = new StringBuilder();
        String array = getIdentify(mips, quaternion.getParam1(), 1, false);

        mips.append("\tmove $t0, ").append(array).append("\n");
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
            case MOD -> mips.append(generateMod(quaternion));
            case SET_VALUE -> mips.append(generateSetValue(quaternion));
            case ARRAY_INIT -> mips.append(generateArrayInit(quaternion));
            case GET_VALUE -> mips.append(generateGetValue(quaternion));
            case MAIN_FUNC_BEGIN -> mips.append(generateMainFuncBegin(quaternion));
            case MAIN_FUNC_END -> mips.append(generateMainFuncEnd(quaternion));
            case FUNC_BEGIN -> mips.append(generateFuncBegin(quaternion));
            case FUNC_END -> mips.append(generateFuncEnd(quaternion));
            case FORMAL_PARA_INT -> mips.append(generateFormalParaInt(quaternion));
            case FORMAL_PARA_ARRAY -> mips.append(generateFormalParaArray(quaternion));
            case RETURN -> mips.append(generateReturn(quaternion));
            case FUNC_CALL_BEGIN -> mips.append(generateFuncCallBegin(quaternion));
            case REAL_PARA -> mips.append(generateRealPara(quaternion));
            case FUNC_CALL_END -> mips.append(generateFuncCallEnd(quaternion));
            case OPPO -> mips.append(generateOppo(quaternion));
            case OR -> mips.append(generateOr(quaternion));
            case AND -> mips.append(generateAnd(quaternion));
            case GREAT -> mips.append(generateGreat(quaternion));
            case GREAT_EQUAL -> mips.append(generateGreatEqual(quaternion));
            case LITTLE -> mips.append(generateLittle(quaternion));
            case LITTLE_EQUAL -> mips.append(generateLittleEqual(quaternion));
            case NOT_EQUAL -> mips.append(generateNotEqual(quaternion));
            case EQUAL -> mips.append(generateEqual(quaternion));
            case NOT -> mips.append(generateNot(quaternion));
            case LABEL -> mips.append(generateLabel(quaternion));
            case SKIP -> mips.append(generateSkip(quaternion));
            case BRANCH_IF_FALSE -> mips.append(generateBranchIfFalse(quaternion));
            case PRINT_STRING -> mips.append(generatePrintString(quaternion));
            case PRINT_INT -> mips.append(generatePrintInt(quaternion));
            case GETINT -> mips.append(generateGetint(quaternion));
            case VAR_INT_DECLARE, CONST_INT_DECLARE -> mips.append(generateIntDeclare(quaternion));
            case VAR_ARRAY_DECLARE, CONST_ARRAY_DECLARE -> mips.append(generateArrayDeclare(quaternion));
            case GET_ADDRESS -> mips.append(generateGetAddress(quaternion));
            case STORE_TO_ADDRESS -> mips.append(generateStoreToAddress(quaternion));
            case GETINT_TO_ADDRESS -> mips.append(generateGetintToAddress(quaternion));
            case GET_ARRAY_HEAD_ADDRESS -> mips.append(generateGetArrayHeadAddress(quaternion));
        }
        return mips.toString();
    }
}
