package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import SymbolTable.SymbolConst;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

public class FuncType extends SyntacticComponent {

    private SymbolConst returnType;

    public FuncType(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.FuncType, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        if (nextWordCategoryCode.getValue().equals("VOIDTK")) {
            this.returnType = SymbolConst.VOID;
        }
        else if (nextWordCategoryCode.getValue().equals("INTTK")) {
            this.returnType = SymbolConst.INT;
        }
        else {
            return AnalysisResult.FAIL;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncType>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }

    public SymbolConst getReturnType() {
        return returnType;
    }
}
