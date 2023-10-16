package SymbolTable.Function;

import SymbolTable.MasterTableItem;

/**
 * 函数子表中的一行
 */
public class FunctionTableItem {

    /**
     * 返回值类型
     */
    private int returnType;

    /**
     * 参数列表
     */
    private MasterTableItem[] params;

    public FunctionTableItem(int returnType, MasterTableItem[] params) {
        this.returnType = returnType;
        this.params = params;
    }
}
