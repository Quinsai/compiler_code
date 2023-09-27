package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class ConstExp extends SyntacticComponent {
    /**
     * 唯一单例
     */
    private static ConstExp constExp;

    private ConstExp() {
        super();
    }

    static {
        constExp = new ConstExp();
    }

    public static ConstExp getInstance() {
        return constExp;
    }

    @Override
    public int analyze() {
        int res = AddExp.getInstance().analyze();
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        OutputIntoFile.appendToFile("<ConstExp>\n", "output.txt");
        return SyntacticAnalysisResult.SUCCESS;
    }
}
