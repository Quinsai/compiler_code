package SyntacticTree;

/**
 * 语法树主类
 */
public class Tree {

    private TreeNode root;

    /**
     * 语法树唯一单例
     */
    static Tree tree;

    /**
     * 语法树构造方法
     */
    private Tree() {
        this.root = new TreeNode(TreeNodeName.CompUnit, "", null);
    }

    static {
        Tree.tree = new Tree();
    }

    /**
     * 获取语法树唯一单例
     */
    public static Tree getInstance() {
        return Tree.tree;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void addTerminalNodeIntoTree(TreeNode parent, String value) {
        TreeNode node = new TreeNode(TreeNodeName.Terminal, value, parent);
    }

    /**
     * 简化语法树，删除合并单传结点
     */
    public void simplifyTree() {
        this.root.simplifyNode();
    }

    public void traverse(ITraverseOperate operate) {
        this.root.traverse(operate);
    }
}