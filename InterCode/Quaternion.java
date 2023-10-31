package InterCode;

import SyntacticTree.ITraverseOperate;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;

import java.util.ArrayList;

public class Quaternion {

    private static Quaternion instance;

    private Quaternion() {
        this.quaternions = new ArrayList<>();
    }

    static {
        Quaternion.instance = new Quaternion();
    }

    public static Quaternion getInstance() {
        return instance;
    }

    private void addIntoInterCodes(Operation operation, QuaternionIdentify param1, QuaternionIdentify param2, QuaternionIdentify result) {
        SingleQuaternion code = new SingleQuaternion(operation, param1, param2, result);
        this.quaternions.add(code);
    }

    /**
     * 四元式中的变量的列表
     * 初衷如下
     * (1) int a;
     * (2) a = 12;
     * 目的就是为了让(1)中的a和(2)中的a所对应的QuaternionIdentify是同一个
     * 从而让她们拥有同一个寄存器
     */
    ArrayList<QuaternionIdentify> identifies;

    /**
     * 一条一条的四元式的集合
     */
    ArrayList<SingleQuaternion> quaternions;

    class TraverseOperate implements ITraverseOperate {

        @Override
        public void translateDeclare(TreeNode node, int varOrConst) {

            for (TreeNode node1: node.children) {
                node1.traverse(this);
            }

            ArrayList<TreeNode> children = node.children;
            int length = children.size();
            QuaternionIdentify variable;

            if (length == 1 || children.get(1).value.equals("=")) {
                variable = new QuaternionIdentify(children.get(0).value);
                children.get(0).quaternionIdentify = variable;
                if (varOrConst == 1) {
                    addIntoInterCodes(Operation.VAR_INT_DECLARE, null, null, children.get(0).quaternionIdentify);
                }
                else if (varOrConst == 2) {
                    addIntoInterCodes(Operation.CONST_INT_DECLARE, null, null, children.get(0).quaternionIdentify);
                }
            }
            else {
                variable = new QuaternionIdentify(children.get(0).value);
                QuaternionIdentify arraySizeOne = children.get(2).quaternionIdentify;
                Operation operation = Operation.VAR_ARRAY_DECLARE;
                if (varOrConst == 2) {
                    operation = Operation.CONST_ARRAY_DECLARE;
                }
                if (children.get(4).value.equals("[")) {
                    QuaternionIdentify arraySizeTwo = children.get(5).quaternionIdentify;
                    addIntoInterCodes(operation, arraySizeOne, arraySizeTwo, variable);
                }
                else {
                    addIntoInterCodes(operation, arraySizeOne, null, variable);
                }
            }

            identifies.add(variable);

            if (children.get(length - 2).value.equals("=")) {
                addIntoInterCodes(Operation.INIT, variable, children.get(length - 1).quaternionIdentify, null);
            }
        }

        @Override
        public void translateFuncDefine(TreeNode node) {

            int i = 0;
            int length = node.children.size();

            for (; i < 2; i++) {
                node.children.get(i).traverse(this);
            }

            QuaternionIdentify name = new QuaternionIdentify(node.children.get(1).value);
            QuaternionIdentify returnType = new QuaternionIdentify(node.children.get(0).value);
            node.children.get(1).quaternionIdentify = name;
            node.children.get(0).quaternionIdentify = returnType;
            addIntoInterCodes(Operation.FUNC_BEGIN, name, returnType, null);

            for (; i < length; i++) {
                node.children.get(i).traverse(this);
            }

            identifies.add(name);

            addIntoInterCodes(Operation.FUNC_END, null, null, null);
        }

        @Override
        public void translateFuncFParam(TreeNode node) {

            int i = 0;
            int length = node.children.size();

            for (; i < length; i++) {
                node.children.get(i).traverse(this);
            }

            if (length == 2) {

                QuaternionIdentify paramName = new QuaternionIdentify(node.children.get(1).value);
                node.children.get(1).quaternionIdentify = paramName;

                addIntoInterCodes(Operation.PARA_INT, paramName, null, null);
            }
            else if (length == 4) {

                QuaternionIdentify paramName = new QuaternionIdentify(node.children.get(1).value);
                node.children.get(1).quaternionIdentify = paramName;

                addIntoInterCodes(Operation.PARA_ARRAY, paramName, null, null);
            }
            else {

                QuaternionIdentify paramName = new QuaternionIdentify(node.children.get(1).value);
                node.children.get(1).quaternionIdentify = paramName;

                QuaternionIdentify secondSize = node.children.get(5).quaternionIdentify;

                addIntoInterCodes(Operation.PARA_ARRAY, paramName, secondSize, null);
            }
        }

        @Override
        public void translateBlock(TreeNode node) {

            addIntoInterCodes(Operation.BLOCK_BEGIN, null, null, null);

            for (TreeNode childNode: node.children) {
                childNode.traverse(this);
            }

            addIntoInterCodes(Operation.BLOCK_END, null, null, null);
        }

        private void translateReturn(TreeNode node) {

            int length = node.children.size();

            for (TreeNode childNode :
                node.children) {
                childNode.traverse(this);
            }

            if (length == 2) {
                addIntoInterCodes(Operation.RETURN, null, null, null);
            }
            else if (length == 3) {
                QuaternionIdentify returnValue = node.children.get(1).quaternionIdentify;

                addIntoInterCodes(Operation.RETURN, returnValue, null, null);
            }
        }

        private void translateGetint(TreeNode node) {

            QuaternionIdentify temp = new QuaternionIdentify("");
            addIntoInterCodes(Operation.GETINT, temp, null, null);
        }

        @Override
        public void translateStmt(TreeNode node) {

            int i = 0;
            int length = node.children.size();

            if (node.children.get(0).value.equals("return")) {
                translateReturn(node);
            }
            else if (length >= 4 && node.children.get(length - 4).value.equals("getint")) {
                translateGetint(node);
            }
        }

        @Override
        public void translateLVal(TreeNode node) {

            int length = node.children.size();

            QuaternionIdentify name = null;
            String s = node.children.get(0).value;

            int numOfIdentifies = identifies.size();
            for (int i = numOfIdentifies - 1; i >= 0; i--) {
                if (identifies.get(i).getValue().equals(s)) {
                    name = node.children.get(i).quaternionIdentify;
                    break;
                }
            }

            if (length == 1) {
                node.quaternionIdentify = name;
            }
        }

    }

    public void generateInterCode() {

        Tree.getInstance().traverse(new TraverseOperate());
    }

}
