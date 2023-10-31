package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysis;
import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class VarDecl extends SyntacticComponent {

    public VarDecl(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.VarDecl, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        BType bType = new BType(treeNode);
        res = bType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        VarDef varDef = new VarDef(treeNode);
        res = varDef.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            LexicalAnalysis.getInstance().skipErrorPart();
            return AnalysisResult.SUCCESS;
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

            VarDef varDef1 = new VarDef(treeNode);
            res = varDef1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                LexicalAnalysis.getInstance().skipErrorPart();
                return AnalysisResult.SUCCESS;
            }
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
            HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
            return AnalysisResult.SUCCESS;
        }
        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<VarDecl>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
