package SyntacticTree;

import InterCode.QuaternionIdentify;
import Other.ParamResult;
import SymbolTable.MasterTable;
import SymbolTable.MasterTableItem;
import SymbolTable.Scope.ScopeStack;
import SymbolTable.SymbolTableResult;

import java.util.ArrayList;

/**
 * 语法树中的结点
 */
public class TreeNode {

    /**
     * 树结点的名字，Terminal代表是终结符号
     */
    public TreeNodeName name;

    /**
     * 树节点的值
     */
    public String value;

    /**
     * 子节点们
     */
    public ArrayList<TreeNode> children;

    /**
     * 父结点
     */
    public TreeNode parent;

    /**
     * 这个结点对应的四元式中的标识符
     */
    private QuaternionIdentify quaternionIdentify;

    /**
     * 这个树节点所处的作用域层次
     * 如果跨越多个，则以最外层为准
     */
    private int scope;

    /**
     * ConstExp的计算的结果
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

    static boolean isGlobal = true;

    public boolean isGlobalData;

    public TreeNode(TreeNodeName name, String value, TreeNode parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.scope = ScopeStack.getInstance().getCurrentScope();
        this.constOneDArrayValue = new ArrayList<>();
        this.constTwoDArrayValue = new ArrayList<>();
        this.isGlobalData = TreeNode.isGlobal;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    /**
     * 合并单传节点
     * 这里的单传节点指的是一个节点只有一个子节点，
     * 这个时候我们会选择直接略过这个子节点，直接把它的子节点的子节点作为它的子节点
     */
    public void mergeSingleChild() {

        for (TreeNode node: this.children) {
            node.mergeSingleChild();
        }

        int childrenSize = this.children.size();
        if (childrenSize == 1) {
            TreeNode child = this.children.get(0);

            if (child.children.isEmpty()) {
                this.value = child.value;
                this.children.clear();
            }
            else {
                this.children = child.children;
            }
        }
    }

    /**
     * 直接把ConstExp的值算出来，节省一下生成的代码的长度
     * 这个方法只在name == ConstExp的时候才使用
     */
    private void calculateConstValue() {

        int length = this.children.size();

        switch (this.name) {
            case ConstExp, Exp -> {
                this.children.get(0).calculateConstValue();
                this.constValue = this.children.get(0).constValue;
            }
            case AddExp, MulExp -> {
                this.children.get(0).calculateConstValue();
                this.constValue = this.children.get(0).constValue;
                for (int i = 1; i < length; i += 2) {
                    String operate = this.children.get(i).value;
                    this.children.get(i+1).calculateConstValue();
                    int nextValue = this.children.get(i+1).constValue;
                    switch (operate) {
                        case "+" -> this.constValue += nextValue;
                        case "-" -> this.constValue -= nextValue;
                        case "*" -> this.constValue *= nextValue;
                        case "/" -> this.constValue /= nextValue;
                        case "%" -> this.constValue %= nextValue;
                    }
                }
            }
            case UnaryExp -> {
                // UnaryExp -> UnaryOp UnaryExp
                if (length == 2) {
                    this.children.get(1).calculateConstValue();
                    this.constValue = this.children.get(1).constValue;
                    String operate = this.children.get(0).value;
                    if (operate.equals("-")) {
                        this.constValue *= -1;
                    }
                }
                // UnaryExp -> PrimaryExp
                else {
                    this.children.get(0).calculateConstValue();
                    this.constValue = this.children.get(0).constValue;
                }
            }
            case PrimaryExp -> {
                // PrimaryExp -> ( Exp )
                if (length == 3) {
                    this.children.get(1).calculateConstValue();
                    this.constValue = this.children.get(1).constValue;
                }
                // PrimaryExp -> LVal
                else if (this.children.get(0).name == TreeNodeName.LVal) {
                    this.children.get(0).calculateConstValue();
                    this.constValue = this.children.get(0).constValue;
                }
                // PrimaryExp -> Number
                else {
                    this.children.get(0).calculateConstValue();
                    this.constValue = this.children.get(0).constValue;
                }
            }
            case LVal -> {
                String name = this.children.get(0).value;
                SymbolTableResult res;
                ParamResult<MasterTableItem> symbolItemResult = new ParamResult<>(null);
                ArrayList<Integer> allVisibleScope = ScopeStack.getInstance().getAllVisible(scope);
                for (Integer outer :
                    allVisibleScope) {
                    int visible = outer;
                    res = MasterTable.getMasterTable().getItemByNameInAllTable(name, visible, symbolItemResult);
                    if (res == SymbolTableResult.EXIST) {
                        break;
                    }
                }
                MasterTableItem item = symbolItemResult.getValue();
                // a
                if (length == 1) {
                    this.constValue = item.constValue;
                }
                // a[1]
                else if (length == 4) {
                    // 解决不了问题，就从源头上改变问题
                    this.children.get(2).name = TreeNodeName.ConstExp;
                    this.children.get(2).calculateConstValue();
                    int index = this.children.get(2).constValue;
                    this.constValue = item.constOneDArrayValue.get(index);
                }
                // a[1][1]
                else if (length == 7) {
                    this.children.get(2).calculateConstValue();
                    int index1 = this.children.get(2).constValue;
                    this.children.get(5).calculateConstValue();
                    int index2 = this.children.get(5).constValue;
                    this.constValue = item.constTwoDArrayValue.get(index1).get(index2);
                }
            }
            case Number -> {
                this.constValue = Integer.parseInt(this.children.get(0).value);
            }
            case ConstInitVal, InitVal -> {
                // 1
                if (length == 1) {
                    this.children.get(0).calculateConstValue();
                    this.constValue = this.children.get(0).constValue;
                }
                else {
                    this.children.get(1).calculateConstValue();
                    // {1, 1}
                    if (this.children.get(1).constOneDArrayValue.isEmpty()) {
                        this.constOneDArrayValue.add(this.children.get(1).constValue);
                        for (int i = 3; i < length; i += 2) {
                            this.children.get(i).calculateConstValue();
                            this.constOneDArrayValue.add(this.children.get(i).constValue);
                        }
                    }
                    // {{1, 1}, {2, 2}}
                    else {
                        this.constTwoDArrayValue.add(this.children.get(1).constOneDArrayValue);
                        for (int i = 3; i < length; i += 2) {
                            this.children.get(i).calculateConstValue();
                            this.constTwoDArrayValue.add(this.children.get(i).constOneDArrayValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * 在常量被定义的时候就设置好它的值
     */
    private void setConstIdentifyValue() {

        int length = this.children.size();
        String name = this.children.get(0).value;
        SymbolTableResult res;
        ParamResult<MasterTableItem> symbolItemResult = new ParamResult<>(null);
        res = MasterTable.getMasterTable().getItemByNameInAllTable(name, this.scope, symbolItemResult);
        MasterTableItem item = symbolItemResult.getValue();

        this.children.get(length - 1).calculateConstValue();

        // a
        if (length == 3) {
            item.constValue = this.children.get(2).constValue;
        }
        // a[1]
        else if (length == 6){
            this.children.get(2).calculateConstValue();
            item.constOneDArrayValue = this.children.get(5).constOneDArrayValue;
        }
        // a[1][1]
        else {
            this.children.get(2).calculateConstValue();
            this.children.get(5).calculateConstValue();
            item.constTwoDArrayValue = this.children.get(8).constTwoDArrayValue;
        }
    }

    public void simplifyConstValue() {

        switch (this.name) {
            case ConstDef -> setConstIdentifyValue();
            case ConstExp, ConstInitVal -> {
                calculateConstValue();
                this.children.clear();
            }
            case InitVal -> {
                if (isGlobal) {
                    this.name = TreeNodeName.ConstInitVal;
                    calculateConstValue();
                    this.children.clear();
                }
            }
            case MainFuncDef, FuncDef -> {
                isGlobal = false;
                for (TreeNode childNode :
                    this.children) {
                    childNode.simplifyConstValue();
                }
            }
            default -> {
                for (TreeNode childNode :
                    this.children) {
                    childNode.simplifyConstValue();
                }
            }
        }
    }

    public QuaternionIdentify getQuaternionIdentify() {
        return quaternionIdentify;
    }

    public void setQuaternionIdentify(QuaternionIdentify quaternionIdentify) {
        this.quaternionIdentify = quaternionIdentify;
    }

    public void traverse(ITraverseOperate operate) {
        switch (this.name) {
            case ConstDef -> operate.translateDeclare(this, 2, this.isGlobalData);
            case VarDef -> operate.translateDeclare(this, 1, this.isGlobalData);
            case FuncDef -> operate.translateFuncDefine(this);
            case FuncFParams -> operate.translateFuncFParams(this);
            case Block -> operate.translateBlock(this);
            case Stmt -> operate.translateStmt(this);
            case ForStmt -> operate.translateForStmt(this);
            case Exp, AddExp, MulExp, UnaryExp, PrimaryExp, Cond, LOrExp, LAndExp, EqExp, RelExp
                -> operate.translateAllExp(this);
            case ConstExp -> operate.translateConst(this);
            case MainFuncDef -> operate.translateMainFunc(this);
            case BlockItem -> operate.translateBlockItem(this);
            case CompUnit -> operate.translateCompUnit(this);
            default -> {
                for (TreeNode childNode :
                    this.children) {
                    childNode.traverse(operate);
                }
            }
        }
    }

    public int getScope() {
        return scope;
    }
}
