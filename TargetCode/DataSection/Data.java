package TargetCode.DataSection;

import InterCode.Quaternion;
import InterCode.SingleQuaternion;

import java.util.LinkedList;

/**
 * MIPS中的.data段
 */
public class Data {

    private String dataCode;

    /**
     * 中间代码四元式
     */
    private LinkedList<SingleQuaternion> quaternions;

    public Data() {
        this.dataCode = ".data\n";
        this.quaternions = new LinkedList<>();
    }

    public void addIntoQuaternions(SingleQuaternion quaternion) {
        this.quaternions.addLast(quaternion);
    }

    public String getDataCode() {
        return dataCode;
    }

    /**
     * 生成.data段的方法
     */
    public void generateDataCode() {

    }
}
