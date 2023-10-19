package SymbolTable.Array;

import Other.ParamResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.SymbolTableResult;

/**
 * 数组子表中的一行
 */
public class ArrayDetail {

    /**
     * 维数
     */
    private int dimension;

    public ArrayDetail(int dimension) {
        this.dimension = dimension;
    }
}
