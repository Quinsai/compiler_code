package SyntacticTree;

import Output.OutputIntoFile;

import java.util.ArrayList;
import java.util.Iterator;

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
    private final String value;

    /**
     * 子节点们
     */
    private final ArrayList<TreeNode> children;

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
        operate.function(this);

        for (TreeNode child : this.children) {
            child.traverse(operate);
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
}
