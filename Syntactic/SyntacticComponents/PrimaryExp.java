package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import javax.net.ssl.SSLContext;

public class PrimaryExp extends SyntacticComponent {

    public PrimaryExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCode.getValue().equals("LPARENT")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            Exp exp = new Exp();
            res = exp.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                return SyntacticAnalysisResult.ERROR;
            }

            this.value = exp.value;
        }
        else if (nextWordCategoryCode.getValue().equals("IDENFR")) {
            LVal lVal = new LVal();
            res = lVal.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            this.value = lVal.value;
        }
        else if (nextWordCategoryCode.getValue().equals("INTCON")) {
            Number number = new Number();
            res = number.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            this.value = number.value;
        }
        else {
            return SyntacticAnalysisResult.ERROR;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<PrimaryExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
