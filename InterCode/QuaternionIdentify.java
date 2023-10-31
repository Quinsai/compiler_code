package InterCode;

public class QuaternionIdentify {

    /**
     * 把所有的东西都变成字符串
     */
    private String value;

    /**
     * 寄存器号
     */
    private int register;

    private boolean hasAllocatedRegister;

    public QuaternionIdentify(String value) {
        this.value = value;
        this.hasAllocatedRegister = false;
        this.register = -1;
    }

    public String getValue() {
        return value;
    }
}
