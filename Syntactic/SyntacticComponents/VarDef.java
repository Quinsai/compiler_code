package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.MasterTableItem;
import SymbolTable.SymbolConst;

import java.util.ArrayList;

public class VarDef extends SyntacticComponent {

    public VarDef() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ParamResult<MasterTableItem> item = new ParamResult<>(null);

        String name = "";
        int dimension = 0;

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

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                return AnalysisResult.FAIL;
            }
        }

        if (dimension == 0) {
            res = masterTable.insertIntoTable(name, SymbolConst.VAR, SymbolConst.INT, item);
        }
        else if (dimension == 1 || dimension == 2) {
            res = masterTable.insertIntoTable(name, SymbolConst.VAR, SymbolConst.ARRAY, dimension, item);
        }
        else {
            return AnalysisResult.FAIL;
        }
        if (res == AnalysisResult.FAIL) {
            return res;
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("ASSIGN")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            InitVal initVal = new InitVal();
            res = initVal.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            if (res == AnalysisResult.FAIL) {
                return AnalysisResult.FAIL;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<VarDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
