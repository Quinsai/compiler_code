package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class RelExp extends SyntacticComponent {

    public RelExp() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        int operation = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        AddExp addExp = new AddExp();
        res = addExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("LSS")
                || nextWordCategoryCode.getValue().equals("LEQ")
                || nextWordCategoryCode.getValue().equals("GRE")
                || nextWordCategoryCode.getValue().equals("GEQ")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<RelExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            AddExp addExp1 = new AddExp();
            res = addExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<RelExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
