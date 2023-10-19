package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.MasterTableItem;
import SymbolTable.SymbolConst;

import java.util.AbstractList;
import java.util.ArrayList;

public class ConstDef extends SyntacticComponent {

    public ConstDef() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        AnalysisResult res;
        ParamResult<MasterTableItem> item = new ParamResult<>(null);

        String name = "";
        int dimension = 0;
        int[] size = new int[2];

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }

        name += nextWordValue.getValue();

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("LBRACK")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            dimension ++;

            if (dimension == 3) {
                HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_BEYOND_TWO);
                return AnalysisResult.FAIL;
            }

            ConstExp constExp = new ConstExp();
            res = constExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

//            size[dimension - 1] = constExp.intValue;

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                return  AnalysisResult.FAIL;
            }
        }

        if (dimension == 0) {
            res = masterTable.insertIntoTable(name, SymbolConst.CONST, SymbolConst.INT, item);
        }
        else if (dimension == 1) {
            res = masterTable.insertIntoTable(name, SymbolConst.CONST, SymbolConst.ARRAY, size[0], item);
        }
        else if (dimension == 2) {
            res = masterTable.insertIntoTable(name, SymbolConst.CONST, SymbolConst.ARRAY, size[0], size[1], item);
        }
        else {
            return AnalysisResult.FAIL;
        }
        if (res == AnalysisResult.FAIL) {
            return res;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("ASSIGN")) {
            return AnalysisResult.FAIL;
        }

        ConstInitVal constInitVal = new ConstInitVal();
        res = constInitVal.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (constInitVal.getValueType() == ComponentValueType.INT) {
            res = masterTable.assignValue(name, constInitVal.intValue);
        }
        else if (constInitVal.getValueType() == ComponentValueType.ONE_DIMENSION_ARRAY) {
            ArrayList<Integer> value = constInitVal.getOneDArrayValue();
            int arraySize = value.size();
            int[] arrayValue = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                arrayValue[i] = value.get(i);
            }

            res = masterTable.assignValue(name, arrayValue);
        }
        else if (constInitVal.getValueType() == ComponentValueType.TWO_DIMENSION_ARRAY) {
            ArrayList<ArrayList<Integer>> value = constInitVal.getTwoDArrayValue();
            int firstSize = value.size();
            int secondSize = value.get(0).size();

            for (int i = 0; i < firstSize; i++) {
                if (value.get(i).size() != secondSize) {
                    HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_NOT_NEAT);
                    return AnalysisResult.FAIL;
                }
            }

            int[][] arrayValue = new int[firstSize][secondSize];
            for (int i = 0; i < firstSize; i++) {
                for (int j = 0; j < secondSize; j++) {
                    arrayValue[i][j] = value.get(i).get(j);
                }
            }

            res = masterTable.assignValue(name, arrayValue);
        }

        if (res == AnalysisResult.FAIL) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
