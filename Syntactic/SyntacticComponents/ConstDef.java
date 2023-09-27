package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class ConstDef extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static ConstDef constDef;

    private ConstDef() {
        super();
    }

    static {
        constDef = new ConstDef();
    }

    public static ConstDef getInstance() {
        return constDef;
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
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("LBRACK")) {
                break;
            }
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = ConstExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                return  SyntacticAnalysisResult.ERROR;
            }
        }

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("ASSIGN")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = ConstInitVal.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<ConstDef>\n", "output");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
