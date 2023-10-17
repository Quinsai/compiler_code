package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class AddExp extends SyntacticComponent {

    private static final int PLUS = 123;
    private static final int MINU = 435;

    public AddExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        int operation = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        MulExp mulExp = new MulExp();
        res = mulExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        this.value = mulExp.value;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("PLUS")) {
                operation = PLUS;
            }
            else if (nextWordCategoryCode.getValue().equals("MINU")) {
                operation = MINU;
            }
            else {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            MulExp mulExp1 = new MulExp();
            res = mulExp1.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            if (operation == PLUS) {
                this.value += mulExp1.value;
            }
            else if (operation == MINU) {
                this.value -= mulExp1.value;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
