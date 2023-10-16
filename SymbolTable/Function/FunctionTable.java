package SymbolTable.Function;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 函数子表所属的类
 */
public class FunctionTable {

    /**
     * 函数主表
     */
    private LinkedList<FunctionTableItem> table;

    /**
     * 唯一单例
     */
    private static FunctionTable instance;

    private FunctionTable() {
        this.table = new LinkedList<>();
    }

    static {
        FunctionTable.instance = new FunctionTable();
    }

    public static FunctionTable getInstance() {
        return instance;
    }
}
