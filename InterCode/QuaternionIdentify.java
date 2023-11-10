package InterCode;

import SymbolTable.MasterTableItem;

import java.util.ArrayList;

public class QuaternionIdentify {

    /**
     * 把所有的东西都变成字符串
     */
    private String value;

    /**
     * 对应的符号表中的条目
     */
    private MasterTableItem symbolTableItem;

    /**
     * 使用的寄存器号
     */
    private String register;

    /**
     * 在栈中的地址
     * 正常情况下应该是负数
     */
    private int address;

    /**
     * 是否使用了寄存器，如果没使用那就一定在栈中
     */
    public boolean isUseRegister;

    /**
     * 一串数组的值
     * 当且仅当操作数是INIT的时候有效
     */
    public ArrayList<QuaternionIdentify> arrayValue;

    private QuaternionIdentifyType type;

    public String id;

    /**
     * 这个count仅仅用于ID相关
     */
    static int count;

    /**
     * 当前这个“函数”里面的栈的下标
     */
    public static int stackIndex;

    static {
        count = 0;
        stackIndex = 0;
    }

    public QuaternionIdentify(String value) {
        this.value = value;
        this.isUseRegister = false;
        this.register = "";
        this.address = 0;
        this.arrayValue = new ArrayList<>();
        count ++;
        id = "V" + count;
        if (this.value.matches("^-?\\d+$")) {
            this.type = QuaternionIdentifyType.NUMBER;
        }
        else {
            this.type = QuaternionIdentifyType.LOCAL;
        }
    }

    public String getValue() {
        return value;
    }

    public MasterTableItem getSymbolTableItem() {
        return symbolTableItem;
    }

    public void setSymbolTableItem(MasterTableItem symbolTableItem) {
        this.symbolTableItem = symbolTableItem;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
        this.isUseRegister = true;
    }

    public int getAddress() {
        return address;
    }

    public void pushIntoStack() {
        this.address = stackIndex * 4;
        stackIndex ++;
    }

    public QuaternionIdentifyType getType() {
        return type;
    }

    public void setType(QuaternionIdentifyType type) {
        this.type = type;
    }
}
