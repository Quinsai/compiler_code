package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class UnaryExp extends SyntacticComponent {

    public UnaryExp() {
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
            this.value = primaryExp.value;
        }
        else if (nextWordCategoryCodeArray[0].getValue().equals("IDENFR")) {
            if (nextWordCategoryCodeArray[1].getValue().equals("LPARENT")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
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
                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
                    return AnalysisResult.FAIL;
                }

                this.value = 1;
            }
            else {
                PrimaryExp primaryExp = new PrimaryExp();
                res = primaryExp.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
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
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            UnaryExp unaryExp1 = new UnaryExp();
            res = unaryExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
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
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.value = primaryExp.value;
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
