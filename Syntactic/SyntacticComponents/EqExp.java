package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class EqExp extends SyntacticComponent {

    public EqExp(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.EqExp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        // TODO 把从这往后的所有文件的终结符添加到语法树中去

        RelExp relExp = new RelExp(treeNode);
        res = relExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("EQL")
                || nextWordCategoryCode.getValue().equals("NEQ")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<EqExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            RelExp relExp1 = new RelExp(treeNode);
            res = relExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<EqExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
