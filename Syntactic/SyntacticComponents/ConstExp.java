package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Result.AnalysisResult;

public class ConstExp extends SyntacticComponent {

    public ConstExp() {
        super();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AddExp addExp = new AddExp();
        AnalysisResult res = addExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        this.intValue = 1;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
