package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

public class ConstInitVal extends SyntacticComponent {

    private ComponentValueType valueType;

    public ConstInitVal(TreeNode parent) {
        super();
        this.valueType = ComponentValueType.INT;
        this.treeNode = new TreeNode(TreeNodeName.ConstInitVal, "", parent);
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {

        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");
        ComponentValueType partValueType = ComponentValueType.INT;

        res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }
        if (nextWordCategoryCode.getValue().equals("LBRACE")) {

            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
            Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());

            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (nextWordCategoryCode.getValue().equals("RBRACE")) {
                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());
                this.valueType = ComponentValueType.ONE_DIMENSION_ARRAY;
            }
            else {
                ConstInitVal constInitVal = new ConstInitVal(treeNode);
                res = constInitVal.analyze(whetherOutput);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }

                if (constInitVal.getValueType() == ComponentValueType.INT) {
                    partValueType =ComponentValueType.INT;
                    this.valueType = ComponentValueType.ONE_DIMENSION_ARRAY;
                }
                else if (constInitVal.getValueType() == ComponentValueType.ONE_DIMENSION_ARRAY) {
                    partValueType = ComponentValueType.ONE_DIMENSION_ARRAY;
                    this.valueType = ComponentValueType.TWO_DIMENSION_ARRAY;
                }
                else if (constInitVal.getValueType() == ComponentValueType.TWO_DIMENSION_ARRAY) {
                    HandleError.handleError(AnalysisErrorType.ARRAY_DIMENSION_BEYOND_TWO);
                    return AnalysisResult.FAIL;
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

                    ConstInitVal constInitVal1 = new ConstInitVal(treeNode);
                    res = constInitVal1.analyze(whetherOutput);
                    if (res == AnalysisResult.FAIL) {
                        return AnalysisResult.FAIL;
                    }

                }

                res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);
                if (res != AnalysisResult.SUCCESS) {
                    return AnalysisResult.FAIL;
                }
                if (!nextWordCategoryCode.getValue().equals("RBRACE")) {
                    HandleError.handleError(AnalysisErrorType.LACK_OF_RBRACK);
                    return AnalysisResult.FAIL;
                }
                Tree.getInstance().addTerminalNodeIntoTree(this.treeNode, nextWordValue.getValue());
            }
        }
        else {
            ConstExp constExp = new ConstExp(treeNode);
            res = constExp.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.valueType = ComponentValueType.INT;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<ConstInitVal>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }

    public ComponentValueType getValueType() {
        return valueType;
    }

}
