package SymbolTable;

/**
 * C_CONST, C_VAR, C_FUNCTION属于category
 * T_ARRAY, T_INT, T_VOID属于type
 */
public enum SymbolConst {

    // ---------以下为种类category----------
    CONST, VAR, FUNCTION,
    // ---------以下为类型type----------
    ARRAY, INT, VOID,
    // ---------以下为无意义----------
    NO_MEANING
}
