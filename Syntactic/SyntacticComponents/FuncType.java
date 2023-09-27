package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

import javax.management.loading.ClassLoaderRepository;

public class FuncType extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static FuncType funcType;

    private FuncType() {
        super();
    }

    static {
        funcType = new FuncType();
    }

    public static FuncType getInstance() {
        return funcType;
    }

    @Override
    public int analyze() {
        int res = 0;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("VOIDTK")) {
            return SyntacticAnalysisResult.ERROR;
        }

        res = lexicalAnalysis.next(true, nextWordCategoryCode, nextWordValue);
        if (res != LexicalAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return SyntacticAnalysisResult.ERROR;
        }

        OutputIntoFile.appendToFile("<FuncType>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
