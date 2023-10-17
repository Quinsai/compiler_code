package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class LAndExp extends SyntacticComponent {

    public LAndExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        EqExp eqExp = new EqExp();
        res = eqExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        this.value = eqExp.value;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!(nextWordCategoryCode.getValue().equals("AND")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            EqExp eqExp1 = new EqExp();
            res = eqExp1.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            this.value = this.value & eqExp1.value;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
