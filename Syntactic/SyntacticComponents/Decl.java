package Syntactic.SyntacticComponents;

import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class Decl extends SyntacticComponent {

    public Decl() {
        super();
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
            ConstDecl constDecl = new ConstDecl();
            res = constDecl.analyze(whetherOutput);
        }
        else if (categoryCode.equals("INTTK")) {
            VarDecl varDecl = new VarDecl();
            res = varDecl.analyze(whetherOutput);
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
