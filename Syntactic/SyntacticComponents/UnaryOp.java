package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class UnaryOp extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static UnaryOp unaryOp;

    private UnaryOp() {
        super();
    }

    static {
        unaryOp = new UnaryOp();
    }

    public static UnaryOp getInstance() {
        return unaryOp;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!(nextWordCategoryCode.getValue().equals("PLUS")
            || nextWordCategoryCode.getValue().equals("MINU")
            || nextWordCategoryCode.getValue().equals("NOT"))
        ) {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<UnaryOp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
