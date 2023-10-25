package Syntactic.SyntacticComponents;

import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class Cond extends SyntacticComponent {

    public Cond(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.Cond, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        LOrExp lOrExp = new LOrExp(treeNode);
        AnalysisResult res = lOrExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Cond>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
