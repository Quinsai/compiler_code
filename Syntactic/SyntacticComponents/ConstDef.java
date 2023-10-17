package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class ConstDef extends SyntacticComponent {

    public ConstDef() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        AnalysisResult res;

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
                return  AnalysisResult.FAIL;
            }
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("ASSIGN")) {
            return AnalysisResult.FAIL;
        }

        ConstInitVal constInitVal = new ConstInitVal();
        res = constInitVal.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
