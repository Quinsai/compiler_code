package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class ConstExp extends SyntacticComponent {

    public ConstExp() {
        super();
    }

    @Override
    public int analyze(boolean whetherOutput) {
        AddExp addExp = new AddExp();
        int res = addExp.analyze(whetherOutput);
        if (res != SyntacticAnalysisResult.SUCCESS) {
            return SyntacticAnalysisResult.ERROR;
        }

        this.value = addExp.value;

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstExp>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;
    }
}
