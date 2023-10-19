package SymbolTable.Scope;

import SymbolTable.MasterTable;

import java.util.ArrayList;
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
        MasterTable.getInstance().cleanTableWhenQuitScope(former);
    }

    public int getCurrentScope() {
        return this.stack.peek();
    }
}
