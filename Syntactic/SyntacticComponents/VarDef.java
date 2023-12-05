package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.MasterTableItem;
import SymbolTable.SymbolConst;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class VarDef extends SyntacticComponent {

    public VarDef(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.VarDef, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ParamResult<MasterTableItem> item = new ParamResult<>(null);

        String name = "";
        int dimension = 0;

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        name += nextWordValue.getValue();

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("LBRACK")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            dimension ++;

            if (dimension == 3) {
                HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_BEYOND_TWO);
                return AnalysisResult.FAIL;
            }

            ConstExp constExp = new ConstExp(treeNode);
            res = constExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                res = masterTable.insertFailDeclareIdentifier(name);
                if (res == AnalysisResult.SUCCESS) {
                    HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                    InitVal initVal = new InitVal(treeNode);
                    res = initVal.analyze(whetherOutput);
                }
                return AnalysisResult.FAIL;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());
        }

        if (dimension == 0) {
            res = masterTable.insertIntoTable(name, SymbolConst.VAR, SymbolConst.INT, item);
        }
        else if (dimension == 1 || dimension == 2) {
            res = masterTable.insertIntoTable(name, SymbolConst.VAR, SymbolConst.ARRAY, dimension, item);
        }
        else {
            return AnalysisResult.FAIL;
        }
        if (res == AnalysisResult.FAIL) {
            return res;
        }

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("ASSIGN")) {
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            InitVal initVal = new InitVal(treeNode);
            res = initVal.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = masterTable.checkAssign(name);
            if (res == AnalysisResult.FAIL) {
                return AnalysisResult.FAIL;
            }

        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<VarDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
