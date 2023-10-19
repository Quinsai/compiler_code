package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Result.AnalysisResult;

public class Exp extends SyntacticComponent {

    public Exp() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AddExp addExp = new AddExp();
        AnalysisResult res = addExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Exp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
