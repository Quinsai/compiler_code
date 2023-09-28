package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class ForStmt extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static ForStmt forStmt;

    private ForStmt() {
        super();
    }

    static {
        forStmt = new ForStmt();
    }

    public static ForStmt getInstance() {
        return forStmt;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = LVal.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("ASSIGN")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = Exp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<ForStmt>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
