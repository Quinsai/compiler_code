package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class LVal extends SyntacticComponent {

    public LVal() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return SyntacticAnalysisResult.ERROR;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("LBRACK")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            Exp exp = new Exp();
            res = exp.analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            if (res != LexicalAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        // TODO 解决LVal获取value的问题
        /*
        这里写的3124仅仅是为了这个能跑起来而已
         */
        this.value = 3124;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LVal>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
