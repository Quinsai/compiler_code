package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class Stmt extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static Stmt stmt;

    private Stmt() {
        super();
    }

    static {
        stmt = new Stmt();
    }

    public static Stmt getInstance() {
        return stmt;
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

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCode.getValue().equals("SEMICN")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            OutputIntoFile.appendToFile("<Stmt>\n", "output.txt");
            return SyntacticAnalysisResult.SUCCESS;
        }

        res = lexicalAnalysis.peekMany(2, nextWordCategoryCodeArray, nextWordValueArray);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCodeArray[0].getValue().equals("IFTK")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = Cond.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = Stmt.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("ELSETK")) {
                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

                res = Stmt.getInstance().analyze();
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("FORTK")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
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
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                res = ForStmt.getInstance().analyze();
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                res = Cond.getInstance().analyze();
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                res = ForStmt.getInstance().analyze();
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

            res = Stmt.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("BREAKTK")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("BREAKTK")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("CONTINUETK")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("CONTINUETK")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("RETURNTK")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RETURNTK")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                res = Exp.getInstance().analyze();
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PRINTFTK")) {
            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("PRINTFTK")) {
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
            if (!nextWordCategoryCode.getValue().equals("STRCON")) {
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
                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

                res = Exp.getInstance().analyze();
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

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("LBRACE")) {
            res = Block.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            // TODO 疑似bug
            if (nextWordCategoryCodeArray[1].getValue().equals("ASSIGN") || nextWordCategoryCodeArray[1].getValue().equals("LBRACK")) {
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

                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != LexicalAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
                if (nextWordCategoryCode.getValue().equals("GETINTTK")) {
                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);

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

                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
                    if (res != LexicalAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                }
                else {
                    res = Exp.getInstance().analyze();
                    if (res != SyntacticAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }

                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
                    if (res != LexicalAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                }
            }
            else {
                res = Exp.getInstance().analyze();
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PLUS")
            || nextWordCategoryCodeArray[0].getValue().equals("MINU")
            || nextWordCategoryCodeArray[0].getValue().equals("NOT")
            || nextWordCategoryCodeArray[0].getValue().equals("LPARENT")
            || nextWordCategoryCodeArray[0].getValue().equals("INTCON")
        ) {
            res = Exp.getInstance().analyze();
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<Stmt>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
