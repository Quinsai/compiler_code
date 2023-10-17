package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class BlockItem extends SyntacticComponent {


    public BlockItem() {
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
        if (nextWordCategoryCode.getValue().equals("CONSTTK") || nextWordCategoryCode.getValue().equals("INTTK")) {
            Decl decl = new Decl();
            res = decl.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else {
            Stmt stmt = new Stmt();
            res = stmt.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        return SyntacticAnalysisResult.SUCCESS;
    }
}
