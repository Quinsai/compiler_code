package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class Block extends SyntacticComponent {

    public Block(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.Block, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("LBRACE")) {
            return Result.AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }
            if (nextWordCategoryCode.getValue().equals("RBRACE")) {
                break;
            }

            BlockItem blockItem = new BlockItem(treeNode);
            res = blockItem.analyze(whetherOutput);
            if (res != Result.AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("RBRACE")) {
            return Result.AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<Block>\n", "output.txt");
        }
        return Result.AnalysisResult.SUCCESS;
    }
}
