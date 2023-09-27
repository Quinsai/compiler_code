package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class Exp extends SyntacticComponent {
    /**
     * 唯一单例
     */
    private static Exp exp;

    private Exp() {
        super();
    }

    static {
        exp = new Exp();
    }

    public static Exp getInstance() {
        return exp;
    }

    @Override
    public int analyze() {
        int res = AddExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        OutputIntoFile.appendToFile("<Exp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
