package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import javax.net.ssl.SSLContext;

public class PrimaryExp extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static PrimaryExp primaryExp;

    private PrimaryExp() {
        super();
    }

    static {
        primaryExp = new PrimaryExp();
    }

    public static PrimaryExp getInstance() {
        return primaryExp;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCode.getValue().equals("LPARENT")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = Exp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCode.getValue().equals("IDENFR")) {
            res = LVal.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCode.getValue().equals("INTCON")) {
            res = Number.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<PrimaryExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
