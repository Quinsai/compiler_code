package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import javax.xml.transform.sax.SAXTransformerFactory;

public class FuncRParams extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static FuncRParams funcRParams;

    private FuncRParams() {
        super();
    }

    static {
        funcRParams = new FuncRParams();
    }

    public static FuncRParams getInstance() {
        return funcRParams;
    }

    @Override
    public int analyze(boolean whetherOutput) {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = Exp.getInstance().analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
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

            res = Exp.getInstance().analyze(whetherOutput);
            if (res != SyntacticAnalysisResult.SUCCESS) {
                return SyntacticAnalysisResult.ERROR;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncRParams>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
