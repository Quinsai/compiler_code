package Syntactic.SyntacticTree;

import java.util.ArrayList;

/**
 * 语法树中的结点
 */
public class TreeNode {

    /**
     * 树结点的名字，Terminal代表是终结符号
     */
    private TreeNodeName name;

    /**
     * 树节点的值，当且仅当name == Terminal的时候有意义，也就是仅仅针对终结符讨论
     */
    private String value;

    /**
     * 子节点们
     */
    private ArrayList<TreeNode> children;

    /**
     * 父结点
     */
    private TreeNode parent;

    public TreeNode(TreeNodeName name, String value, TreeNode parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
        parent.children.add(this);
    }
}
