package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Result.AnalysisResult;

public class BlockItem extends SyntacticComponent {

    public static boolean isReturnWithValue;

    static {
        isReturnWithValue = false;
    }


    public BlockItem() {
        super();
        isReturnWithValue = false;
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("CONSTTK") || nextWordCategoryCode.getValue().equals("INTTK")) {
            Decl decl = new Decl();
            res = decl.analyze(whetherOutput);
            if (res != Result.AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }
        }
        else {
            Stmt stmt = new Stmt();
            res = stmt.analyze(whetherOutput);
            if (res != Result.AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }
        }

        return Result.AnalysisResult.SUCCESS;
    }
}
