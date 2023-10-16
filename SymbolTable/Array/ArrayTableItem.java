package SymbolTable.Array;

/**
 * 数组子表中的一行
 */
public class ArrayTableItem {

    /**
     * 维数
     */
    private int dimension;

    /**
     * 第一维的大小
     */
    private int firstSize;

    /**
     * 第二维的大小
     */
    private int secondSize;

    /**
     * 值
     */
    private int[][] value;

    public ArrayTableItem(int dimension, int firstSize, int secondSize) {
        this.dimension = dimension;
        this.firstSize = firstSize;
        this.secondSize = secondSize;
    }
}
