package SyntacticTree;

public interface ITraverseOperate {

    /**
     * 声明语句
     * @param varOrConst 变量为1，常量为2
     */
    void declare(TreeNode node, int varOrConst);

    /**
     * 函数定义
     */
    void funcDefine(TreeNode node);

    /**
     * 函数类型
     */
    void funcType(TreeNode node);
}
