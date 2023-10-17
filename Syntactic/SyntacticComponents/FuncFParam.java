package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class FuncFParam extends SyntacticComponent {

    public FuncFParam() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        BType bType = new BType();
        res = bType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("LBRACK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
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

                ConstExp constExp = new ConstExp();
                res = constExp.analyze(whetherOutput);
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
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncFParam>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
