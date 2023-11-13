package SymbolTable;

import InterCode.QuaternionIdentify;
import Other.ParamResult;
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

    /**
     * 去往子作用域对应的子表的链接
     */
    private MasterTable subTableLink;

    /**
     * 对应的四元式中的变量
     */
    private QuaternionIdentify quaternionIdentify;

    /**
     * constExp的value
     */
    public int constValue;

    /**
     * 一维数组常量值
     */
    public ArrayList<Integer> constOneDArrayValue;

    /**
     * 二维数组常量值
     */
    public ArrayList<ArrayList<Integer>> constTwoDArrayValue;

    /**
     * 新定义一个有符号意义的条目
     */
    public MasterTableItem(String name, SymbolConst category, SymbolConst type, int scope) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.hasAssigned = false;
        this.arrayLink = null;
        this.functionLink = null;
        this.scope = scope;
        this.subTableLink = null;
        this.constOneDArrayValue = new ArrayList<>();
        this.constTwoDArrayValue = new ArrayList<>();
    }

    /**
     * 新定义一个仅仅表示指向子表的链接的条目
     */
    public MasterTableItem(MasterTable subTableLink) {
        this.subTableLink = subTableLink;
    }

    public void setArrayLink(ArrayDetail arrayLink) {
        this.arrayLink = arrayLink;
    }

    public void setFunctionLink(FunctionDetail functionLink) {
        this.functionLink = functionLink;
    }

    /**
     * 符号名是否匹配
     * @param name 符号名
     */
    boolean matchName(String name) {
        return this.subTableLink == null && this.name.equals(name);
    }

    /**
     * 符号名和作用域是否都匹配
     * @param name 符号名
     * @param scope 作用域
     */
    boolean matchNameAndScope(String name, int scope) {
        return matchName(name) && this.scope == scope;
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

    public QuaternionIdentify getQuaternionIdentify() {
        return quaternionIdentify;
    }

    public void setQuaternionIdentify(QuaternionIdentify quaternionIdentify) {
        this.quaternionIdentify = quaternionIdentify;
    }

    public MasterTable getSubTableLink() {
        return subTableLink;
    }

    public void setSubTableLink(MasterTable subTableLink) {
        this.subTableLink = subTableLink;
    }

    public void getArraySize(ParamResult<QuaternionIdentify> size1, ParamResult<QuaternionIdentify> size2) {
        size1.setValue(this.arrayLink.getSize1());
        size2.setValue(this.arrayLink.getSize2());
    }

    public void setArraySize(QuaternionIdentify size1, QuaternionIdentify size2) {
        this.arrayLink.setSize1(size1);
        this.arrayLink.setSize2(size2);
    }
}
