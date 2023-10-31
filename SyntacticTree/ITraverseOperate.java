package SyntacticTree;

import InterCode.QuadrupleConst;

/**
 * translateXXX 是把对应的语法成分翻译成四元式
 * traverseXXX 仅仅是遍历过程中传递值
 */
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
     * 函数形式参数
     */
    void translateFuncFParam(TreeNode node);


    /**
     * 函数类型
     */
    void traverseFuncType(TreeNode node);
}
