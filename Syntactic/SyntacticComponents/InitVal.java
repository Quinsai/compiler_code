package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class InitVal extends SyntacticComponent {

    public InitVal() {
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
        if (!nextWordCategoryCode.getValue().equals("LBRACE")) {
            Exp exp = new Exp();
            res = exp.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("RBRACE")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            }
            else {
                InitVal initVal = new InitVal();
                res = initVal.analyze(whetherOutput);
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

                    InitVal initVal1 = new InitVal();
                    res = initVal1.analyze(whetherOutput);
                    if (res != SyntacticAnalysisResult.SUCCESS) {
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

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<InitVal>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
