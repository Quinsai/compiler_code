package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Result.AnalysisResult;

public class Cond extends SyntacticComponent {

    public Cond() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        LOrExp lOrExp = new LOrExp();
        AnalysisResult res = lOrExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        this.value = lOrExp.value;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Cond>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
