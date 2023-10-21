package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.Scope.ScopeStack;

public class Stmt extends SyntacticComponent {

    /**
     * （如果在函数中的）函数返回值类型
     */
    public static ComponentValueType functionReturnType;

    /**
     * 是否在循环中
     */
    public static boolean isInCirculate;

    static {
        functionReturnType = ComponentValueType.NO_MEANING;
        isInCirculate = false;
    }

    public Stmt() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ParamResult<String>[] nextWordCategoryCodeArray = new ParamResult[2];
        ParamResult<String>[] nextWordValueArray = new ParamResult[2];
        for (int i = 0; i < 2; i++) {
            nextWordCategoryCodeArray[i] = new ParamResult<>("");
            nextWordValueArray[i] = new ParamResult<>("");
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("SEMICN")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<Stmt>\n", "output.txt");
            }
            return AnalysisResult.SUCCESS;
        }

        res = lexicalAnalysis.peekMany(2, nextWordCategoryCodeArray, nextWordValueArray);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCodeArray[0].getValue().equals("IFTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
                return AnalysisResult.FAIL;
            }

            Cond cond = new Cond();
            res = cond.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
                return AnalysisResult.SUCCESS;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            ScopeStack.getInstance().enterScope();

            Stmt stmt = new Stmt();
            res = stmt.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            ScopeStack.getInstance().quitScope();

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (nextWordCategoryCode.getValue().equals("ELSETK")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                ScopeStack.getInstance().enterScope();

                Stmt stmt1 = new Stmt();
                res = stmt1.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                ScopeStack.getInstance().quitScope();
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("FORTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                ForStmt forStmt = new ForStmt();
                res = forStmt.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                Cond cond = new Cond();
                res = cond.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                ForStmt forStmt = new ForStmt();
                res = forStmt.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
                return AnalysisResult.SUCCESS;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            ScopeStack.getInstance().enterScope();

            Stmt stmt = new Stmt();
            Stmt.isInCirculate = true;
            res = stmt.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            Stmt.isInCirculate = false;
            ScopeStack.getInstance().quitScope();
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("BREAKTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("BREAKTK")) {
                return AnalysisResult.FAIL;
            }

            if (!isInCirculate) {
                HandleError.handleError(AnalysisErrorType.UNEXPECTED_BREAK_OR_CONTINUE);
                return AnalysisResult.SUCCESS;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                return AnalysisResult.FAIL;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("CONTINUETK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("CONTINUETK")) {
                return AnalysisResult.FAIL;
            }

            if (!isInCirculate) {
                HandleError.handleError(AnalysisErrorType.UNEXPECTED_BREAK_OR_CONTINUE);
                return AnalysisResult.SUCCESS;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
                return AnalysisResult.SUCCESS;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("RETURNTK")) {
            boolean hasReturnValue = false;

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RETURNTK")) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                Exp exp = new Exp();
                res = exp.analyze(whetherOutput);
                hasReturnValue = true;
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
            }

            if (Stmt.functionReturnType != ComponentValueType.INT && hasReturnValue) {
                HandleError.handleError(AnalysisErrorType.VOID_FUNCTION_WITH_RETURN);
                return AnalysisResult.SUCCESS;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
                return AnalysisResult.SUCCESS;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            BlockItem.isReturnWithValue = true;
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PRINTFTK")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("PRINTFTK")) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("STRCON")) {
                return AnalysisResult.FAIL;
            }

            while (true) {
                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                    break;
                }
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                Exp exp = new Exp();
                res = exp.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
                return AnalysisResult.SUCCESS;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
                return AnalysisResult.SUCCESS;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("LBRACE")) {
            ScopeStack.getInstance().enterScope();

            Block block = new Block();
            res = block.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            ScopeStack.getInstance().quitScope();
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            boolean hadAssignBeforeSemicn = lexicalAnalysis.haveAssignBeforeSemicn();
            if (hadAssignBeforeSemicn) {
                LVal lVal = new LVal(LVal.ASSIGN);
                res = lVal.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("ASSIGN")) {
                    return AnalysisResult.FAIL;
                }

                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (nextWordCategoryCode.getValue().equals("GETINTTK")) {
                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }
                    if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
                        return AnalysisResult.FAIL;
                    }

                    res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }
                    if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                        HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
                        return AnalysisResult.SUCCESS;
                    }
                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                    res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }
                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                        HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
                        return AnalysisResult.SUCCESS;
                    }
                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                }
                else {
                    Exp exp = new Exp();
                    res = exp.analyze(whetherOutput);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }

                    res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }
                    if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                        HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
                        return AnalysisResult.SUCCESS;
                    }
                    res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                }
            }
            else {
                Exp exp =  new Exp();
                res = exp.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                    HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
                    return AnalysisResult.SUCCESS;
                }
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
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
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
                return AnalysisResult.SUCCESS;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        }
        else {
            HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
            return AnalysisResult.SUCCESS;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Stmt>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
