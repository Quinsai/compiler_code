package SyntacticTree;

/**
 * translateXXX 是把对应的语法成分翻译成四元式
 * traverseXXX 仅仅是遍历过程中传递值
 */
public interface ITraverseOperate {

    /**
     * 声明语句
     * @param varOrConst 1表示变量，2表示常量
     */
    void translateDeclare(TreeNode node, int varOrConst);

    /**
     * 函数定义
     */
    void translateFuncDefine(TreeNode node);

    /**
     * 函数形式参数
     */
    void translateFuncFParam(TreeNode node);

    /**
     * 代码块
     */
    void translateBlock(TreeNode node);

    /**
     * 翻译Stmt
     */
    void translateStmt(TreeNode node);

    /**
     * 翻译左值表达式LVal
     */
    void translateLVal(TreeNode node);

    void translateForStmt(TreeNode node);

    void translateAllExp(TreeNode node);

    void translateMainFunc(TreeNode node);

    void translateBlockItem(TreeNode node);
}
