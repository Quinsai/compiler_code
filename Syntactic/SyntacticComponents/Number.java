package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class Number extends SyntacticComponent {

    public Number() {
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
        if (!nextWordCategoryCode.getValue().equals("INTCON")) {
            return AnalysisResult.FAIL;
        }

        this.value = Integer.parseInt(nextWordValue.getValue());

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Number>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
