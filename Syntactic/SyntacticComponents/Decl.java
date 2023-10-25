package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class Decl extends SyntacticComponent {

    public Decl(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.Decl, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        String categoryCode = nextWordCategoryCode.getValue();
        if (categoryCode.equals("CONSTTK")) {
            ConstDecl constDecl = new ConstDecl(treeNode);
            res = constDecl.analyze(whetherOutput);
        }
        else if (categoryCode.equals("INTTK")) {
            VarDecl varDecl = new VarDecl(treeNode);
            res = varDecl.analyze(whetherOutput);
        }
        else {
            res = AnalysisResult.FAIL;
        }
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        return AnalysisResult.SUCCESS;
    }
}
