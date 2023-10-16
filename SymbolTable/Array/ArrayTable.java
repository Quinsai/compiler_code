package SymbolTable.Array;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 数组子表所属的类
 */
public class ArrayTable {

    /**
     * 数组主表
     */
    private LinkedList<ArrayTableItem> table;

    /**
     * 唯一单例
     */
    private static ArrayTable instance;

    private ArrayTable() {
        this.table = new LinkedList<>();
    }

    static {
        ArrayTable.instance = new ArrayTable();
    }

    public static ArrayTable getInstance() {
        return instance;
    }
}
