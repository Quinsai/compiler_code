package SymbolTable;

import SymbolTable.Array.ArrayTableItem;
import SymbolTable.Function.FunctionTableItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

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
     * 把常量或者变量插入
     * @param name
     * @param category
     * @param type
     */
    public void insertIntoTable(String name, int category, int type) {
        this.table.addLast(new MasterTableItem(name, category, type));
    }

    /**
     * 把函数插入
     * @param name
     * @param category
     * @param type
     * @param returnType
     * @param params
     */
    public void insertIntoTable(String name, int category, int type, int returnType, MasterTableItem[] params) {
        FunctionTableItem functionTableItem = new FunctionTableItem(returnType, params);
        MasterTableItem masterTableItem = new MasterTableItem(name, category, type);
        masterTableItem.setFunctionLink(functionTableItem);
        this.table.addLast(masterTableItem);
    }

    /**
     * 把数组插入
     * @param name
     * @param category
     * @param type
     * @param dimension
     * @param firstSize
     * @param secondSize
     */
    public void insertIntoTable(String name, int category, int type, int dimension, int firstSize, int secondSize) {
        ArrayTableItem arrayTableItem = new ArrayTableItem(dimension, firstSize, secondSize);
        MasterTableItem masterTableItem = new MasterTableItem(name, category, type);
        masterTableItem.setArrayLink(arrayTableItem);
        this.table.addLast(masterTableItem);
    }
}
