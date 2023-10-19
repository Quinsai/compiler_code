package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Result.AnalysisResult;

public class Exp extends SyntacticComponent {

    public Exp() {
        super();
        this.valueType = ComponentValueType.INT;
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AddExp addExp = new AddExp();
        AnalysisResult res = addExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        this.valueType = addExp.valueType;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Exp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
