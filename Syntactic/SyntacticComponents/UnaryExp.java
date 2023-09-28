package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import javax.naming.ldap.StartTlsRequest;

public class UnaryExp extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static UnaryExp unaryExp;

    private UnaryExp() {
        super();
    }

    static {
        unaryExp = new UnaryExp();
    }

    public static UnaryExp getInstance() {
        return unaryExp;
    }

    @Override
    public int analyze() {
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
            res = PrimaryExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            if (nextWordCategoryCodeArray[1].getValue().equals("LPARENT")) {
                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != LexicalAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
                if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                    res = FuncRParams.getInstance().analyze();
                    if (res != SyntacticAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                }

                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
                if (res != LexicalAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
                if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
            else {
                res = PrimaryExp.getInstance().analyze();
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PLUS")
            || nextWordCategoryCodeArray[0].getValue().equals("MINU")
            || nextWordCategoryCodeArray[0].getValue().equals("NOT")
        ) {
            res = UnaryOp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = UnaryExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("INTCON")) {
            res = PrimaryExp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<UnaryExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
