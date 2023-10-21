package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;

public class PrimaryExp extends SyntacticComponent {

    public PrimaryExp() {
        super();
        this.valueType = ComponentValueType.INT;
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("LPARENT")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            Exp exp = new Exp();
            res = exp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
                return AnalysisResult.FAIL;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

        }
        else if (nextWordCategoryCode.getValue().equals("IDENFR")) {
            LVal lVal = new LVal(LVal.REFERENCE);
            res = lVal.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.valueType = lVal.valueType;

        }
        else if (nextWordCategoryCode.getValue().equals("INTCON")) {
            Number number = new Number();
            res = number.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

        }
        else {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<PrimaryExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
