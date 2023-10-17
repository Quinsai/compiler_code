package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class ConstDecl extends SyntacticComponent {

   public ConstDecl() {
       super();
   }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode,nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        else if (!nextWordCategoryCode.getValue().equals("CONSTTK")) {
            return AnalysisResult.FAIL;
        }

        BType bType = new BType();
        res = bType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        ConstDef constDef = new ConstDef();
        res = constDef.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res == AnalysisResult.FAIL) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            ConstDef constDef1 = new ConstDef();
            res = constDef1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res == AnalysisResult.FAIL) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstDecl>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
