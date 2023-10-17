package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class ConstInitVal extends SyntacticComponent {

    public ConstInitVal() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {

        int res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCode.getValue().equals("LBRACE")) {

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("RBRACE")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            }
            else {
                ConstInitVal constInitVal = new ConstInitVal();
                res = constInitVal.analyze(whetherOutput);
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
                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                    ConstInitVal constInitVal1 = new ConstInitVal();
                    res = constInitVal1.analyze(whetherOutput);
                    if (res == SyntacticAnalysisResult.ERROR) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != LexicalAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
                if (!nextWordCategoryCode.getValue().equals("RBRACE")) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
        }
        else {
            ConstExp constExp = new ConstExp();
            res = constExp.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstInitVal>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
