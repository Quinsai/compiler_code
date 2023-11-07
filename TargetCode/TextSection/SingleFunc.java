package TargetCode.TextSection;

import InterCode.Quaternion;
import InterCode.SingleQuaternion;

import java.util.LinkedList;

/**
 * 每一个函数都对应一个SingleFunc对象，其中存储了它对应的mips代码的字符串
 */
public class SingleFunc {

    /**
     * 目标的MIPS代码
     */
    private String funcCode;

    /**
     * 中间代码四元式
     */
    private LinkedList<SingleQuaternion> quaternions;

    public SingleFunc() {
        this.funcCode = "";
        this.quaternions = new LinkedList<>();
    }

    public void addIntoQuaternions(SingleQuaternion quaternion) {
        this.quaternions.addLast(quaternion);
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void generateFuncCode() {

    }
}
