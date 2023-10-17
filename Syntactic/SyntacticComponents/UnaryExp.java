package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import javax.naming.ldap.StartTlsRequest;

public class UnaryExp extends SyntacticComponent {

    public UnaryExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ParamResult<String>[] nextWordCategoryCodeArray = new ParamResult[2];
        ParamResult<String>[] nextWordValueArray = new ParamResult[2];

        for (int i = 0; i < 2; i++) {
            nextWordCategoryCodeArray[i] = new ParamResult<>("");
            nextWordValueArray[i] = new ParamResult<>("");
        }

        res = lexicalAnalysis.peekMany(2, nextWordCategoryCodeArray, nextWordValueArray);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return LexicalAnalysisResult.ERROR;
        }
        if (nextWordCategoryCodeArray[0].getValue().equals("LPARENT")) {
            PrimaryExp primaryExp = new PrimaryExp();
            res = primaryExp.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            this.value = primaryExp.value;
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            if (nextWordCategoryCodeArray[1].getValue().equals("LPARENT")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != LexicalAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
                if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                    FuncRParams funcRParams = new FuncRParams();
                    res = funcRParams.analyze(whetherOutput);
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

                this.value = 1;
            }
            else {
                PrimaryExp primaryExp = new PrimaryExp();
                res = primaryExp.analyze(whetherOutput);
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }

                this.value = primaryExp.value;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PLUS")
            || nextWordCategoryCodeArray[0].getValue().equals("MINU")
            || nextWordCategoryCodeArray[0].getValue().equals("NOT")
        ) {
            UnaryOp unaryOp = new UnaryOp();
            res = unaryOp.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            UnaryExp unaryExp1 = new UnaryExp();
            res = unaryExp1.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            this.value = unaryExp1.value;

            if (nextWordCategoryCodeArray[0].getValue().equals("PLUS")) {
                this.value = unaryExp1.value;
            }
            else if (nextWordCategoryCodeArray[0].getValue().equals("MINU")) {
                this.value = -1 * unaryExp1.value;
            }
            else if (nextWordCategoryCodeArray[0].getValue().equals("NOT")) {
                this.value = 1 - unaryExp1.value;
            }

        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("INTCON")) {
            PrimaryExp primaryExp = new PrimaryExp();
            res = primaryExp.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            this.value = primaryExp.value;
        }
        else {
            return SyntacticAnalysisResult.ERROR;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<UnaryExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
