package Syntactic.SyntacticComponents;

import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class Decl extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static Decl decl;

    private Decl() {
        super();
    }

    static {
        decl = new Decl();
    }

    public static Decl getInstance() {
        return decl;
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        String categoryCode = nextWordCategoryCode.getValue();
        if (categoryCode.equals("CONSTTK")) {
            res = ConstDecl.getInstance().analyze(whetherOutput);
        }
        else if (categoryCode.equals("INTTK")) {
            res = VarDecl.getInstance().analyze(whetherOutput);
        }
        else {
            res = SyntacticAnalysisResult.ERROR;
        }
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        return SyntacticAnalysisResult.SUCCESS;
    }
}
