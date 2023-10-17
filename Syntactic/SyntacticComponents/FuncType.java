package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import javax.management.loading.ClassLoaderRepository;

public class FuncType extends SyntacticComponent {

    public FuncType() {
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
        if (!(nextWordCategoryCode.getValue().equals("VOIDTK")
            || nextWordCategoryCode.getValue().equals("INTTK")
        )) {
            return SyntacticAnalysisResult.ERROR;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncType>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
