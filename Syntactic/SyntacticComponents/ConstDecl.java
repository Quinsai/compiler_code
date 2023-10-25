package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysis;
import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class ConstDecl extends SyntacticComponent {

   public ConstDecl(TreeNode parent) {
       super();
       this.treeNode = new TreeNode(TreeNodeName.ConstDecl, "", parent);
   }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode,nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        else if (!nextWordCategoryCode.getValue().equals("CONSTTK")) {
            return AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        BType bType = new BType(treeNode);
        res = bType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        ConstDef constDef = new ConstDef(treeNode);
        res = constDef.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            LexicalAnalysis.getInstance().skipErrorPart();
            return AnalysisResult.SUCCESS;
        }

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res == AnalysisResult.FAIL) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            ConstDef constDef1 = new ConstDef(treeNode);
            res = constDef1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                LexicalAnalysis.getInstance().skipErrorPart();
//                return AnalysisResult.FAIL;
                return AnalysisResult.SUCCESS;
            }
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res == AnalysisResult.FAIL) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("SEMICN")) {
            HandleError.handleError(AnalysisErrorType.LACK_OF_SEMICN);
            return AnalysisResult.SUCCESS;
        }
        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstDecl>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
