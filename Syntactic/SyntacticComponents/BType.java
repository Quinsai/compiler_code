package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class BType extends SyntacticComponent {


    public BType() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        int res;

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return SyntacticAnalysisResult.ERROR;
        }

        return SyntacticAnalysisResult.SUCCESS;
    }
}
