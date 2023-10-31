package SyntacticTree;

import InterCode.QuadrupleConst;
import InterCode.QuadrupleVariable;
import Output.OutputIntoFile;

import java.util.ArrayList;

/**
 * 语法树中的结点
 */
public class TreeNode {

    /**
     * 树结点的名字，Terminal代表是终结符号
     */
    private final TreeNodeName name;

    /**
     * 树节点的值，当且仅当name == Terminal的时候有意义，也就是仅仅针对终结符讨论
     */
    private String value;

    /**
     * 这个树节点对应的四元式变量
     */
    public QuadrupleVariable quadrupleVariable;

    /**
     * 子节点们
     */
    public final ArrayList<TreeNode> children;

    /**
     * 父结点
     */
    private final TreeNode parent;

    /**
     * 层级
     */
    public int level;

    public TreeNode(TreeNodeName name, String value, TreeNode parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
        this.children = new ArrayList<>();
        if (parent != null) {
            this.level = this.parent.level + 1;
            parent.children.add(this);
        }
    }

    public void traverse(ITraverseOperate operate) {
        switch (this.name) {
            case ConstDef ->  operate.translateDeclare(this, QuadrupleConst.CONST);
            case VarDef -> operate.translateDeclare(this, QuadrupleConst.VAR);
            case FuncType -> operate.traverseFuncType(this);
            case FuncDef -> operate.translateFuncDefine(this);
        }
    }

    public void display() {
        String string = "";
        for (int i = 0; i< level; i++) {
            string += "#";
        }
        string += this.name;
        string += " ";
        string += this.value;
        string += "\n";
        OutputIntoFile.appendToFile(string, "output.txt");
    }

    public TreeNodeName getName() {
        return name;
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
