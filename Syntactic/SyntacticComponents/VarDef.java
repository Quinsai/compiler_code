package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

public class VarDef extends SyntacticComponent {

    public VarDef() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("LBRACK")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            ConstExp constExp = new ConstExp();
            res = constExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                return AnalysisResult.FAIL;
            }
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("ASSIGN")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            InitVal initVal = new InitVal();
            res = initVal.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<VarDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
