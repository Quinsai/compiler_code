package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.MasterTableItem;
import SymbolTable.Scope.ScopeStack;
import SymbolTable.SymbolConst;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;
import Syntactic.SyntacticTree.TreeNodeName;

import java.net.http.HttpRequest;

public class FuncDef extends SyntacticComponent {

    public FuncDef(TreeNode parent) {
        super();
        this.treeNode = new TreeNode(TreeNodeName.FuncDef, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ParamResult<MasterTableItem> item = new ParamResult<>(null);
        SymbolConst returnType;
        String name;

        FuncType funcType = new FuncType(treeNode);
        res = funcType.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        returnType = funcType.getReturnType();

        if (returnType == SymbolConst.INT) {
            Stmt.functionReturnType = ComponentValueType.INT;
        }
        else if (returnType == SymbolConst.VOID) {
            Stmt.functionReturnType = ComponentValueType.VOID;
        }

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        name = nextWordValue.getValue();
        res = masterTable.insertIntoTable(name, SymbolConst.FUNCTION, SymbolConst.NO_MEANING, returnType, item);
        if (res == AnalysisResult.FAIL) {
            ScopeStack.getInstance().enterScope();
            Block block = new Block(treeNode);
            res = block.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            ScopeStack.getInstance().quitScope();
            return AnalysisResult.SUCCESS;
        }

        ScopeStack.getInstance().enterScope();

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("LPARENT")) {
            return AnalysisResult.FAIL;
        }
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("INTTK")) {
            FuncFParams funcFParams = new FuncFParams(treeNode);
            res = funcFParams.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                ScopeStack.getInstance().enterScope();
                Block block = new Block(treeNode);
                res = block.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                ScopeStack.getInstance().quitScope();
                return AnalysisResult.SUCCESS;
//                return AnalysisResult.FAIL;
            }
        }

        masterTable.setFunctionParams(name);

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("RPARENT")) {
            HandleError.handleError(AnalysisErrorType.LACK_OF_RPARENT);
            Block block = new Block(treeNode);
            res = block.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            ScopeStack.getInstance().quitScope();
            return AnalysisResult.SUCCESS;
//            return AnalysisResult.FAIL;
        }
        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        Block block = new Block(treeNode);
        res = block.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        if (!BlockItem.isReturnWithValue && Stmt.functionReturnType == ComponentValueType.INT) {
            HandleError.handleError(AnalysisErrorType.INT_FUNCTION_WITHOUT_RETURN);
            return AnalysisResult.SUCCESS;
        }

        Stmt.functionReturnType = ComponentValueType.NO_MEANING;

        ScopeStack.getInstance().quitScope();

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncDef>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
