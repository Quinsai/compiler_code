package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import SymbolTable.SymbolConst;

public class FuncType extends SyntacticComponent {

    private SymbolConst returnType;

    public FuncType() {
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

        if (nextWordCategoryCode.getValue().equals("VOIDTK")) {
            this.returnType = SymbolConst.VOID;
        }
        else if (nextWordCategoryCode.getValue().equals("INTTK")) {
            this.returnType = SymbolConst.INT;
        }
        else {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncType>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }

    public SymbolConst getReturnType() {
        return returnType;
    }
}
