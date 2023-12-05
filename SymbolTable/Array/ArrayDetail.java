package SymbolTable.Array;

import InterCode.QuaternionIdentify;

/**
 * 数组子表中的一行
 */
public class ArrayDetail {

    /**
     * 维数
     */
    private int dimension;

    private QuaternionIdentify size1;

    private QuaternionIdentify size2;

    public ArrayDetail(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    public QuaternionIdentify getSize1() {
        return size1;
    }

    public void setSize1(QuaternionIdentify size1) {
        this.size1 = size1;
    }

    public QuaternionIdentify getSize2() {
        return size2;
    }

    public void setSize2(QuaternionIdentify size2) {
        this.size2 = size2;
    }
}
