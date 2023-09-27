package Syntactic.SyntacticComponents;

import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class MainFuncDef extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static MainFuncDef mainFuncDef;

    private MainFuncDef() {
        super();
    }

    static {
        mainFuncDef = new MainFuncDef();
    }

    public static MainFuncDef getInstance() {
        return mainFuncDef;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        System.out.println(nextWordCategoryCode.getValue());
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return SyntacticAnalysisResult.ERROR;
        }


        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("MAINTK")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = Block.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<MainFuncDef>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
