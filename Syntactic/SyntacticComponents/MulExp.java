package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class MulExp extends SyntacticComponent {

    public MulExp() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        UnaryExp unaryExp = new UnaryExp();
        res = unaryExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        this.value = unaryExp.value;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("MULT")
                || nextWordCategoryCode.getValue().equals("DIV")
                || nextWordCategoryCode.getValue().equals("MOD")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            UnaryExp unaryExp1 = new UnaryExp();
            res = unaryExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            if (nextWordCategoryCode.getValue().equals("MULT")) {
                this.value *= unaryExp1.value;
            }
            else if (nextWordCategoryCode.getValue().equals("DIV")) {
                // System.out.println(this.value);
                this.value = this.value / unaryExp1.value;
            }
            else if (nextWordCategoryCode.getValue().equals("MOD")) {
                this.value %= unaryExp1.value;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
