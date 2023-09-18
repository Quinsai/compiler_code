import Lexical.LexicalAnalysis;

/**
 * 编译器主类
 */
public class Compiler {

    public static void main(String[] args) {
        String sourceCode = InputSourceCode.getSourceCode();

        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(sourceCode);
        lexicalAnalysis.run();
    }
}
