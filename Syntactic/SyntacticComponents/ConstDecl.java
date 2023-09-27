package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class ConstDecl extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static ConstDecl constDecl;

    private ConstDecl() {
        super();
    }

    static {
        constDecl = new ConstDecl();
    }

    public static ConstDecl getInstance() {
        return constDecl;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(true, nextWordCategoryCode,nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        else if (!nextWordCategoryCode.getValue().equals("CONSTTK")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = BType.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = ConstDef.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res == LexicalAnalysisResult.ERROR) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                break;
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            res = ConstDef.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res == LexicalAnalysisResult.ERROR) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<ConstDecl>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
