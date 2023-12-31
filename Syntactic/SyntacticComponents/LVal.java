package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SymbolTable.MasterTableItem;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class LVal extends SyntacticComponent {

    public static final int ASSIGN = 3242;
    public static final int REFERENCE = 4235;

    private int assignOrReference;

    public LVal(int assignOrReference, TreeNode parent) {
        super();
        this.assignOrReference = assignOrReference;
        this.treeNode = new TreeNode(TreeNodeName.LVal, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ParamResult<MasterTableItem> item = new ParamResult<>(null);
        int dimension = 0;
        String name;

        res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (!nextWordCategoryCode.getValue().equals("IDENFR")) {
            return AnalysisResult.FAIL;
        }
        name = nextWordValue.getValue();
        Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

        if (this.assignOrReference == ASSIGN) {
            res = masterTable.checkAssign(name);
        }
        else if (this.assignOrReference == REFERENCE) {
            res = masterTable.checkReference(name);
        }
        if (res == AnalysisResult.FAIL) {
            return AnalysisResult.FAIL;
        }

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

            Exp exp = new Exp(treeNode);
            res = exp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("RBRACK")) {
                HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                return AnalysisResult.FAIL;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());
        }

        ParamResult<ComponentValueType> componentValueTypeParamResult = new ParamResult<>(null);
        res = masterTable.getComponentValueType(name, componentValueTypeParamResult);
        if (res == AnalysisResult.FAIL) {
            return AnalysisResult.FAIL;
        }
        ComponentValueType componentValueType = componentValueTypeParamResult.getValue();

        if (dimension == 0) {
            this.valueType = componentValueType;
        }
        else if (dimension == 1) {
            if (componentValueType == ComponentValueType.ONE_DIMENSION_ARRAY) {
                this.valueType = ComponentValueType.INT;
            }
            else if (componentValueType == ComponentValueType.TWO_DIMENSION_ARRAY) {
                this.valueType = ComponentValueType.ONE_DIMENSION_ARRAY;
            }
        }
        else if (dimension == 2) {
            if (componentValueType == ComponentValueType.TWO_DIMENSION_ARRAY) {
                this.valueType = ComponentValueType.INT;
            }
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<LVal>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }
}
