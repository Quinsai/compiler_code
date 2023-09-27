package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class VarDecl extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static VarDecl varDecl;

    private VarDecl() {
        super();
    }

    static {
        varDecl = new VarDecl();
    }

    public static VarDecl getInstance() {
        return varDecl;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = BType.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = VarDef.getInstance().analyze();
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
            res = VarDef.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<VarDecl>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
