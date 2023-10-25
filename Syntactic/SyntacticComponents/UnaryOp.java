package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class UnaryOp extends SyntacticComponent {

    public UnaryOp(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.UnaryOp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!(nextWordCategoryCode.getValue().equals("PLUS")
            || nextWordCategoryCode.getValue().equals("MINU")
            || nextWordCategoryCode.getValue().equals("NOT"))
        ) {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<UnaryOp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
