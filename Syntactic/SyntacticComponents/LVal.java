package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class LVal extends SyntacticComponent {

    public LVal() {
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
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("LBRACK")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            Exp exp = new Exp();
            res = exp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                return AnalysisResult.FAIL;
            }
        }

        // TODO 解决LVal获取value的问题
        /*
        这里写的3124仅仅是为了这个能跑起来而已
         */
        this.value = 3124;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LVal>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
