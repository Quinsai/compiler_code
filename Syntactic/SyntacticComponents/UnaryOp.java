package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class UnaryOp extends SyntacticComponent {

    public UnaryOp() {
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
        if (!(nextWordCategoryCode.getValue().equals("PLUS")
            || nextWordCategoryCode.getValue().equals("MINU")
            || nextWordCategoryCode.getValue().equals("NOT"))
        ) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<UnaryOp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
