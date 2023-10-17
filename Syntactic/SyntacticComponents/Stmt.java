package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import java.util.concurrent.Callable;

public class Stmt extends SyntacticComponent {

    public Stmt() {
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

            Cond cond = new Cond();
            res = cond.analyze(whetherOutput);
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

            Stmt stmt = new Stmt();
            res = stmt.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (nextWordCategoryCode.getValue().equals("ELSETK")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                Stmt stmt1 = new Stmt();
                res = stmt1.analyze(whetherOutput);
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
                ForStmt forStmt = new ForStmt();
                res = forStmt.analyze(whetherOutput);
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
                Cond cond = new Cond();
                res = cond.analyze(whetherOutput);
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
                ForStmt forStmt = new ForStmt();
                res = forStmt.analyze(whetherOutput);
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

            Stmt stmt = new Stmt();
            res = stmt.analyze(whetherOutput);
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
                Exp exp = new Exp();
                res = exp.analyze(whetherOutput);
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

                Exp exp = new Exp();
                res = exp.analyze(whetherOutput);
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
            Block block = new Block();
            res = block.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            boolean hadAssignBeforeSemicn = lexicalAnalysis.haveAssignBeforeSemicn();
            if (hadAssignBeforeSemicn) {
                LVal lVal = new LVal();
                res = lVal.analyze(whetherOutput);
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
                    Exp exp = new Exp();
                    res = exp.analyze(whetherOutput);
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
                Exp exp =  new Exp();
                res = exp.analyze(whetherOutput);
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
        else if (nextWordCategoryCodeArray[0].getValue().equals("PLUS")
            || nextWordCategoryCodeArray[0].getValue().equals("MINU")
            || nextWordCategoryCodeArray[0].getValue().equals("NOT")
            || nextWordCategoryCodeArray[0].getValue().equals("LPARENT")
            || nextWordCategoryCodeArray[0].getValue().equals("INTCON")
        ) {
            Exp exp = new Exp();
            res = exp.analyze(whetherOutput);
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
