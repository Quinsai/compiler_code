package SymbolTable;

import Other.ParamResult;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.Array.ArrayDetail;
import SymbolTable.Function.FunctionDetail;

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
    private static MasterTable instance;

    private MasterTable() {
        this.table = new LinkedList<>();
    }

    static {
        MasterTable.instance = new MasterTable();
    }

    public static MasterTable getInstance() {
        return instance;
    }

    /**
     * 从后往前地查找某一item
     * @param name   被查找的name
     * @param result 查找到的结果，若不存在则为null
     * @return 查找返回值
     */
    private SymbolTableResult getItemByName(String name, ParamResult<MasterTableItem> result) {
        Iterator<MasterTableItem> masterTableItemIterator = this.table.descendingIterator();
        MasterTableItem item = null;
        SymbolTableResult res;
        while (masterTableItemIterator.hasNext()) {
            item = masterTableItemIterator.next();
            if (item.match(name)) {
                break;
            }
        }
        if (item != null) {
            res = SymbolTableResult.EXIST;
        }
        else {
            res = SymbolTableResult.NOT_EXIST;
        }
        result.setValue(item);
        return res;
    }

    /**
     * 把常量或者变量插入
     * @param name 符号名
     * @param category 种类（常量、变量、函数）
     * @param type 类型（int，数组）
     * @param item 刚刚插入的item
     * @param currentScope 现在的作用域ID
     */
    public AnalysisResult insertIntoTable(String name, SymbolConst category, SymbolConst type, int currentScope, ParamResult<MasterTableItem> item) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> former = new ParamResult<>(null);
        res = getItemByName(name, former);
        if (res == SymbolTableResult.EXIST && former.getValue().getScope() == currentScope) {
            HandleError.handleError(AnalysisErrorType.NAME_REPETITION);
            return AnalysisResult.FAIL;
        }

        MasterTableItem masterTableItem =  new MasterTableItem(name, category, type, currentScope);
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
     * @param params 函数参数列表
     * @param item 刚刚插入的值
     * @param currentScope 现在的作用域ID
     */
    public AnalysisResult insertIntoTable(String name, SymbolConst category, SymbolConst type, int returnType, MasterTableItem[] params, int currentScope, ParamResult<MasterTableItem> item) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> former = new ParamResult<>(null);
        res = getItemByName(name, former);
        if (res == SymbolTableResult.EXIST && currentScope == former.getValue().getScope()) {
            HandleError.handleError(AnalysisErrorType.NAME_REPETITION);
            return AnalysisResult.FAIL;
        }

        FunctionDetail functionDetail = new FunctionDetail(returnType);
        MasterTableItem masterTableItem = new MasterTableItem(name, category, type, currentScope);
        masterTableItem.setFunctionLink(functionDetail);
        this.table.addLast(masterTableItem);
        item.setValue(masterTableItem);
        return AnalysisResult.SUCCESS;
    }

    /**
     * 把一维数组插入
     * @param name 符号名
     * @param category 种类
     * @param type 类型
     * @param firstSize 第一维大小
     * @param item 刚刚插入的值
     * @param currentScope 现在的作用域ID
     */
    public AnalysisResult insertIntoTable(String name, SymbolConst category, SymbolConst type, int firstSize, int currentScope, ParamResult<MasterTableItem> item) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> former = new ParamResult<>(null);
        res = getItemByName(name, former);
        if (res == SymbolTableResult.EXIST && former.getValue().getScope() == currentScope) {
            HandleError.handleError(AnalysisErrorType.NAME_REPETITION);
            return AnalysisResult.FAIL;
        }

        ArrayDetail arrayDetail = new ArrayDetail(firstSize);
        MasterTableItem masterTableItem = new MasterTableItem(name, category, type, currentScope);
        masterTableItem.setArrayLink(arrayDetail);
        this.table.addLast(masterTableItem);
        item.setValue(masterTableItem);
        return AnalysisResult.SUCCESS;
    }

    /**
     * 把二维数组插入
     * @param name 符号名
     * @param category 种类
     * @param type 类型
     * @param firstSize 第一维大小
     * @param secondSize 第二维大小
     * @param item 刚刚插入的值
     * @param currentScope 现在的作用域ID
     */
    public AnalysisResult insertIntoTable(String name, SymbolConst category, SymbolConst type, int firstSize, int secondSize, int currentScope, ParamResult<MasterTableItem> item) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> former = new ParamResult<>(null);
        res = getItemByName(name, former);
        if (res == SymbolTableResult.EXIST && former.getValue().getScope() == currentScope) {
            HandleError.handleError(AnalysisErrorType.NAME_REPETITION);
            return AnalysisResult.FAIL;
        }

        ArrayDetail arrayDetail = new ArrayDetail(firstSize, secondSize);
        MasterTableItem masterTableItem = new MasterTableItem(name, category, type, currentScope);
        masterTableItem.setArrayLink(arrayDetail);
        this.table.addLast(masterTableItem);
        item.setValue(masterTableItem);
        return AnalysisResult.SUCCESS;
    }

    private boolean isIdentifierNotExist(String name, ParamResult<MasterTableItem> result) {
        SymbolTableResult res;
        res = getItemByName(name, result);
        if (res == SymbolTableResult.NOT_EXIST) {
            HandleError.handleError(AnalysisErrorType.IDENTIFIER_NOT_DEFINE);
            return true;
        }
        return false;
    }

    /**
     * a = 12; 类型的赋值
     */
    public AnalysisResult assignValue(String name, int value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if(isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        if (item.getValue().isConstInt()) {
            if (item.getValue().isHasAssigned()) {
                HandleError.handleError(AnalysisErrorType.CHANGE_CONST_VALUE);
                return AnalysisResult.FAIL;
            }
            else {
                res = item.getValue().setValue(value);
            }
        }
        else if (item.getValue().isVarInt()) {
            res = item.getValue().setValue(value);
        }
        else {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * a[2] = 12; 类型的赋值
     */
    public AnalysisResult assignValue(String name, int firstIndex, int value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if(isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        if (item.getValue().isConstArray()) {
            if (item.getValue().isHasAssigned()) {
                HandleError.handleError(AnalysisErrorType.CHANGE_CONST_VALUE);
                return AnalysisResult.FAIL;
            }
            else {
                res = item.getValue().setValue(value, firstIndex);
                if (res == SymbolTableResult.FAIL) {
                    return AnalysisResult.FAIL;
                }
            }
        }
        else if (item.getValue().isVarArray()) {
            res = item.getValue().setValue(value, firstIndex);
            if (res == SymbolTableResult.FAIL) {
                return AnalysisResult.FAIL;
            }
        }
        else {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * a[2][1] = 12; 类型的赋值
     */
    public AnalysisResult assignValue(String name, int firstIndex, int secondIndex, int value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if(isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        if (item.getValue().isConstArray()) {
            if (item.getValue().isHasAssigned()) {
                HandleError.handleError(AnalysisErrorType.CHANGE_CONST_VALUE);
                return AnalysisResult.FAIL;
            }
            else {
                res = item.getValue().setValue(value, firstIndex, secondIndex);
                if (res == SymbolTableResult.FAIL) {
                    return AnalysisResult.FAIL;
                }
            }
        }
        else if (item.getValue().isVarArray()) {
            res = item.getValue().setValue(value, firstIndex, secondIndex);
            if (res == SymbolTableResult.FAIL) {
                return AnalysisResult.FAIL;
            }
        }
        else {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * int a[2] = {1, 2} 类型的赋值
     */
    public AnalysisResult assignValue(String name, int[] value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if(isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        if (item.getValue().isConstArray()) {
            if (item.getValue().isHasAssigned()) {
                HandleError.handleError(AnalysisErrorType.CHANGE_CONST_VALUE);
                return AnalysisResult.FAIL;
            }
            else {
                res = item.getValue().setValue(value);
                if (res == SymbolTableResult.FAIL) {
                    return AnalysisResult.FAIL;
                }
            }
        }
        else if (item.getValue().isVarArray()) {
            res = item.getValue().setValue(value);
            if (res == SymbolTableResult.FAIL) {
                return AnalysisResult.FAIL;
            }
        }
        else {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * int a[2][2] = {{1, 2}, {3, 4}} 类型的赋值
     */
    public AnalysisResult assignValue(String name, int[][] value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if(isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        if (item.getValue().isConstArray()) {
            if (item.getValue().isHasAssigned()) {
                HandleError.handleError(AnalysisErrorType.CHANGE_CONST_VALUE);
                return AnalysisResult.FAIL;
            }
            else {
                res = item.getValue().setValue(value);
                if (res == SymbolTableResult.FAIL) {
                    return AnalysisResult.FAIL;
                }
            }
        }
        else if (item.getValue().isVarArray()) {
            res = item.getValue().setValue(value);
            if (res == SymbolTableResult.FAIL) {
                return AnalysisResult.FAIL;
            }
        }
        else {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * a 这样的获得值
     */
    public AnalysisResult gainValue(String name, ParamResult<Integer> value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if (isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        res = item.getValue().getValue(value);
        if (res == SymbolTableResult.FAIL) {
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * a[0] 这样的获得值
     */
    public AnalysisResult gainValue(String name, int firstIndex, ParamResult<Integer> value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if (isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        res = item.getValue().getValue(firstIndex, value);
        if (res == SymbolTableResult.FAIL) {
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }

    /**
     * a[0][4] 这样的获得值
     */
    public AnalysisResult gainValue(String name, int firstIndex, int secondIndex, ParamResult<Integer> value) {

        SymbolTableResult res;
        ParamResult<MasterTableItem> item =  new ParamResult<>(null);

        if (isIdentifierNotExist(name, item)) {
            return AnalysisResult.FAIL;
        }

        res = item.getValue().getValue(firstIndex, secondIndex, value);
        if (res == SymbolTableResult.FAIL) {
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
        SymbolTableResult res = getItemByName(name, functionItem);

        if (res == SymbolTableResult.NOT_EXIST) {
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
}
