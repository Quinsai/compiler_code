package SymbolTable.Function;

import SymbolTable.MasterTableItem;

import java.util.ArrayList;

/**
 * 函数子表中的一行
 */
public class FunctionDetail {

    /**
     * 返回值类型
     */
    private int returnType;

    /**
     * 参数列表
     */
    private ArrayList<MasterTableItem> params;

    private boolean isSetParams;

    public FunctionDetail(int returnType) {
        this.returnType = returnType;
        this.params = new ArrayList<>();
        this.isSetParams = false;
    }

    public void addIntoParams(MasterTableItem param) {
        this.params.add(param);
        this.isSetParams = true;
    }
}
