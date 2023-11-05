package InterCode;

import SymbolTable.MasterTableItem;

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
     * 寄存器号
     */
    private int register;

    private boolean hasAllocatedRegister;

    public String id;

    static int count;

    static {
        count = 0;
    }

    public QuaternionIdentify(String value) {
        this.value = value;
        this.hasAllocatedRegister = false;
        this.register = -1;
        count ++;
        id = "V" + count;
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
}
