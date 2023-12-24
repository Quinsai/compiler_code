import Input.InputSourceCode;
import InterCode.Quaternion;
import Optimize.Register.RegisterAllocator;
import Output.OutputIntoFile;
import Result.Error.HandleError;
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

        if (HandleError.whetherError) {
            return;
        }

        Tree.getInstance().simplifyTree();

        // 生成四元式形式的中间代码
        Quaternion.getInstance().generateInterCode();
        // 重定向输出到intercode.txt中，仅仅为了检验一下四元式生成的正确性而已
        // 最后的时候要记得注释掉这行
        Quaternion.getInstance().display();

        // 启用下面这句，则开启优化，可能会有奇怪的未知的bug存在，不保证通过代码生成，保证通过竞速
        // 如果不启用，则不开启优化，但保证一定能通过代码生成
        // RegisterAllocator.getInstance().allocate();

        Target.getInstance().generateTargetCode();
        Target.getInstance().outputTargetCode();
    }
}
