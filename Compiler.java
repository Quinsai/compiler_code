import Input.InputSourceCode;
import InterCode.Quaternion;
import Optimize.Register.RegisterAllocator;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysis;
import SyntacticTree.Tree;
import TargetCode.Target;

/**
 * 编译器主类
 */
public class Compiler {

    public static void main(String[] args) {
        OutputIntoFile.cleanFile("output.txt");
        OutputIntoFile.cleanFile("error.txt");
        OutputIntoFile.cleanFile("intercode.txt");
        OutputIntoFile.cleanFile("mips.txt");
        InputSourceCode.readSourceCode();

        // 词法分析作业使用
        // LexicalAnalysis.getInstance().run(true);

        // 语法分析作业使用
        SyntacticAnalysis.getInstance().run(false);

        Tree.getInstance().simplifyTree();

        // 生成四元式形式的中间代码
        Quaternion.getInstance().generateInterCode();
        // 重定向输出到intercode.txt中，仅仅为了检验一下四元式生成的正确性而已
        // 最后的时候要记得注释掉这行
        Quaternion.getInstance().display();

        RegisterAllocator.getInstance().allocate();

        Target.getInstance().generateTargetCode();
        Target.getInstance().outputTargetCode();
    }
}
