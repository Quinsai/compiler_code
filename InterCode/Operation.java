package InterCode;

public enum Operation {
    ADDRESS, // 获取`a`所指向的地址偏移（增大）`offset`后的地址
    GET_ADDRESS_VALUE,
    SET_ADDRESS_VALUE,
    MAIN_FUNC_BEGIN,
    MAIN_FUNC_END,
    FUNC_BEGIN,
    FUNC_END,
    PARA_INT,
    PARA_ARRAY,
    RETURN,
    BLOCK_BEGIN,
    BLOCK_END,
    VAR_INT_DECLARE,
    VAR_ARRAY_DECLARE,
    CONST_INT_DECLARE,
    CONST_ARRAY_DECLARE,
    PLUS,
    MINU,
    MULT,
    DIV,
    MOD,
    ASSIGN,
    LABEL,
    SKIP,
    BRANCH_IF_EQUAL,
    INIT, // 初始化赋值
}
