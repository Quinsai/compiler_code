package SyntacticTree;

import Output.OutputIntoFile;

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

    public TreeNode(TreeNodeName name, String value, TreeNode parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
        this.children = new ArrayList<>();
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

    public void traverse() {
        OutputIntoFile.appendToFile(this.name.toString() + " " + this.value + "\n", "output.txt");

        for (TreeNode node: this.children) {
            node.traverse();
        }
    }
}
