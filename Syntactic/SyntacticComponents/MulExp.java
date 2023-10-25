package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class MulExp extends SyntacticComponent {

    public MulExp(TreeNode parent) {
        super();
        this.valueType = ComponentValueType.INT;
        this.treeNode = new TreeNode(TreeNodeName.MulExp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        UnaryExp unaryExp = new UnaryExp(treeNode);
        res = unaryExp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        this.valueType = unaryExp.valueType;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("MULT")
                || nextWordCategoryCode.getValue().equals("DIV")
                || nextWordCategoryCode.getValue().equals("MOD")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            UnaryExp unaryExp1 = new UnaryExp(treeNode);
            res = unaryExp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<MulExp>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
