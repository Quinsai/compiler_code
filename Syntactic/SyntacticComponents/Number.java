package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class Number extends SyntacticComponent {

    public Number() {
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
        if (!nextWordCategoryCode.getValue().equals("INTCON")) {
            return SyntacticAnalysisResult.ERROR;
        }

        this.value = Integer.parseInt(nextWordValue.getValue());

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Number>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
