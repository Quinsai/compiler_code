package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Result.AnalysisResult;

public class BType extends SyntacticComponent {


    public BType() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        AnalysisResult res;

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

        if (res != AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return Result.AnalysisResult.FAIL;
        }

        return Result.AnalysisResult.SUCCESS;
    }
}
