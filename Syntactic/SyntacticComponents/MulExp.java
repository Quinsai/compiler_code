package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class MulExp extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static MulExp mulExp;

    private MulExp() {
        super();
    }

    static {
        mulExp = new MulExp();
    }

    public static MulExp getInstance() {
        return mulExp;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = UnaryExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

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
            OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = UnaryExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
