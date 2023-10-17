package SymbolTable.Array;

import Other.ParamResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.SymbolTableResult;

/**
 * 数组子表中的一行
 */
public class ArrayDetail {

    private static final int NO_MEANING = -1;

    /**
     * 维数
     */
    private int dimension;

    /**
     * 第一维的大小
     */
    private int firstSize;

    /**
     * 第二维的大小
     */
    private int secondSize;

    /**
     * 值
     */
    private int[][] value;

    public ArrayDetail(int firstSize) {
        this.dimension = 1;
        this.firstSize = firstSize;
        this.secondSize = NO_MEANING;
        this.value = new int[1][firstSize];
    }

    public ArrayDetail(int firstSize, int secondSize) {
        this.dimension = 2;
        this.firstSize = firstSize;
        this.secondSize = secondSize;
        this.value = new int[firstSize][secondSize];
    }

    public SymbolTableResult setValueOfIndex(int value, int firstIndex) {
        if (dimension != 1) {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return SymbolTableResult.FAIL;
        }
        if (firstIndex >= firstSize) {
            HandleError.handleError(AnalysisErrorType.ARRAY_INDEX_OUT_OF_BOUND);
            return SymbolTableResult.FAIL;
        }

        this.value[firstIndex][0] = value;
        return SymbolTableResult.SUCCESS;
    }

    public SymbolTableResult setValueOfIndex(int value, int firstIndex, int secondIndex) {
        if (dimension != 2) {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return SymbolTableResult.FAIL;
        }
        if (firstIndex >= firstSize || secondIndex >= secondSize) {
            HandleError.handleError(AnalysisErrorType.ARRAY_INDEX_OUT_OF_BOUND);
            return SymbolTableResult.FAIL;
        }

        this.value[firstIndex][secondIndex] = value;
        return SymbolTableResult.SUCCESS;
    }

    public SymbolTableResult setValue(int[] value) {

        if (this.dimension != 1 || value.length != this.firstSize) {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return SymbolTableResult.FAIL;
        }

        this.value[0] = value;
        return SymbolTableResult.SUCCESS;
    }

    public SymbolTableResult setValue(int[][] value) {

        if (this.dimension != 2 || value.length != this.firstSize || value[0].length != this.secondSize) {
            HandleError.handleError(AnalysisErrorType.ASSIGN_TYPE_NOT_MATCH);
            return SymbolTableResult.FAIL;
        }

        this.value = value;
        return SymbolTableResult.SUCCESS;
    }

    public SymbolTableResult getValue(int firstIndex, ParamResult<Integer> result) {

        if (this.dimension != 1) {
            HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_NOT_MATCH);
            return SymbolTableResult.FAIL;
        }

        if (firstIndex >= this.firstSize) {
            HandleError.handleError(AnalysisErrorType.ARRAY_INDEX_OUT_OF_BOUND);
            return SymbolTableResult.FAIL;
        }

        result.setValue(this.value[0][firstIndex]);
        return SymbolTableResult.SUCCESS;
    }

    public SymbolTableResult getValue(int firstIndex, int secondIndex, ParamResult<Integer> result) {

        if (this.dimension != 2) {
            HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_NOT_MATCH);
            return SymbolTableResult.FAIL;
        }

        if (firstIndex >= this.firstSize || secondIndex >= this.secondSize) {
            HandleError.handleError(AnalysisErrorType.ARRAY_INDEX_OUT_OF_BOUND);
            return SymbolTableResult.FAIL;
        }

        result.setValue(this.value[firstIndex][secondIndex]);
        return SymbolTableResult.SUCCESS;
    }
}
