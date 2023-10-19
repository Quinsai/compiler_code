package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.MasterTableItem;
import SymbolTable.Scope.ScopeStack;
import SymbolTable.SymbolConst;

public class FuncDef extends SyntacticComponent {

    public FuncDef() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ParamResult<MasterTableItem> item = new ParamResult<>(null);
        SymbolConst returnType;
        String name;

        FuncType funcType = new FuncType();
        res = funcType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        returnType = funcType.getReturnType();

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }

        name = nextWordValue.getValue();
        res = masterTable.insertIntoTable(name, SymbolConst.FUNCTION, SymbolConst.NO_MEANING, returnType, item);
        if (res == AnalysisResult.FAIL) {
            ScopeStack.getInstance().enterScope();
            Block block = new Block();
            res = block.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            ScopeStack.getInstance().quitScope();
            return AnalysisResult.SUCCESS;
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
            FuncFParams funcFParams = new FuncFParams();
            res = funcFParams.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
        }

        masterTable.setFunctionParams(name);

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
            return AnalysisResult.FAIL;
        }

        if (returnType == SymbolConst.INT) {
            Stmt.functionReturnType = ComponentValueType.INT;
        }
        else if (returnType == SymbolConst.VOID) {
            Stmt.functionReturnType = ComponentValueType.VOID;
        }

        Block block = new Block();
        res = block.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        Stmt.functionReturnType = ComponentValueType.NO_MEANING;

        ScopeStack.getInstance().quitScope();

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
