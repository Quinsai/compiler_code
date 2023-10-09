package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class RelExp extends SyntacticComponent {
    /**
     * 唯一单例
     */
    private static RelExp relExp;

    private RelExp() {
        super();
    }

    static {
        relExp = new RelExp();
    }

    public static RelExp getInstance() {
        return relExp;
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = AddExp.getInstance().analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!(nextWordCategoryCode.getValue().equals("LSS")
                || nextWordCategoryCode.getValue().equals("LEQ")
                || nextWordCategoryCode.getValue().equals("GRE")
                || nextWordCategoryCode.getValue().equals("GEQ")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<RelExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = AddExp.getInstance().analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<RelExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
