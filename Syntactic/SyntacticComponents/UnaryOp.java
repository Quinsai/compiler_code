package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class UnaryOp extends SyntacticComponent {

    public UnaryOp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!(nextWordCategoryCode.getValue().equals("PLUS")
            || nextWordCategoryCode.getValue().equals("MINU")
            || nextWordCategoryCode.getValue().equals("NOT"))
        ) {
            return SyntacticAnalysisResult.ERROR;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<UnaryOp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
