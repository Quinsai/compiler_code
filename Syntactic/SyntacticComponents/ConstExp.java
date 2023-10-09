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
    public int analyze(boolean whetherOutput) {
        int res = AddExp.getInstance().analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }
        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
