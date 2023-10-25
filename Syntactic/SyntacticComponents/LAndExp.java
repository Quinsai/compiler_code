package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class LAndExp extends SyntacticComponent {

    public LAndExp(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.LAndExp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        EqExp eqExp = new EqExp(treeNode);
        res = eqExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("AND")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            EqExp eqExp1 = new EqExp(treeNode);
            res = eqExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LAndExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
