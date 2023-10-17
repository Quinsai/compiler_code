package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class EqExp extends SyntacticComponent {

    private static final int EQL = 979;
    private static final int NEQ = 899;

    public EqExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        int operation = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        RelExp relExp = new RelExp();
        res = relExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        this.value = relExp.value;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("EQL")) {
                operation = EQL;
            }
            else if (nextWordCategoryCode.getValue().equals("NEQ")) {
                operation = NEQ;
            }
            else {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<EqExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            RelExp relExp1 = new RelExp();
            res = relExp1.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            if (operation == EQL && this.value == relExp1.value) {
                this.value = 1;
            }
            else if (operation == NEQ && this.value != relExp1.value) {
                this.value = 1;
            }
            else {
                this.value = 0;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<EqExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
