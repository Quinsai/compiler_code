package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;

import java.util.ArrayList;

public class ConstInitVal extends SyntacticComponent {

    private ComponentValueType valueType;

    public ConstInitVal() {
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
        if (nextWordCategoryCode.getValue().equals("LBRACE")) {

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
                ConstInitVal constInitVal = new ConstInitVal();
                res = constInitVal.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                if (constInitVal.getValueType() == ComponentValueType.INT) {
                    partValueType =ComponentValueType.INT;
                    this.valueType = ComponentValueType.ONE_DIMENSION_ARRAY;
                }
                else if (constInitVal.getValueType() == ComponentValueType.ONE_DIMENSION_ARRAY) {
                    partValueType = ComponentValueType.ONE_DIMENSION_ARRAY;
                    this.valueType = ComponentValueType.TWO_DIMENSION_ARRAY;
                }
                else if (constInitVal.getValueType() == ComponentValueType.TWO_DIMENSION_ARRAY) {
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

                    ConstInitVal constInitVal1 = new ConstInitVal();
                    res = constInitVal1.analyze(whetherOutput);
                    if (res == AnalysisResult.FAIL) {
                        return AnalysisResult.FAIL;
                    }

                    if (constInitVal1.getValueType() != partValueType) {
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
        else {
            ConstExp constExp = new ConstExp();
            res = constExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.valueType = ComponentValueType.INT;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstInitVal>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }

    public ComponentValueType getValueType() {
        return valueType;
    }

}
