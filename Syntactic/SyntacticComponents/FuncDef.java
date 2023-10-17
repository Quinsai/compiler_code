package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class FuncDef extends SyntacticComponent {

    public FuncDef() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        FuncType funcType = new FuncType();
        res = funcType.analyze(whetherOutput);
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

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
            return AnalysisResult.FAIL;
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            FuncFParams funcFParams = new FuncFParams();
            res = funcFParams.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            return AnalysisResult.FAIL;
        }

        Block block = new Block();
        res = block.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
