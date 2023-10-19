package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;

import java.util.ArrayList;

public class InitVal extends SyntacticComponent {

    private ComponentValueType valueType;

    public InitVal() {
        super();
        this.valueType = ComponentValueType.INT;
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ComponentValueType partValueType = ComponentValueType.INT;

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("LBRACE")) {
            Exp exp = new Exp();
            res = exp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.valueType = ComponentValueType.INT;
        }
        else {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (nextWordCategoryCode.getValue().equals("RBRACE")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                this.valueType = ComponentValueType.ONE_DIMENSION_ARRAY;
            }
            else {
                InitVal initVal = new InitVal();
                res = initVal.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                if (initVal.getValueType() == ComponentValueType.INT) {
                    partValueType = ComponentValueType.INT;
                    this.valueType = ComponentValueType.ONE_DIMENSION_ARRAY;
                }
                else if (initVal.getValueType() == ComponentValueType.ONE_DIMENSION_ARRAY) {
                    partValueType = ComponentValueType.ONE_DIMENSION_ARRAY;
                    this.valueType = ComponentValueType.TWO_DIMENSION_ARRAY;
                }
                else if (initVal.getValueType() == ComponentValueType.TWO_DIMENSION_ARRAY) {
                    HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_BEYOND_TWO);
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

                    InitVal initVal1 = new InitVal();
                    res = initVal1.analyze(whetherOutput);
                    if (res != AnalysisResult.SUCCESS) {
                        return AnalysisResult.FAIL;
                    }

                    if (initVal1.getValueType() != partValueType) {
                        HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_NOT_MATCH);
                        return AnalysisResult.FAIL;
                    }
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("RBRACE")) {
                    HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                    return AnalysisResult.FAIL;
                }
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<InitVal>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }

    public ComponentValueType getValueType() {
        return valueType;
    }

}
