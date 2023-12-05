package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Result.AnalysisResult;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class ConstExp extends SyntacticComponent {

    public ConstExp(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.ConstExp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AddExp addExp = new AddExp(treeNode);
        AnalysisResult res = addExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
