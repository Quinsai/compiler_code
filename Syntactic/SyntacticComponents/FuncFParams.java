package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysis;
import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class FuncFParams extends SyntacticComponent {

    public FuncFParams(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.FuncFParams, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        FuncFParam funcFParam = new FuncFParam(treeNode);
        res = funcFParam.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            System.out.println("www");
            // LexicalAnalysis.getInstance().skipToNextComma();
            // return AnalysisResult.FAIL;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            FuncFParam funcFParam1 = new FuncFParam(treeNode);
            res = funcFParam1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                System.out.println("www");
                // LexicalAnalysis.getInstance().skipToNextComma();
                // return AnalysisResult.FAIL;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncFParams>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
