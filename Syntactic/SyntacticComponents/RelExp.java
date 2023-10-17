package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class RelExp extends SyntacticComponent {

    private static final int LSS = 424;
    private static final int LEQ = 979;
    private static final int GRE = 667;
    private static final int GEQ = 596;

    public RelExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        int operation = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        AddExp addExp = new AddExp();
        res = addExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        this.value = addExp.value;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("LSS")) {
                operation = LSS;
            }
            else if (nextWordCategoryCode.getValue().equals("LEQ")) {
                operation = LEQ;
            }
            else if (nextWordCategoryCode.getValue().equals("GRE")) {
                operation = GRE;
            }
            else if (nextWordCategoryCode.getValue().equals("GEQ")) {
                operation = GEQ;
            }
            else {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<RelExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            AddExp addExp1 = new AddExp();
            res = addExp1.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            if (operation == LSS && this.value < addExp1.value) {
                this.value = 1;
            }
            else if (operation == LEQ && this.value <= addExp1.value) {
                this.value = 1;
            }
            else if (operation == GRE && this.value > addExp1.value) {
                this.value = 1;
            }
            else if (operation == GEQ && this.value >= addExp1.value) {
                this.value = 1;
            }
            else {
                this.value = 0;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<RelExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
