package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class LAndExp extends SyntacticComponent {

    public LAndExp() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        EqExp eqExp = new EqExp();
        res = eqExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        this.value = eqExp.value;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("AND")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            EqExp eqExp1 = new EqExp();
            res = eqExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.value = this.value & eqExp1.value;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
