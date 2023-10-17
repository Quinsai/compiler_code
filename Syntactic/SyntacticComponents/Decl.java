package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Result.AnalysisResult;

public class Decl extends SyntacticComponent {

    public Decl() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        String categoryCode = nextWordCategoryCode.getValue();
        if (categoryCode.equals("CONSTTK")) {
            ConstDecl constDecl = new ConstDecl();
            res = constDecl.analyze(whetherOutput);
        }
        else if (categoryCode.equals("INTTK")) {
            VarDecl varDecl = new VarDecl();
            res = varDecl.analyze(whetherOutput);
        }
        else {
            res = AnalysisResult.FAIL;
        }
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }
}
