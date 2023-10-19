package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.SymbolConst;

import java.util.ArrayList;

public class UnaryExp extends SyntacticComponent {

    public UnaryExp() {
        super();
        this.valueType = ComponentValueType.INT;
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

        res = lexicalAnalysis.peekMany(2, nextWordCategoryCodeArray, nextWordValueArray);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCodeArray[0].getValue().equals("LPARENT")) {
            PrimaryExp primaryExp = new PrimaryExp();
            res = primaryExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.valueType = primaryExp.valueType;
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            if (nextWordCategoryCodeArray[1].getValue().equals("LPARENT")) {

                String functionName;
                ArrayList<ComponentValueType> functionRealParams = new ArrayList<>();

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                functionName = nextWordValue.getValue();
                res = masterTable.checkReference(functionName);
                if (res == AnalysisResult.FAIL) {
                    return AnalysisResult.FAIL;
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

                res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                    FuncRParams funcRParams = new FuncRParams();
                    res = funcRParams.analyze(whetherOutput);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }

                    functionRealParams.addAll(funcRParams.getRealParamsTypeList());
                }

                res = masterTable.checkFunctionParams(functionName, functionRealParams);
                if (res == AnalysisResult.FAIL) {
                    return AnalysisResult.FAIL;
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                    HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
                    return AnalysisResult.FAIL;
                }

                ParamResult<SymbolConst> returnType = new ParamResult<>(null);
                res = masterTable.getFunctionReturnType(functionName, returnType);
                if (res == AnalysisResult.FAIL) {
                    return AnalysisResult.FAIL;
                }

                if (returnType.getValue() == SymbolConst.INT) {
                    this.valueType = ComponentValueType.INT;
                }
                else if (returnType.getValue() == SymbolConst.VOID) {
                    this.valueType = ComponentValueType.VOID;
                }
            }
            else {
                PrimaryExp primaryExp = new PrimaryExp();
                res = primaryExp.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                this.valueType = primaryExp.valueType;
            }
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("PLUS")
            || nextWordCategoryCodeArray[0].getValue().equals("MINU")
            || nextWordCategoryCodeArray[0].getValue().equals("NOT")
        ) {
            UnaryOp unaryOp = new UnaryOp();
            res = unaryOp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            UnaryExp unaryExp1 = new UnaryExp();
            res = unaryExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("INTCON")) {
            PrimaryExp primaryExp = new PrimaryExp();
            res = primaryExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.valueType = primaryExp.valueType;
        }
        else {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<UnaryExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
