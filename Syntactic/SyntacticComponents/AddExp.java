package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class AddExp extends SyntacticComponent {
    /**
     * 唯一单例
     */
    private static AddExp addExp;

    private AddExp() {
        super();
    }

    static {
        addExp = new AddExp();
    }

    public static AddExp getInstance() {
        return addExp;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = MulExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!(nextWordCategoryCode.getValue().equals("PLUS")
                || nextWordCategoryCode.getValue().equals("MINU")
            )) {
                break;
            }
            OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = MulExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
