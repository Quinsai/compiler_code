package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class AddExp extends SyntacticComponent {

    private static final int PLUS = 123;
    private static final int MINU = 435;

    public AddExp() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        int operation = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        MulExp mulExp = new MulExp();
        res = mulExp.analyze(whetherOutput);
        if (res != Result.AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }

        this.intValue = mulExp.intValue;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }
            if (nextWordCategoryCode.getValue().equals("PLUS")) {
                operation = PLUS;
            }
            else if (nextWordCategoryCode.getValue().equals("MINU")) {
                operation = MINU;
            }
            else {
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

            if (operation == PLUS) {
                this.intValue += mulExp1.intValue;
            }
            else if (operation == MINU) {
                this.intValue -= mulExp1.intValue;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
        }
        return Result.AnalysisResult.SUCCESS;
    }
}
