package Syntactic.SyntacticComponents;

import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class MainFuncDef extends SyntacticComponent {

    public MainFuncDef() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return SyntacticAnalysisResult.ERROR;
        }


        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("MAINTK")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            return SyntacticAnalysisResult.ERROR;
        }

        Block block = new Block();
        res = block.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<MainFuncDef>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
