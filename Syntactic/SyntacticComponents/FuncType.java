package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class FuncType extends SyntacticComponent {

    public FuncType() {
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
        if (!(nextWordCategoryCode.getValue().equals("VOIDTK")
            || nextWordCategoryCode.getValue().equals("INTTK")
        )) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncType>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
