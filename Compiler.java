import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysis;

/**
 * 编译器主类
 */
public class Compiler {

    public static void main(String[] args) {
        OutputIntoFile.cleanFile("output.txt");
        InputSourceCode.readSourceCode();

//        词法分析作业使用
//        LexicalAnalysis.getInstance().run(true);

//        语法分析作业使用
        SyntacticAnalysis.getInstance().run(true);
    }
}
