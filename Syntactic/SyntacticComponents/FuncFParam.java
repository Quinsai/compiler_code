package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.MasterTableItem;
import SymbolTable.SymbolConst;

public class FuncFParam extends SyntacticComponent {

    public FuncFParam() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        String name;
        ParamResult<MasterTableItem> item = new ParamResult<>(null);

        BType bType = new BType();
        res = bType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }
        name = nextWordValue.getValue();

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("LBRACK")) {
            int dimension = 0;
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                return AnalysisResult.FAIL;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            dimension ++;

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

                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                    HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                    return AnalysisResult.FAIL;
                }
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            }

            res = masterTable.insertIntoTable(name, SymbolConst.VAR, SymbolConst.ARRAY, dimension, item);
            if (res == AnalysisResult.FAIL) {
                return AnalysisResult.FAIL;
            }
        }
        else {
            res = masterTable.insertIntoTable(name, SymbolConst.VAR, SymbolConst.INT, item);
            if (res == AnalysisResult.FAIL) {
                return AnalysisResult.FAIL;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncFParam>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
