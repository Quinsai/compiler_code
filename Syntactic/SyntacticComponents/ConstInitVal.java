package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class ConstInitVal extends SyntacticComponent {
    /**
     * 唯一单例
     */
    private static ConstInitVal constInitVal;

    private ConstInitVal() {
        super();
    }

    static {
        constInitVal = new ConstInitVal();
    }

    public static ConstInitVal getInstance() {
        return constInitVal;
    }

    @Override
    public int analyze() {

        int res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCode.getValue().equals("LBRACE")) {

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("RBRACE")) {
                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            }
            else {
                res = ConstInitVal.getInstance().analyze();
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }

                while (true) {
                    res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                    if (res != LexicalAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                    if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                        break;
                    }
                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

                    res = ConstInitVal.getInstance().analyze();
                    if (res == SyntacticAnalysisResult.ERROR) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                }

                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
                if (res != LexicalAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
                if (!nextWordCategoryCode.getValue().equals("RBRACE")) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
        }
        else {
            res = ConstExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        OutputIntoFile.appendToFile("<ConstInitVal>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
