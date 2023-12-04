package InterCode;

import Optimize.Register.FunctionBlock;

public class SingleQuaternion {

    private Operation operation;

    private QuaternionIdentify param1;

    private QuaternionIdentify param2;

    private QuaternionIdentify result;

    public FunctionBlock functionBlock;

    public SingleQuaternion(Operation operation, QuaternionIdentify param1, QuaternionIdentify param2, QuaternionIdentify result) {
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
        this.result = result;
    }

    public Operation getOperation() {
        return operation;
    }

    public QuaternionIdentify getParam1() {
        return param1;
    }

    public QuaternionIdentify getParam2() {
        return param2;
    }

    public QuaternionIdentify getResult() {
        return result;
    }
}
