package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class AddExp extends SyntacticComponent {

    public AddExp(TreeNode parent) {
        super();
        this.valueType = ComponentValueType.INT;
        this.treeNode = new TreeNode(TreeNodeName.AddExp, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        MulExp mulExp = new MulExp(treeNode);
        res = mulExp.analyze(whetherOutput);
        if (res != Result.AnalysisResult.SUCCESS) {
            return Result.AnalysisResult.FAIL;
        }

        this.valueType = mulExp.valueType;

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }
            if (!(nextWordCategoryCode.getValue().equals("PLUS")
                || nextWordCategoryCode.getValue().equals("MINU")
            )) {
                break;
            }
            if (whetherOutput) {
                OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            MulExp mulExp1 = new MulExp(treeNode);
            res = mulExp1.analyze(whetherOutput);
            if (res != Result.AnalysisResult.SUCCESS) {
                return Result.AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<AddExp>\n", "output.txt");
        }
        return Result.AnalysisResult.SUCCESS;
    }
}
