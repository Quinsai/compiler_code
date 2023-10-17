package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class MulExp extends SyntacticComponent {

    public MulExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        UnaryExp unaryExp = new UnaryExp();
        res = unaryExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        this.value = unaryExp.value;
//        System.out.println("-----------------------");
//        System.out.println(this.value);

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!(nextWordCategoryCode.getValue().equals("MULT")
                || nextWordCategoryCode.getValue().equals("DIV")
                || nextWordCategoryCode.getValue().equals("MOD")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            UnaryExp unaryExp1 = new UnaryExp();
            res = unaryExp1.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

//            System.out.println(nextWordCategoryCode.getValue());
//            System.out.println(unaryExp1.value);

            if (nextWordCategoryCode.getValue().equals("MULT")) {
                this.value *= unaryExp1.value;
            }
            // TODO 一个bug，有些时候会错误地把unaryExp1.value的值判定为0
            else if (nextWordCategoryCode.getValue().equals("DIV")) {
                // System.out.println(this.value);
                this.value = this.value / unaryExp1.value;
            }
            else if (nextWordCategoryCode.getValue().equals("MOD")) {
                this.value %= unaryExp1.value;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
