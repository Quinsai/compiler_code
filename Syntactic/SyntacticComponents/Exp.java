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
    public int analyze(boolean whetherOutput) {
        int res = AddExp.getInstance().analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Exp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
