package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class LOrExp extends SyntacticComponent {

    public LOrExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        LAndExp lAndExp = new LAndExp();
        res = lAndExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        this.value = lAndExp.value;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("OR")) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<LOrExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            LAndExp lAndExp1 = new LAndExp();
            res = lAndExp1.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            this.value = this.value | lAndExp1.value;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LOrExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
