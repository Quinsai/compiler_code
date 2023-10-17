package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class ConstInitVal extends SyntacticComponent {

    public ConstInitVal() {
        super();
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
        if (nextWordCategoryCode.getValue().equals("LBRACE")) {

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (nextWordCategoryCode.getValue().equals("RBRACE")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            }
            else {
                ConstInitVal constInitVal = new ConstInitVal();
                res = constInitVal.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                while (true) {
                    res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }
                    if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                        break;
                    }
                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                    ConstInitVal constInitVal1 = new ConstInitVal();
                    res = constInitVal1.analyze(whetherOutput);
                    if (res == AnalysisResult.FAIL) {
                        return AnalysisResult.FAIL;
                    }
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("RBRACE")) {
                    return AnalysisResult.FAIL;
                }
            }
        }
        else {
            ConstExp constExp = new ConstExp();
            res = constExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstInitVal>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
