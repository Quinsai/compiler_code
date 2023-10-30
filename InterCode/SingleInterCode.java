package InterCode;

/**
 * 单独一条中间代码的四元式
 */
public class SingleInterCode {

    public static String NO_MEANING = "NO_MEANING";

    /**
     * 操作符
     */
    Operation operation;

    /**
     * 第1个操作数
     */
    QuadrupleVariable param1;

    /**
     * 第2个操作数的名字
     */
    QuadrupleVariable param2;

    /**
     * 操作结果
     */
    QuadrupleVariable result;

    public SingleInterCode(Operation operation, QuadrupleVariable param1, QuadrupleVariable param2, QuadrupleVariable result) {
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
        this.result = result;
    }
}
