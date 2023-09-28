package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class Cond extends SyntacticComponent {

    /**
     * 唯一单例
     */
    private static Cond cond;

    private Cond() {
        super();
    }

    static {
        cond = new Cond();
    }

    public static Cond getInstance() {
        return cond;
    }

    @Override
    public int analyze() {
        int res = LOrExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        OutputIntoFile.appendToFile("<Cond>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}