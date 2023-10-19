package SymbolTable;

import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.Array.ArrayDetail;
import SymbolTable.Function.FunctionDetail;
import Syntactic.SyntacticComponents.ComponentValueType;

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
        return this.name.equals(name);
    }

    public int getScope() {
        return scope;
    }

    /**
     * 是否可以被赋值
     */
    public boolean canBeAssigned() {
        if (this.category == SymbolConst.VAR) {
            return true;
        }
        else if (this.category == SymbolConst.CONST) {
            if (!this.hasAssigned) {
                this.hasAssigned = true;
                return true;
            }
            else {
                HandleError.handleError(AnalysisErrorType.ASSIGN_TO_CONST);
                return false;
            }
        }
        else if (this.category == SymbolConst.FUNCTION) {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TO_FUNCTION);
            return false;
        }
        else {
            HandleError.handleError(AnalysisErrorType.UNEXPECTED_ERROR);
            return false;
        }
    }

    public boolean isInt() {
        return this.type == SymbolConst.INT;
    }

    public boolean isOneDimensionArray() {
        return this.type == SymbolConst.ARRAY && this.arrayLink.getDimension() == 1;
    }

    public boolean isTwoDimensionArray() {
        return this.type == SymbolConst.ARRAY && this.arrayLink.getDimension() == 2;
    }

    public SymbolConst getFunctionReturnType() {
        return this.functionLink.getReturnType();
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

    public ComponentValueType getComponentValueType() {
        if (this.type == SymbolConst.INT) {
            return ComponentValueType.INT;
        }
        else if (this.type == SymbolConst.ARRAY && this.arrayLink.getDimension() == 1) {
            return ComponentValueType.ONE_DIMENSION_ARRAY;
        }
        else if (this.type == SymbolConst.ARRAY && this.arrayLink.getDimension() == 2) {
            return ComponentValueType.TWO_DIMENSION_ARRAY;
        }
        else {
            return ComponentValueType.NO_MEANING;
        }
    }
}
