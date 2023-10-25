package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class LOrExp extends SyntacticComponent {

    public LOrExp(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.LOrExp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        LAndExp lAndExp = new LAndExp(treeNode);
        res = lAndExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("OR")) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<LOrExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            LAndExp lAndExp1 = new LAndExp(treeNode);
            res = lAndExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LOrExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
