import Input.InputSourceCode;
import Lexical.LexicalAnalysis;

/**
 * 编译器主类
 */
public class Compiler {

    public static void main(String[] args) {
        InputSourceCode.readSourceCode();

        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
        // 词法分析作业使用
        // lexicalAnalysis.run();
    }
}
