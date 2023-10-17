package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class FuncFParams extends SyntacticComponent {

    public FuncFParams() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        FuncFParam funcFParam = new FuncFParam();
        res = funcFParam.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            FuncFParam funcFParam1 = new FuncFParam();
            res = funcFParam1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncFParams>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
