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

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCode.getValue().equals("SEMICN")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<Stmt>\n", "output.txt");
            }
            return SyntacticAnalysisResult.SUCCESS;
        }

        res = lexicalAnalysis.peekMany(2, nextWordCategoryCodeArray, nextWordValueArray);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (nextWordCategoryCodeArray[0].getValue().equals("IFTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = Cond.getInstance().analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = Stmt.getInstance().analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("ELSETK")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                res = Stmt.getInstance().analyze(whetherOutput);
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("FORTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

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
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                res = ForStmt.getInstance().analyze(whetherOutput);
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
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
                res = Cond.getInstance().analyze(whetherOutput);
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
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
                res = ForStmt.getInstance().analyze(whetherOutput);
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

            res = Stmt.getInstance().analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("BREAKTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("BREAKTK")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("CONTINUETK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("CONTINUETK")) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("RETURNTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
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
                res = Exp.getInstance().analyze(whetherOutput);
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PRINTFTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("PRINTFTK")) {
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
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                res = Exp.getInstance().analyze(whetherOutput);
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

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("LBRACE")) {
            res = Block.getInstance().analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            boolean hadAssignBeforeSemicn = lexicalAnalysis.haveAssignBeforeSemicn();
            if (hadAssignBeforeSemicn) {
                res = LVal.getInstance().analyze(whetherOutput);
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
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
                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

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

                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                    if (res != LexicalAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                }
                else {
                    res = Exp.getInstance().analyze(whetherOutput);
                    if (res != SyntacticAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }

                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                    if (res != LexicalAnalysisResult.SUCCESS) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                        return SyntacticAnalysisResult.ERROR;
                    }
                }
            }
            else {

                res = Exp.getInstance().analyze(whetherOutput);
                if (res != SyntacticAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != LexicalAnalysisResult.SUCCESS) {
                    return SyntacticAnalysisResult.ERROR;
                }
                if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                    return SyntacticAnalysisResult.ERROR;
                }
            }
            /*
            如果我没猜错，问题可能就是出在这里
            就是偷看的第二位是左方括号的情况
             */
//            if (nextWordCategoryCodeArray[1].getValue().equals("ASSIGN")) {
//                res = LVal.getInstance().analyze();
//                if (res != SyntacticAnalysisResult.SUCCESS) {
//                    return SyntacticAnalysisResult.ERROR;
//                }
//
//                res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                if (res != LexicalAnalysisResult.SUCCESS) {
//                    return SyntacticAnalysisResult.ERROR;
//                }
//                if (!nextWordCategoryCode.getValue().equals("ASSIGN")) {
//                    return SyntacticAnalysisResult.ERROR;
//                }
//
//                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
//                if (res != LexicalAnalysisResult.SUCCESS) {
//                    return SyntacticAnalysisResult.ERROR;
//                }
//                if (nextWordCategoryCode.getValue().equals("GETINTTK")) {
//                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//
//                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                    if (res != LexicalAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                    if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//
//                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                    if (res != LexicalAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                    if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//
//                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                    if (res != LexicalAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                }
//                else {
//                    res = Exp.getInstance().analyze();
//                    if (res != SyntacticAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//
//                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                    if (res != LexicalAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                }
//            }
//            else if (nextWordCategoryCodeArray[1].getValue().equals("LBRACK")) {
//                boolean hadAssignBeforeSemicn = lexicalAnalysis.haveAssignBeforeSemicn();
//                if (hadAssignBeforeSemicn) {
//                    res = LVal.getInstance().analyze();
//                    if (res != SyntacticAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//
//                    res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                    if (res != LexicalAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                    if (!nextWordCategoryCode.getValue().equals("ASSIGN")) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//
//                    res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
//                    if (res != LexicalAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                    if (nextWordCategoryCode.getValue().equals("GETINTTK")) {
//                        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//
//                        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                        if (res != LexicalAnalysisResult.SUCCESS) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//                        if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//
//                        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                        if (res != LexicalAnalysisResult.SUCCESS) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//                        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//
//                        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                        if (res != LexicalAnalysisResult.SUCCESS) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//                        if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//                    }
//                    else {
//                        res = Exp.getInstance().analyze();
//                        if (res != SyntacticAnalysisResult.SUCCESS) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//
//                        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
//                        if (res != LexicalAnalysisResult.SUCCESS) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//                        if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
//                            return SyntacticAnalysisResult.ERROR;
//                        }
//                    }
//                }
//                else {
//                    res = Exp.getInstance().analyze();
//                    if (res != SyntacticAnalysisResult.SUCCESS) {
//                        return SyntacticAnalysisResult.ERROR;
//                    }
//                }
//            }
//            else {
//                res = Exp.getInstance().analyze();
//                if (res != SyntacticAnalysisResult.SUCCESS) {
//                    return SyntacticAnalysisResult.ERROR;
//                }
//            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PLUS")
            || nextWordCategoryCodeArray[0].getValue().equals("MINU")
            || nextWordCategoryCodeArray[0].getValue().equals("NOT")
            || nextWordCategoryCodeArray[0].getValue().equals("LPARENT")
            || nextWordCategoryCodeArray[0].getValue().equals("INTCON")
        ) {
            res = Exp.getInstance().analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
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

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Stmt>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
