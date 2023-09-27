package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class LOrExp extends SyntacticComponent {
    /**
     * 唯一单例
     */
    private static LOrExp lOrExp;

    private LOrExp() {
        super();
    }

    static {
        lOrExp = new LOrExp();
    }

    public static LOrExp getInstance() {
        return lOrExp;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = LAndExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!(nextWordCategoryCode.getValue().equals("OR")
            )) {
                break;
            }
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = LAndExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        OutputIntoFile.appendToFile("<LOrExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
