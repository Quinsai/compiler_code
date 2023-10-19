package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class AddExp extends SyntacticComponent {

    public AddExp() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        MulExp mulExp = new MulExp();
        res = mulExp.analyze(whetherOutput);
        if (res != Result.AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("PLUS")
                || nextWordCategoryCode.getValue().equals("MINU")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            MulExp mulExp1 = new MulExp();
            res = mulExp1.analyze(whetherOutput);
            if (res != Result.AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
        }
        return Result.AnalysisResult.SUCCESS;
    }
}
