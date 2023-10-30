package InterCode;

import SyntacticTree.IAfterOperate;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;
import SyntacticTree.TreeNodeName;

import java.util.ArrayList;

/**
 * 中间代码生成器主类，单例模式
 */
public class GenerateInterCode {

    ArrayList<SingleInterCode> interCodes;

    private static GenerateInterCode generateInterCode;

    private static final int VAR = 3234;
    private static final int CONST = 4235;

    private GenerateInterCode() {
        this.interCodes = new ArrayList<>();
    }

    static {
        GenerateInterCode.generateInterCode = new GenerateInterCode();
    }

    public static GenerateInterCode getInstance() {
        return generateInterCode;
    }

    private void addIntoInterCodes(Operation operation, QuadrupleVariable param1, QuadrupleVariable param2, QuadrupleVariable result) {
        SingleInterCode code = new SingleInterCode(operation, param1, param2, result);
        this.interCodes.add(code);
    }

    /**
     * 声明语句的中间代码生成
     */
    private void InterCodeOfDeclare(TreeNode node, int varOrConst) {
        ArrayList<TreeNode> children = node.getChildren();
        int length = children.size();
        QuadrupleVariable variable;

        if (length == 1 || children.get(1).getValue().equals("=")) {
            variable = new QuadrupleVariable(children.get(0).getValue());
            children.get(0).quadrupleVariable = variable;
            if (varOrConst == VAR) {
                addIntoInterCodes(Operation.VAR_INT_DECLARE, null, null, children.get(0).quadrupleVariable);
            }
            else if (varOrConst == CONST) {
                addIntoInterCodes(Operation.VAR_INT_DECLARE, null, null, children.get(0).quadrupleVariable);
            }
        }
        else {
            variable = new QuadrupleVariable(children.get(0).getValue());
            QuadrupleVariable arraySizeOne = children.get(2).quadrupleVariable;
            Operation operation = Operation.VAR_ARRAY_DECLARE;
            if (varOrConst == CONST) {
                operation = Operation.CONST_ARRAY_DECLARE;
            }
            if (children.get(4).getValue().equals("[")) {
                QuadrupleVariable arraySizeTwo = children.get(5).quadrupleVariable;
                addIntoInterCodes(operation, arraySizeOne, arraySizeTwo, variable);
            }
            else {
                addIntoInterCodes(operation, arraySizeOne, null, variable);
            }
        }

        if (children.get(length - 2).getValue().equals("=")) {
            addIntoInterCodes(Operation.INIT, variable, children.get(length - 1).quadrupleVariable, null);
        }
    }

    /**
     * 函数定义语句的中间代码生成
     */
    private void InterCodeOfFuncDefine() {

    }

    private void handleBefore(TreeNode node) {
        if (node.getName() == TreeNodeName.FuncDef) {

        }
    }

    private void handleAfter(TreeNode node) {
        if (node.getName() == TreeNodeName.VarDef) {
            InterCodeOfDeclare(node, VAR);
        }
        else if (node.getName() == TreeNodeName.ConstDef) {
            InterCodeOfDeclare(node, CONST);
        }
        else if (node.getName() == TreeNodeName.FuncDef) {

        }
    }

    public void run() {
        Tree.getInstance().traverse(this::handleBefore, this::handleAfter);
    }


}
