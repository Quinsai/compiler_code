package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Result.AnalysisResult;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class BType extends SyntacticComponent {


    public BType(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.BType, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        AnalysisResult res;

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

        if (res != AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("INTTK")) {
            return Result.AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        return Result.AnalysisResult.SUCCESS;
    }
}
