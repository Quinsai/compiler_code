package InterCode;

public class SingleQuaternion {

    Operation operation;

    QuaternionIdentify param1;

    QuaternionIdentify param2;

    QuaternionIdentify result;

    public SingleQuaternion(Operation operation, QuaternionIdentify param1, QuaternionIdentify param2, QuaternionIdentify result) {
        this.operation = operation;
        this.param1 = param1;
        this.param2 = param2;
        this.result = result;
    }
}
