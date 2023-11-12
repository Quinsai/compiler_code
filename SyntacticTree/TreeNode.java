package SyntacticTree;

import InterCode.QuaternionIdentify;
import SymbolTable.Scope.ScopeStack;

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

    public TreeNode(TreeNodeName name, String value, TreeNode parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.scope = ScopeStack.getInstance().getCurrentScope();
        if (parent != null) {
            parent.children.add(this);
        }
    }

    /**
     * 简化结点
     */
    public void simplifyNode() {

        for (TreeNode node: this.children) {
            node.simplifyNode();
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

    public QuaternionIdentify getQuaternionIdentify() {
        return quaternionIdentify;
    }

    public void setQuaternionIdentify(QuaternionIdentify quaternionIdentify) {
        this.quaternionIdentify = quaternionIdentify;
    }

    public void traverse(ITraverseOperate operate) {
        switch (this.name) {
            case ConstDef -> operate.translateDeclare(this, 2);
            case VarDef -> operate.translateDeclare(this, 1);
            case FuncDef -> operate.translateFuncDefine(this);
            case FuncFParams -> operate.translateFuncFParams(this);
            case Block -> operate.translateBlock(this);
            case Stmt -> operate.translateStmt(this);
            case ForStmt -> operate.translateForStmt(this);
            case Exp, AddExp, MulExp, UnaryExp, PrimaryExp, Cond, ConstExp, LOrExp, LAndExp, EqExp, RelExp
                -> operate.translateAllExp(this);
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
