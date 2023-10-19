package SymbolTable;

import Other.ParamResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.Array.ArrayDetail;
import SymbolTable.Function.FunctionDetail;

import java.util.ArrayList;

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
    private SymbolConst category;

    /**
     * 类型
     */
    private SymbolConst type;

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
    private ArrayDetail arrayLink;

    /**
     * 去往函数子表的链接
     */
    private FunctionDetail functionLink;

    /**
     * 作用域ID
     */
    private int scope;

    public MasterTableItem(String name, SymbolConst category, SymbolConst type, int scope) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.value = 0;
        this.hasAssigned = false;
        this.arrayLink = null;
        this.functionLink = null;
        this.scope = scope;
    }

    public void setArrayLink(ArrayDetail arrayLink) {
        this.arrayLink = arrayLink;
    }

    public void setFunctionLink(FunctionDetail functionLink) {
        this.functionLink = functionLink;
    }

    /**
     * 是否匹配
     * @param name 符号名
     */
    boolean match(String name) {
        if (!this.name.equals(name)) {
            return false;
        }
        return true;
    }

    public int getScope() {
        return scope;
    }

    public boolean isConstInt() {
        return this.category == SymbolConst.CONST && this.type == SymbolConst.INT;
    }

    public boolean isVarInt() {
        return this.category == SymbolConst.VAR && this.type == SymbolConst.INT;
    }

    public boolean isConstArray() {
        return this.category == SymbolConst.CONST && this.type == SymbolConst.ARRAY;
    }

    public boolean isVarArray() {
        return this.category == SymbolConst.VAR && this.type == SymbolConst.ARRAY;
    }

    public boolean isHasAssigned() {
        return this.hasAssigned;
    }

    public boolean isFunction() {
        return this.category == SymbolConst.FUNCTION && this.type == SymbolConst.NO_MEANING;
    }

    public void addIntoParams(MasterTableItem param) {
        this.functionLink.addIntoParams(param);
    }

    public ArrayList<MasterTableItem> getFunctionParamsList() {
        return this.functionLink.getParams();
    }
}
