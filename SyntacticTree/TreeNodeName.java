package SyntacticTree;

/**
 * 树节点对应的符号的名字
 */
public enum TreeNodeName {
    /**
     * 表示是终结符号
     */
    Terminal,
    CompUnit,
    Decl,
    ConstDecl,
    BType,
    ConstDef,
    ConstInitVal,
    VarDecl,
    VarDef,
    InitVal,
    FuncDef,
    MainFuncDef,
    FuncType,
    FuncFParams,
    FuncFParam,
    Block,
    BlockItem,
    Stmt,
    ForStmt,
    Exp,
    Cond,
    LVal,
    PrimaryExp,
    Number,
    UnaryExp,
    UnaryOp,
    FuncRParams,
    MulExp,
    AddExp,
    RelExp,
    EqExp,
    LAndExp,
    LOrExp,
    ConstExp
}
