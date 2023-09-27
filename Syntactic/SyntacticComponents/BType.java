package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Syntactic.SyntacticAnalysisResult;

public class BType extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static BType bType;

    private BType() {
        super();
    }

    static {
        bType = new BType();
    }

    public static BType getInstance() {
        return bType;
    }

    @Override
    public int analyze() {
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        int res;

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return SyntacticAnalysisResult.ERROR;
        }

        return SyntacticAnalysisResult.SUCCESS;
    }
}
