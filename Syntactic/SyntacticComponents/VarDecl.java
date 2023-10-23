package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysis;
import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;

public class VarDecl extends SyntacticComponent {

    public VarDecl() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        BType bType = new BType();
        res = bType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        VarDef varDef = new VarDef();
        res = varDef.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            LexicalAnalysis.getInstance().skipErrorPart();
//            return AnalysisResult.FAIL;
            return AnalysisResult.SUCCESS;
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

            VarDef varDef1 = new VarDef();
            res = varDef1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                LexicalAnalysis.getInstance().skipErrorPart();
//                return AnalysisResult.FAIL;
                return AnalysisResult.SUCCESS;
            }
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

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<VarDecl>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
