package Result.Error;

/**
 * 错误种类的枚举
 */
public enum AnalysisErrorType {
    UNEXPECTED_ERROR, // 预料之外的其他错误
    ILLEGAL_SYMBOL, // 非法符号
    NAME_REPETITION, // 名字重定义
    ASSIGN_TYPE_NOT_MATCH, // 赋值类型不匹配
    IDENTIFIER_NOT_DEFINE, // 标识符未定义
    CHANGE_CONST_VALUE, // 改变常量的值
    ARRAY_INDEX_OUT_OF_BOUND, // 数组下标越界
    VALUE_NOT_INITIALIZE, // 值未初始化
    ARRAY_DIMENSION_NOT_MATCH, // 数组维度不匹配，比如说试图访问一维数组第二维的值或者相反的
}
