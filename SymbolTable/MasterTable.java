package SymbolTable;

import Other.ParamResult;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.Array.ArrayDetail;
import SymbolTable.Function.FunctionDetail;
import SymbolTable.Scope.ScopeStack;
import Syntactic.SyntacticComponents.ComponentValueType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 符号表主表所属的类
 */
public class MasterTable {

    /**
     * 符号表主表
     */
    private LinkedList<MasterTableItem> table;

    /**
     * 唯一单例
     */
    private static MasterTable masterTable;

    public MasterTable() {
        this.table = new LinkedList<>();
    }

    static {
        MasterTable.masterTable = new MasterTable();
    }

    public static MasterTable getMasterTable() {
        return masterTable;
    }

    /**
     * 这个方法会从后往前的查找item，而不考虑这个item是在子表或是在主表
     */
    public SymbolTableResult getItemByNameInAllTable(String name, int scope, ParamResult<MasterTableItem> result) {
        Iterator<MasterTableItem> masterTableItemIterator = this.table.descendingIterator();
        MasterTableItem item = null;
        SymbolTableResult res;
        boolean hasMatched = false;

        while (masterTableItemIterator.hasNext()) {
            item = masterTableItemIterator.next();
            MasterTable subTable = item.getSubTableLink();
            if (subTable != null) {
                res = subTable.getItemByNameInAllTable(name, scope, result);
                if (res == SymbolTableResult.EXIST) {
                    return res;
                }
            }
            else if (item.matchNameAndScope(name, scope)) {
                hasMatched = true;
                break;
            }
        }
        if (hasMatched) {
            res = SymbolTableResult.EXIST;
        }
        else {
            res = SymbolTableResult.NOT_EXIST;
        }
        result.setValue(item);
        return res;
    }

    /**
     * 从后往前地查找某一item
     * 注意，这个方法只查找能在目前被看见的
     * @param name   被查找的name
     * @param result 查找到的结果，若不存在则为null
     * @return 查找返回值
     */
    public SymbolTableResult getItemByNameInCurrentTable(String name, ParamResult<MasterTableItem> result) {
        Iterator<MasterTableItem> masterTableItemIterator = this.table.descendingIterator();
        MasterTableItem item = null;
        SymbolTableResult res;
        boolean hasMatched = false;
        while (masterTableItemIterator.hasNext()) {
            item = masterTableItemIterator.next();
            if (item.matchName(name)) {
                hasMatched = true;
                break;
            }
        }
        if (hasMatched) {
            res = SymbolTableResult.EXIST;
        }
        else {
            res = SymbolTableResult.NOT_EXIST;
        }
        result.setValue(item);
        return res;
    }

    /**
     * 当前作用域下这个名字是否重复
     */
    private boolean isNameRepeatInCurrentScope(String name) {

        int currentScope = ScopeStack.getInstance().getCurrentScope();
        SymbolTableResult res;
        ParamResult<MasterTableItem> former = new ParamResult<>(null);
        res = getItemByNameInCurrentTable(name, former);
        if (res == SymbolTableResult.EXIST && former.getValue().getScope() == currentScope) {
            HandleError.handleError(AnalysisErrorType.NAME_REPEAT);
            return true;
        }
        return false;
    }

    /**
     * 把常量或者变量插入
     * @param name 符号名
     * @param category 种类（常量、变量、函数）
     * @param type 类型（int，数组）
     * @param item 刚刚插入的item
     */
    public AnalysisResult insertIntoTable(String name, SymbolConst category, SymbolConst type, ParamResult<MasterTableItem> item) {

        if (isNameRepeatInCurrentScope(name)) {
            return AnalysisResult.FAIL;
        }

        MasterTableItem masterTableItem =  new MasterTableItem(name, category, type, ScopeStack.getInstance().getCurrentScope());
        this.table.addLast(masterTableItem);
        item.setValue(masterTableItem);
        return AnalysisResult.SUCCESS;
    }

    /**
     * 把函数插入
     * @param name 符号名
     * @param category 种类（常量、变量、函数）
     * @param type 类型（int，数组）
     * @param returnType 函数返回值类型
     * @param item 刚刚插入的值
     */
    public AnalysisResult insertIntoTable(String name, SymbolConst category, SymbolConst type, SymbolConst returnType, ParamResult<MasterTableItem> item) {

        if (isNameRepeatInCurrentScope(name)) {

            return AnalysisResult.FAIL;
        }

        FunctionDetail functionDetail = new FunctionDetail(returnType);
        MasterTableItem masterTableItem = new MasterTableItem(name, category, type, ScopeStack.getInstance().getCurrentScope());
        masterTableItem.setFunctionLink(functionDetail);
        this.table.addLast(masterTableItem);
        item.setValue(masterTableItem);
        return AnalysisResult.SUCCESS;
    }

    /**
     * 把数组插入
     * @param name 符号名
     * @param category 种类
     * @param type 类型
     * @param dimension 数组维度
     * @param item 刚刚插入的值
     */
    public AnalysisResult insertIntoTable(String name, SymbolConst category, SymbolConst type, int dimension, ParamResult<MasterTableItem> item) {

        if (isNameRepeatInCurrentScope(name)) {
            return AnalysisResult.FAIL;
        }

        ArrayDetail arrayDetail = new ArrayDetail(dimension);
        MasterTableItem masterTableItem = new MasterTableItem(name, category, type, ScopeStack.getInstance().getCurrentScope());
        masterTableItem.setArrayLink(arrayDetail);
        this.table.addLast(masterTableItem);
        item.setValue(masterTableItem);
        return AnalysisResult.SUCCESS;
    }

    public AnalysisResult checkDeclare(String name) {
        if (isNameRepeatInCurrentScope(name)) {
            HandleError.handleError(AnalysisErrorType.NAME_REPEAT);
            return AnalysisResult.FAIL;
        }
        return AnalysisResult.SUCCESS;
    }

    /**
     * 检查是否可以赋值
     */
    public AnalysisResult checkAssign(String name) {
        ParamResult<MasterTableItem> item = new ParamResult<>(null);
        if (getItemByNameInCurrentTable(name, item) == SymbolTableResult.NOT_EXIST) {
            HandleError.handleError(AnalysisErrorType.IDENTIFIER_NOT_DEFINE);
            return AnalysisResult.FAIL;
        }

        if (!item.getValue().canBeAssigned()) {
            return AnalysisResult.FAIL;
        }
        return AnalysisResult.SUCCESS;
    }

    /**
     * 检查是否可以被引用
     */
    public AnalysisResult checkReference(String name) {
        ParamResult<MasterTableItem> item = new ParamResult<>(null);
        if (getItemByNameInCurrentTable(name, item) == SymbolTableResult.NOT_EXIST) {
            HandleError.handleError(AnalysisErrorType.IDENTIFIER_NOT_DEFINE);
            return AnalysisResult.FAIL;
        }
        return AnalysisResult.SUCCESS;
    }

    /**
     * 用以在把函数名添加进符号表后设置它的参数
     * 是出于这样的一种考虑的：
     * 既然在把函数添加进符号表后，接下来读取的就是它的参数们，并且它的参数们也会被添加进符号表，
     * 那么就可以在先把它的参数们添加进符号表后，再配置这个函数的参数
     * 具体将在读取到)的时候执行
     * 注意：还有一件事情，如果在读到{的时候，这个函数依旧没有被定义参数，那么这个函数定义失败，需要从符号表中移除
     * @param name 函数的名字
     */
    public AnalysisResult setFunctionParams(String name) {

        ParamResult<MasterTableItem> functionItem = new ParamResult<>(null);
        SymbolTableResult res = getItemByNameInCurrentTable(name, functionItem);

        if (res == SymbolTableResult.NOT_EXIST) {
            HandleError.handleError(AnalysisErrorType.IDENTIFIER_NOT_DEFINE);
            return AnalysisResult.FAIL;
        }

        int index = this.table.lastIndexOf(functionItem.getValue());
        ListIterator<MasterTableItem> listIterator = this.table.listIterator(index);
        listIterator.next();

        while (listIterator.hasNext()) {
            MasterTableItem item = listIterator.next();
            functionItem.getValue().addIntoParams(item);
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * 获取函数参数列表
     * @param name 函数名
     * @param params 参数列表
     */
    private AnalysisResult getFunctionParams(String name, ParamResult<MasterTableItem[]> params) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item = new ParamResult<>(null);

        res = getItemByNameInCurrentTable(name, item);
        if (res == SymbolTableResult.NOT_EXIST) {
            HandleError.handleError(AnalysisErrorType.IDENTIFIER_NOT_DEFINE);
            return AnalysisResult.FAIL;
        }

        if (!item.getValue().isFunction()) {
            HandleError.handleError(AnalysisErrorType.NOT_FUNCTION);
            return AnalysisResult.FAIL;
        }

        ArrayList<MasterTableItem> paramsList = item.getValue().getFunctionParamsList();
        int size = paramsList.size();
        MasterTableItem[] paramsArray = new MasterTableItem[size];
        for (int i = 0; i < size; i++) {
            paramsArray[i] = paramsList.get(i);
        }
        params.setValue(paramsArray);

        return AnalysisResult.SUCCESS;
    }

    /**
     * 检查函数参数
     */
    public AnalysisResult checkFunctionParams(String name, ArrayList<ComponentValueType> realParams) {
        AnalysisResult res;
        ParamResult<MasterTableItem[]> formParamsReturn = new ParamResult<>(null);

        res = getFunctionParams(name, formParamsReturn);
        if (res == AnalysisResult.FAIL) {
            return res;
        }

        MasterTableItem[] formParams = formParamsReturn.getValue();
        int length = realParams.size();
        if (formParams.length != length) {
            HandleError.handleError(AnalysisErrorType.FUNCTION_PARAMS_NUMBER_NOT_MATCH);
            return AnalysisResult.FAIL;
        }

        for (int i = 0; i < length; i++) {

            ComponentValueType realType = realParams.get(i);

            if (formParams[i].isInt() && realType == ComponentValueType.INT) {
                continue;
            }
            else if (formParams[i].isOneDimensionArray() && realType == ComponentValueType.ONE_DIMENSION_ARRAY) {
                continue;
            }
            else if (formParams[i].isTwoDimensionArray() && realType == ComponentValueType.TWO_DIMENSION_ARRAY) {
                continue;
            }
            else {
                HandleError.handleError(AnalysisErrorType.FUNCTION_PARAMS_TYPE_NOT_MATCH);
                return AnalysisResult.FAIL;
            }
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * 获取函数返回值类型
     */
    public AnalysisResult getFunctionReturnType(String name, ParamResult<SymbolConst> returnType) {

        ParamResult<MasterTableItem> item = new ParamResult<>(null);
        SymbolTableResult res;

        res = getItemByNameInCurrentTable(name, item);
        if (res == SymbolTableResult.NOT_EXIST) {
            HandleError.handleError(AnalysisErrorType.IDENTIFIER_NOT_DEFINE);
            return AnalysisResult.FAIL;
        }

        MasterTableItem functionItem = item.getValue();
        if (!functionItem.isFunction()) {
            HandleError.handleError(AnalysisErrorType.NOT_FUNCTION);
            return AnalysisResult.FAIL;
        }

        returnType.setValue(functionItem.getFunctionReturnType());
        return AnalysisResult.SUCCESS;
    }

    public AnalysisResult getComponentValueType(String name, ParamResult<ComponentValueType> returnValueType) {

        ParamResult<MasterTableItem> returnItem = new ParamResult<>(null);
        SymbolTableResult res;

        res = getItemByNameInCurrentTable(name, returnItem);
        if (res == SymbolTableResult.NOT_EXIST) {
            HandleError.handleError(AnalysisErrorType.IDENTIFIER_NOT_DEFINE);
            return AnalysisResult.FAIL;
        }

        MasterTableItem item = returnItem.getValue();
        returnValueType.setValue(item.getComponentValueType());
        return AnalysisResult.SUCCESS;
    }

    /**
     * 在退出某一作用域的时候，将其对应的条目挪动到子表中
     */
    public void moveItemIntoSubTable(int scope) {

        MasterTable subTable = new MasterTable();
        MasterTableItem linkItem = new MasterTableItem(subTable);

        while (!this.table.isEmpty()) {
            MasterTableItem item = this.table.getLast();
            if (item.getScope() == scope) {
                subTable.table.addFirst(item);
                this.table.removeLast();
            }
            else {
                break;
            }
        }

        this.table.addLast(linkItem);
    }

    public AnalysisResult insertFailDeclareIdentifier(String name) {
        if (isNameRepeatInCurrentScope(name)) {
            HandleError.handleError(AnalysisErrorType.NAME_REPEAT);
            return AnalysisResult.FAIL;
        }

        MasterTableItem masterTableItem =  new MasterTableItem(name, SymbolConst.NO_MEANING, SymbolConst.NO_MEANING, ScopeStack.getInstance().getCurrentScope());
        this.table.addLast(masterTableItem);
        return AnalysisResult.SUCCESS;
    }
}
