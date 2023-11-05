package InterCode;

/**
 * 四元式中的操作符
 */
public enum Operation {
    /**
     * ADDRESS, a, offset, t
     * 获取`a`所指向的地址偏移（增大）`offset`后的地址
     */
    ADDRESS,

    /**
     * GET_VALUE, a, null, v
     * 获取a的值，如果a指向某个地址的话，则获取其指向的地址上的值
     */
    GET_VALUE,

    /**
     * SET_VALUE, a, v, null
     * 给a赋值，如果a指向某个地址的话，则给其指向的地址赋值
     */
    SET_VALUE,
    MAIN_FUNC_BEGIN,
    MAIN_FUNC_END,

    /**
     * FUNC_BEGIN, name, returnType, null
     */
    FUNC_BEGIN,

    /**
     * FUNC_END, null, null, null
     */
    FUNC_END,

    /**
     * FORMAL_PARA_INT, name, null, null
     */
    FORMAL_PARA_INT,

    /**
     * FORMAL_PARA_ARRAY, name, secondSize, null
     */
    FORMAL_PARA_ARRAY,

    /**
     * RETURN, null, null, null
     * RETURN, 12, null, null
     */
    RETURN,

    /**
     * FUNC_CALL_BEGIN, func, null, null
     */
    FUNC_CALL_BEGIN,

    /**
     * REAL_PARA_INT, a, null, null
     */
    REAL_PARA,

    /**
     * FUNC_CELL_END, null, null, null
     */
    FUNC_CALL_END,

    /**
     * BLOCK_BEGIN, null, null, null
     */
    BLOCK_BEGIN,

    /**
     * BLOCK_END, null, null, null
     */
    BLOCK_END,

    /**
     * VAR_INT_DECLARE, null, null, a
     */
    VAR_INT_DECLARE,

    /**
     * VAR_ARRAY_DECLARE, 12, null, b
     */
    VAR_ARRAY_DECLARE,

    /**
     * CONST_INT_DECLARE, null, null, c
     */
    CONST_INT_DECLARE,

    /**
     * CONST_ARRAY_DECLARE, 24, null, d
     */
    CONST_ARRAY_DECLARE,
    PLUS,
    MINU,
    MULT,
    DIV,
    MOD,
    OPPO,
    OR,
    AND,
    GREAT,
    GREAT_EQUAL,
    LITTLE,
    LITTLE_EQUAL,
    EQUAL,
    NOT_EQUAL,
    NOT,
    ASSIGN,

    /**
     * LABEL, label, null, null
     * 标记此处为一个LABEL，便于跳转使用
     */
    LABEL,

    /**
     * SKIP, label, null, null
     * 跳转到label所在，从label（包括）开始执行
     * 其实包不包括无所谓，反正LABEL没有任何实际作用
     */
    SKIP,

    /**
     * BRANCH_IF_TRUE, a, label, null
     * 如果a为真，则跳转到label所在处，从label（包括）开始执行
     */
    BRANCH_IF_TRUE,

    /**
     * BRANCH_IF_FALSE, a, label, null
     * 如果a为假，则跳转到label所在处，从label（包括）开始执行
     */
    BRANCH_IF_FALSE,

    /**
     * PRINT_STRING, "AAA", null, null
     * 输出单纯的字符串
     */
    PRINT_STRING,

    /**
     * PRINT_INT, a, null, null
     * 输出数字（int）a
     */
    PRINT_INT,

    INIT, // 初始化赋值
    GETINT, // 获取输入的int值
}