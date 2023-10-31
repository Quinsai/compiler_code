package SymbolTable.Scope;

import SymbolTable.MasterTable;

import java.util.Stack;

/**
 * 作用域（层次）栈
 */
public class ScopeStack {

    /**
     * 作用域栈
     */
    private Stack<Integer> stack;

    /**
     * 唯一单例
     */
    private static ScopeStack instance;

    /**
     * scope层次ID计数器
     */
    private int scopeCounter;

    private ScopeStack() {
        this.stack = new Stack<>();
        this.scopeCounter = 0;
        this.stack.push(this.scopeCounter);
    }

    static {
        ScopeStack.instance = new ScopeStack();
    }

    public static ScopeStack getInstance() {
        return instance;
    }

    /**
     * 进入新的作用域（层次）
     */
    public void enterScope() {
        this.scopeCounter ++;
        this.stack.push(scopeCounter);
    }

    /**
     * 退出作用域（层次）
     */
    public void quitScope() {
        int former = this.stack.pop();
        /**
         * 我是傻逼
         * tnnd
         * 这个栈式符号表把我害惨了
         * tnnd
         * 这样一来运行到最后符号表都tm被清空了
         * 你tm还开发个毛线啊
         * 纯纯傻逼
         */
        MasterTable.getMasterTable().cleanTableWhenQuitScope(former);
    }

    public int getCurrentScope() {
        return this.stack.peek();
    }
}
