package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.Scope.ScopeStack;

public class MainFuncDef extends SyntacticComponent {

    public MainFuncDef() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return AnalysisResult.FAIL;
        }


        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("MAINTK")) {
            return AnalysisResult.FAIL;
        }

        ScopeStack.getInstance().enterScope();

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
            return AnalysisResult.FAIL;
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
            return AnalysisResult.FAIL;
        }
        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

        Stmt.functionReturnType = ComponentValueType.INT;

        Block block = new Block();
        res = block.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (!BlockItem.isReturnWithValue && Stmt.functionReturnType == ComponentValueType.INT) {
            HandleError.handleError(AnalysisErrorType.INT_FUNCTION_WITHOUT_RETURN);
            return AnalysisResult.SUCCESS;
        }

        ScopeStack.getInstance().quitScope();

        Stmt.functionReturnType = ComponentValueType.NO_MEANING;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<MainFuncDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
