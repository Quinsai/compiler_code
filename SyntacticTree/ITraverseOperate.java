package SyntacticTree;

/**
 * translateXXX 是把对应的语法成分翻译成四元式
 * traverseXXX 仅仅是遍历过程中传递值
 */
public interface ITraverseOperate {

    /**
     * 声明语句
     * @param varOrConst 1表示变量，2表示常量
     * @param isGlobal 是否是全局
     */
    void translateDeclare(TreeNode node, int varOrConst, boolean isGlobal);

    /**
     * 函数定义
     */
    void translateFuncDefine(TreeNode node);

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
     * @param isAssign 是否是赋值，true代表赋值，否则false代表引用
     */
    void translateLVal(TreeNode node, boolean isAssign);

    void translateForStmt(TreeNode node);

    void translateAllExp(TreeNode node);

    void translateMainFunc(TreeNode node);

    void translateBlockItem(TreeNode node);

    void translateCompUnit(TreeNode node);

    void translateFuncFParams(TreeNode node);

    void translateConst(TreeNode node);
}
