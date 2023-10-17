package Syntactic.SyntacticComponents;

import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class FuncDef extends SyntacticComponent {

    public FuncDef() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        FuncType funcType = new FuncType();
        res = funcType.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            FuncFParams funcFParams = new FuncFParams();
            res = funcFParams.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
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
            OutputIntoFile.appendToFile("<FuncDef>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
