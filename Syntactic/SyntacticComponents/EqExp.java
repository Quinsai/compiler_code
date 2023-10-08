package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class EqExp extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static EqExp eqExp;

    private EqExp() {
        super();
    }

    static {
        eqExp = new EqExp();
    }

    public static EqExp getInstance() {
        return eqExp;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = RelExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!(nextWordCategoryCode.getValue().equals("EQL")
                || nextWordCategoryCode.getValue().equals("NEQ")
            )) {
                break;
            }
            OutputIntoFile.appendToFile("<EqExp>\n", "output.txt");
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = RelExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        OutputIntoFile.appendToFile("<EqExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
