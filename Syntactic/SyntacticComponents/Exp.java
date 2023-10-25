package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class Exp extends SyntacticComponent {

    public Exp(TreeNode parent) {
        super();
        this.valueType = ComponentValueType.INT;
        this.treeNode = new TreeNode(TreeNodeName.Exp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AddExp addExp = new AddExp(treeNode);
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
