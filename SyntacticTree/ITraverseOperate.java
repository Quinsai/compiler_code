package SyntacticTree;

import InterCode.QuadrupleConst;

public interface ITraverseOperate {

    /**
     * 声明语句
     * @param varOrConst 变量为1，常量为2
     */
    void translateDeclare(TreeNode node, QuadrupleConst varOrConst);

    /**
     * 函数定义
     */
    void translateFuncDefine(TreeNode node);

    /**
     * 函数类型
     */
    void translateFuncType(TreeNode node);
}
