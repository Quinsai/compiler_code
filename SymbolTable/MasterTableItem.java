package SymbolTable;

import SymbolTable.Array.ArrayTableItem;
import SymbolTable.Function.FunctionTableItem;

/**
 * 符号表主表中的一行
 */
public class MasterTableItem {

    /**
     * 符号名
     */
    private String name;

    /**
     * 种类
     */
    private int category;

    /**
     * 类型
     */
    private int type;

    /**
     * 值
     */
    private int value;

    /**
     * 是否已经被赋值
     */
    private boolean hasAssigned;

    /**
     * 去往数组子表的链接
     */
    private ArrayTableItem arrayLink;

    /**
     * 去往函数子表的链接
     */
    private FunctionTableItem functionLink;

    public MasterTableItem(String name, int category, int type) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.value = 0;
        this.hasAssigned = false;
        this.arrayLink = null;
        this.functionLink = null;
    }

    public void setArrayLink(ArrayTableItem arrayLink) {
        this.arrayLink = arrayLink;
    }

    public void setFunctionLink(FunctionTableItem functionLink) {
        this.functionLink = functionLink;
    }
}
