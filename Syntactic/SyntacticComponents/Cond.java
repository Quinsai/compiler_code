package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class Cond extends SyntacticComponent {

    public Cond() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        LOrExp lOrExp = new LOrExp();
        int res = lOrExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        this.value = lOrExp.value;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Cond>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
