import Input.InputSourceCode;
import InterCode.Quaternion;
import Lexical.LexicalAnalysis;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysis;
import SyntacticTree.Tree;

/**
 * 编译器主类
 */
public class Compiler {

    public static void main(String[] args) {
        OutputIntoFile.cleanFile("output.txt");
        OutputIntoFile.cleanFile("error.txt");
        OutputIntoFile.cleanFile("intercode.txt");
        InputSourceCode.readSourceCode();

        // 词法分析作业使用
        // LexicalAnalysis.getInstance().run(true);

        // 语法分析作业使用
        SyntacticAnalysis.getInstance().run(true);

        Tree.getInstance().simplifyTree();

        // 生成四元式形式的中间代码
        Quaternion.getInstance().generateInterCode();
        // 重定向输出到intercode.txt中，仅仅为了检验一下四元式生成的正确性而已
        // 最后的时候要记得注释掉这行
        Quaternion.getInstance().display();
    }
}
