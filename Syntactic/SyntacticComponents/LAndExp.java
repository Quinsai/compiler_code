package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class LAndExp extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static LAndExp lAndExp;

    private LAndExp() {
        super();
    }

    static {
        lAndExp = new LAndExp();
    }

    public static LAndExp getInstance() {
        return lAndExp;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = EqExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!(nextWordCategoryCode.getValue().equals("AND")
            )) {
                break;
            }
            OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = EqExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
