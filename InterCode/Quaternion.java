package InterCode;

import Other.ParamResult;
import Output.OutputIntoFile;
import SymbolTable.MasterTable;
import SymbolTable.MasterTableItem;
import SymbolTable.SymbolTableResult;
import SyntacticTree.ITraverseOperate;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Quaternion {

    private static Quaternion instance;

    private Quaternion() {
        this.quaternions = new LinkedList<>();
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
     * 一条一条的四元式的集合
     */
    LinkedList<SingleQuaternion> quaternions;

    /**
     * 获取这个name对应的符号表条目对应的四元式的变量
     */
    private QuaternionIdentify getIdentifyOfSymbolName(String name) {
        SymbolTableResult res;
        ParamResult<MasterTableItem> identify = new ParamResult<>(null);

        res = MasterTable.getMasterTable().getItemByNameInAllTable(name, identify);
        if (res == SymbolTableResult.EXIST) {
            return identify.getValue().getQuaternionIdentify();
        }
        return null;
    }

    /**
     * 设置这个name对应的符号表条目对应的四元式的变量
     */
    private void setIdentifyToSymbolTableItem(String name, QuaternionIdentify quaternionIdentify) {
        SymbolTableResult res;
        ParamResult<MasterTableItem> identify = new ParamResult<>(null);

        res = MasterTable.getMasterTable().getItemByNameInAllTable(name, identify);
        if (res == SymbolTableResult.EXIST) {
            identify.getValue().setQuaternionIdentify(quaternionIdentify);
            quaternionIdentify.setSymbolTableItem(identify.getValue());
        }
    }

    private void setIdentifyToTreeNode(TreeNode node, QuaternionIdentify identify) {
        node.setQuaternionIdentify(identify);
    }

    class TraverseOperate implements ITraverseOperate {

        /**
         * for语句里面的开始标签的栈
         */
        static Stack<QuaternionIdentify> beginLabelStack;

        /**
         * for语句里面的结束标签的栈
         */
        static Stack<QuaternionIdentify> endLabelStack;

        static {
            beginLabelStack = new Stack<>();
            endLabelStack = new Stack<>();
        }

        private void traverseAllChildren(TreeNode node) {
            for (TreeNode node1: node.children) {
                node1.traverse(this);
            }
        }

        private void translateArrayInit(TreeNode node) {
            int i;
            int length = node.children.size();

            if (length == 0) {
                String value = node.value;
                QuaternionIdentify identify;
                // 如果是个标识符（这个标识符一定是已经出现过了的）
                if (!value.matches("^-?\\d+$")) {
                    identify = getIdentifyOfSymbolName(value);
                }
                // 如果不是标识符，那就应该仅仅是一个数字罢了
                else {
                    identify = new QuaternionIdentify(value);
                }
                setIdentifyToTreeNode(node, identify);
                return;
            }

            if (!node.children.get(0).value.equals("{")) {
                translateAllExp(node);
                return;
            }

            i = 1;
            QuaternionIdentify identify = new QuaternionIdentify("");
            while (i < length) {
                translateArrayInit(node.children.get(i));
                QuaternionIdentify temp = node.children.get(i).getQuaternionIdentify();
                identify.arrayValue.add(temp);
                i += 2;
            }
            setIdentifyToTreeNode(node, identify);
        }

        @Override
        public void translateDeclare(TreeNode node, int varOrConst) {

            // traverseAllChildren(node);

            ArrayList<TreeNode> children = node.children;
            int length = children.size();
            QuaternionIdentify identify;
            String name;
            int dimension;

            // 合并删除单传节点后就不再有子节点，说明一定是int a;这样的情况
            if (length == 0) {
                name = node.value;
                identify = new QuaternionIdentify(name);
                setIdentifyToTreeNode(node, identify);
                setIdentifyToSymbolTableItem(name, identify);
                addIntoInterCodes(Operation.VAR_INT_DECLARE, null, null, identify);
                return;
            }

            name = children.get(0).value;
            identify = new QuaternionIdentify(name);
            setIdentifyToTreeNode(children.get(0), identify);
            setIdentifyToSymbolTableItem(name, identify);

            // const int a = 1;
            if (children.get(1).value.equals("=")) {
                dimension = 0;
                if (varOrConst == 1) {
                    addIntoInterCodes(Operation.VAR_INT_DECLARE, null, null, children.get(0).getQuaternionIdentify());
                }
                else if (varOrConst == 2) {
                    addIntoInterCodes(Operation.CONST_INT_DECLARE, null, null, children.get(0).getQuaternionIdentify());
                }
            }
            // 数组
            else {
                QuaternionIdentify arraySizeOne = children.get(2).getQuaternionIdentify();
                Operation operation = Operation.VAR_ARRAY_DECLARE;
                if (varOrConst == 2) {
                    operation = Operation.CONST_ARRAY_DECLARE;
                }
                // 二维数组
                if (children.get(4).value.equals("[")) {
                    dimension = 2;
                    QuaternionIdentify arraySizeTwo = children.get(5).getQuaternionIdentify();
                    identify.getSymbolTableItem().setArraySize(arraySizeOne, arraySizeTwo);
                    addIntoInterCodes(operation, arraySizeOne, arraySizeTwo, identify);
                }
                // 一维数组
                else {
                    dimension = 1;
                    identify.getSymbolTableItem().setArraySize(arraySizeOne, null);
                    addIntoInterCodes(operation, arraySizeOne, null, identify);
                }
            }

            // 初始化赋值
            if (children.get(length - 2).value.equals("=")) {
                // 给int初始化一个int值
                if (dimension == 0) {
                    translateAllExp(node.children.get(length - 1));
                    QuaternionIdentify intInitialValue = node.children.get(length - 1).getQuaternionIdentify();
                    addIntoInterCodes(Operation.SET_VALUE, identify, intInitialValue, null);
                }
                // 给数组初始化一个数组
                else {
                    translateArrayInit(node.children.get(length - 1));
                    QuaternionIdentify arrayInitialValue = node.children.get(length - 1).getQuaternionIdentify();
                    addIntoInterCodes(Operation.ARRAY_INIT, identify, arrayInitialValue, null);
                }
            }
        }

        @Override
        public void translateFuncDefine(TreeNode node) {

            int i = 0;
            int length = node.children.size();

            for (; i < 2; i++) {
                node.children.get(i).traverse(this);
            }

            String name = node.children.get(1).value;
            QuaternionIdentify func = new QuaternionIdentify(name);
            setIdentifyToTreeNode(node.children.get(1), func);
            setIdentifyToSymbolTableItem(name, func);
            QuaternionIdentify returnType = new QuaternionIdentify(node.children.get(0).value);
            setIdentifyToTreeNode(node.children.get(1), func);
            setIdentifyToTreeNode(node.children.get(0), returnType);
            addIntoInterCodes(Operation.FUNC_BEGIN, func, returnType, null);

            for (; i < length; i++) {
                node.children.get(i).traverse(this);
            }

            addIntoInterCodes(Operation.FUNC_END, null, null, null);
        }

        @Override
        public void translateFuncFParam(TreeNode node) {

            int length = node.children.size();

            traverseAllChildren(node);

            QuaternionIdentify paramName;

            if (length == 2) {

                paramName = new QuaternionIdentify(node.children.get(1).value);

                addIntoInterCodes(Operation.FORMAL_PARA_INT, paramName, null, null);
            }
            else if (length == 4) {

                paramName = new QuaternionIdentify(node.children.get(1).value);

                addIntoInterCodes(Operation.FORMAL_PARA_ARRAY, paramName, null, null);
            }
            else {

                paramName = new QuaternionIdentify(node.children.get(1).value);

                QuaternionIdentify secondSize = node.children.get(5).getQuaternionIdentify();

                addIntoInterCodes(Operation.FORMAL_PARA_ARRAY, paramName, secondSize, null);
            }

            setIdentifyToTreeNode(node.children.get(1), paramName);
        }

        @Override
        public void translateBlock(TreeNode node) {

            addIntoInterCodes(Operation.BLOCK_BEGIN, null, null, null);

            traverseAllChildren(node);

            addIntoInterCodes(Operation.BLOCK_END, null, null, null);
        }

        private void translateReturn(TreeNode node) {

            int length = node.children.size();

            traverseAllChildren(node);

            if (length == 2) {
                addIntoInterCodes(Operation.RETURN, null, null, null);
            }
            else if (length == 3) {
                QuaternionIdentify returnValue = node.children.get(1).getQuaternionIdentify();

                addIntoInterCodes(Operation.RETURN, returnValue, null, null);
            }
        }

        private void translateGetint(TreeNode node) {
            // TODO
            QuaternionIdentify temp = new QuaternionIdentify("");
            addIntoInterCodes(Operation.GETINT, temp, null, null);
        }

        private void translateBreak(TreeNode node) {
            QuaternionIdentify endLabel = endLabelStack.pop();
            addIntoInterCodes(Operation.SKIP, endLabel, null, null);
            /*
            这一整个循环结束了
            不仅是结束标签要出栈，开始标签也要出栈
             */
            beginLabelStack.pop();
        }

        private void translateContinue(TreeNode node) {
            QuaternionIdentify beginLabel = beginLabelStack.peek();
            addIntoInterCodes(Operation.SKIP, beginLabel, null, null);
        }

        private void translatePrintf(TreeNode node) {

            int i;
            /*
              用以表示开头的是字符串还是%d
              1是字符串
              2是%d
             */
            int delta = 1;

            String temp = node.children.get(2).value;
            int tempLength = temp.length();
            String format = temp.substring(1, tempLength - 1);
            String[] subStrings = format.split("%d");

            i = 4;

            if (format.length() >= 2 && format.charAt(0) == '%' && format.charAt(1) == 'd') {
                delta = 2;
            }

            if (delta == 2) {
                node.children.get(i).traverse(this);
                addIntoInterCodes(Operation.PRINT_INT, node.children.get(i).getQuaternionIdentify(), null, null);
                i += 2;
            }
            int numberOfSubString = subStrings.length;
            int j = 0;
            if (subStrings[0].isEmpty()) {
                j ++;
            }
            for (; j < numberOfSubString; j++) {
                addIntoInterCodes(Operation.PRINT_STRING, new QuaternionIdentify(subStrings[j]), null, null);
                if (!node.children.get(i).value.equals(";")) {
                    node.children.get(i).traverse(this);
                    addIntoInterCodes(Operation.PRINT_INT, node.children.get(i).getQuaternionIdentify(), null, null);
                    i += 2;
                }
            }
        }

        private void translateFor(TreeNode node) {
            int i;
            int length = node.children.size();
            QuaternionIdentify beginLabel = new QuaternionIdentify("");
            QuaternionIdentify endLabel = new QuaternionIdentify("");
            QuaternionIdentify condition;

            /*
            要在这里把开始标签和结束标签加入栈中
            这样好让每一个break和continue都知道它们要去哪一个结束标签/开始标签
            但是这个标签具体在哪
            不知道
             */
            beginLabelStack.push(beginLabel);
            endLabelStack.push(endLabel);

            for (i = 0; i < 2; i++) {
                node.children.get(i).traverse(this);
            }

            // 第一个ForStmt不为空
            if (!node.children.get(2).value.equals(";")) {
                node.children.get(2).traverse(this);
                i += 2;
            }
            else {
                i++;
            }

            addIntoInterCodes(Operation.LABEL, beginLabel, null, null);

            if (!node.children.get(i).value.equals(";")) {
                node.children.get(i).traverse(this);
                condition = node.children.get(i).getQuaternionIdentify();
                addIntoInterCodes(Operation.BRANCH_IF_FALSE, condition, endLabel, null);
                i += 2;
            }
            else {
                i++;
            }

            node.children.get(length - 2).traverse(this);

            if (!node.children.get(i).value.equals(")")) {
                node.children.get(i).traverse(this);
            }

            addIntoInterCodes(Operation.SKIP, beginLabel, null, null);

            addIntoInterCodes(Operation.LABEL, endLabel, null, null);

            /*
             *
             */
            beginLabelStack.pop();
            endLabelStack.pop();
        }

        private void translateIf(TreeNode node) {
            int i;
            int length = node.children.size();

            for (i = 0; i < 4; i ++) {
                node.children.get(i).traverse(this);
            }

            QuaternionIdentify endLabel = new QuaternionIdentify("");
            QuaternionIdentify condition = node.children.get(2).getQuaternionIdentify();

            // 有else
            if (length == 7) {

                QuaternionIdentify elseLabel = new QuaternionIdentify("");

                addIntoInterCodes(Operation.BRANCH_IF_FALSE, condition, elseLabel, null);

                for (i = 4; i < 5; i++) {
                    node.children.get(i).traverse(this);
                }

                addIntoInterCodes(Operation.SKIP, endLabel, null, null);
                addIntoInterCodes(Operation.LABEL, elseLabel, null, null);

                for (i = 5; i < 7; i++) {
                    node.children.get(i).traverse(this);
                }
            }
            else if (length == 5) {
                addIntoInterCodes(Operation.BRANCH_IF_FALSE, condition, endLabel, null);

                for (i = 4; i < 5; i++) {
                    node.children.get(i).traverse(this);
                }
            }

            addIntoInterCodes(Operation.LABEL, endLabel, null, null);
        }

        @Override
        public void translateStmt(TreeNode node) {

            int i = 0;
            int length = node.children.size();

            // 'return' [Exp] ';'
            if (node.children.get(0).value.equals("return")) {
                translateReturn(node);
            }
            // LVal '=' 'getint''('')'';'
            else if (length >= 4 && node.children.get(length - 4).value.equals("getint")) {
                translateGetint(node);
            }
            // LVal '=' Exp ';'
            else if (length == 4) {

                traverseAllChildren(node);

                QuaternionIdentify left = node.children.get(0).getQuaternionIdentify();
                QuaternionIdentify right = node.children.get(2).getQuaternionIdentify();
                addIntoInterCodes(Operation.SET_VALUE, left, right, null);
            }
            // 'break' ';'
            else if (length == 2 && node.children.get(0).value.equals("break")) {
                translateBreak(node);
            }
            // 'continue' ';'
            else if (length == 2 && node.children.get(0).value.equals("continue")) {
                translateContinue(node);
            }
            // 'printf''('FormatString{','Exp}')'';'
            else if (node.children.get(0).value.equals("printf")) {
                translatePrintf(node);
            }
            // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            else if (node.children.get(0).value.equals("if")) {
                translateIf(node);
            }
            // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            else if (node.children.get(0).value.equals("for")) {
                translateFor(node);
            }
        }

        @Override
        public void translateLVal(TreeNode node) {

            int length = node.children.size();
            String nameString;
            QuaternionIdentify identify;

            traverseAllChildren(node);

            /*
             * 如果LVal → Ident，那么由于我完成了删除单传节点的操作，
             * 此时的LVAL对应的节点的value应该直接等于标识符的名字
             */
            // a
            if (length == 0) {
                nameString = node.value;
                identify = getIdentifyOfSymbolName(nameString);
                setIdentifyToTreeNode(node, identify);
                return;
            }

            // a[1]
            if (length == 4) {
                QuaternionIdentify name = getIdentifyOfSymbolName(node.children.get(0).value);
                QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                QuaternionIdentify address = new QuaternionIdentify("");
                identify = new QuaternionIdentify("");
                addIntoInterCodes(Operation.ADDRESS, name, offset1, address);
                addIntoInterCodes(Operation.GET_VALUE, address, null, identify);
                setIdentifyToTreeNode(node, identify);
            }
            else if (length == 7) {
                QuaternionIdentify name = getIdentifyOfSymbolName(node.children.get(0).value);
                QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                QuaternionIdentify offset2 = node.children.get(5).getQuaternionIdentify();
                identify = new QuaternionIdentify("");
                QuaternionIdentify temp = new QuaternionIdentify("");
                QuaternionIdentify temp1 = new QuaternionIdentify("");

                ParamResult<QuaternionIdentify> size1;
                ParamResult<QuaternionIdentify> size2;
                size1 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                size2 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                name.getSymbolTableItem().getArraySize(size1, size2);

                /*
                 * int a[2][2];
                 * a第一维度为3， 第二维度为4
                 * 则
                 * 2 * 3 = 6;
                 * 6 + 4 = 10;
                 * a + 10
                 */
                addIntoInterCodes(Operation.MULT, size1.getValue(), offset1, temp);
                addIntoInterCodes(Operation.PLUS, temp, offset2, temp1);
                addIntoInterCodes(Operation.ADDRESS, name, temp1, identify);
                setIdentifyToTreeNode(node, identify);
            }
        }

        @Override
        public void translateForStmt(TreeNode node) {
            traverseAllChildren(node);

            QuaternionIdentify left = node.children.get(0).getQuaternionIdentify();
            QuaternionIdentify right = node.children.get(2).getQuaternionIdentify();
            addIntoInterCodes(Operation.SET_VALUE, left, right, null);
        }

        private void translateAddExp(TreeNode node) {
            int length = node.children.size();
            int i = 0;
            QuaternionIdentify res;

            res = new QuaternionIdentify("");

            node.children.get(0).traverse(this);
            node.children.get(2).traverse(this);
            QuaternionIdentify init1 = node.children.get(0).getQuaternionIdentify();
            QuaternionIdentify init2 = node.children.get(2).getQuaternionIdentify();
            switch (node.children.get(1).value) {
                case "+" -> addIntoInterCodes(Operation.PLUS, init1, init2, res);
                case "-" -> addIntoInterCodes(Operation.MINU, init1, init2, res);
            }

            for(i = 3; i < length; i += 2) {
                node.children.get(i+1).traverse(this);
                QuaternionIdentify temp = node.children.get(i+1).getQuaternionIdentify();
                switch (node.children.get(i).value) {
                    case "+" -> addIntoInterCodes(Operation.PLUS, res, temp, res);
                    case "-" -> addIntoInterCodes(Operation.MINU, res, temp, res);
                }
            }

            setIdentifyToTreeNode(node, res);
        }

        private void translateMulExp(TreeNode node) {
            int length = node.children.size();
            int i = 0;
            QuaternionIdentify res;

            res = new QuaternionIdentify("");

            node.children.get(0).traverse(this);
            node.children.get(2).traverse(this);
            QuaternionIdentify init1 = node.children.get(0).getQuaternionIdentify();
            QuaternionIdentify init2 = node.children.get(2).getQuaternionIdentify();
            switch (node.children.get(1).value) {
                case "*" -> addIntoInterCodes(Operation.MULT, init1, init2, res);
                case "/" -> addIntoInterCodes(Operation.DIV, init1, init2, res);
                case "%" -> addIntoInterCodes(Operation.MOD, init1, init2, res);
            }

            for(i = 3; i < length; i += 2) {
                node.children.get(i+1).traverse(this);
                QuaternionIdentify temp = node.children.get(i+1).getQuaternionIdentify();
                switch (node.children.get(i).value) {
                    case "*" -> addIntoInterCodes(Operation.MULT, res, temp, res);
                    case "/" -> addIntoInterCodes(Operation.DIV, res, temp, res);
                    case "%" -> addIntoInterCodes(Operation.MOD, res, temp, res);
                }
            }

            setIdentifyToTreeNode(node, res);
        }

        private void translateUnaryOp(TreeNode node) {

            traverseAllChildren(node);

            node.children.get(1).traverse(this);
            QuaternionIdentify identify = node.children.get(1).getQuaternionIdentify();
            QuaternionIdentify res = new QuaternionIdentify("");


            if (node.children.get(0).value.equals("-")) {
                addIntoInterCodes(Operation.OPPO, identify, null, res);
            }
            else if (node.children.get(0).value.equals("!")) {
                addIntoInterCodes(Operation.NOT, identify, null, res);
            }
            else {
                res = identify;
            }

            setIdentifyToTreeNode(node, res);
        }

        private void translateFunctionRealPara(TreeNode node) {

            int length = node.children.size();
            QuaternionIdentify identify;

            if (length == 0) {
                String value = node.value;
                // 如果是个标识符（这个标识符一定是已经出现过了的）
                if (!value.matches("^-?\\d+$")) {
                    identify = getIdentifyOfSymbolName(value);
                }
                // 如果不是标识符，那就应该仅仅是一个数字罢了
                else {
                    identify = new QuaternionIdentify(value);
                }
                setIdentifyToTreeNode(node, identify);
                addIntoInterCodes(Operation.REAL_PARA, identify, null, null);
                return;
            }

            translateAllExp(node.children.get(0));
            identify = node.children.get(0).getQuaternionIdentify();
            addIntoInterCodes(Operation.REAL_PARA, identify, null, null);

            for (int i = 1; i <length; i += 2) {
                translateAllExp(node.children.get(i+1));
                identify = node.children.get(i+1).getQuaternionIdentify();
                addIntoInterCodes(Operation.REAL_PARA, identify, null, null);
            }
        }

        private void translateFunctionCall(TreeNode node) {

            int length = node.children.size();
            QuaternionIdentify funcName = getIdentifyOfSymbolName(node.children.get(0).value);
            QuaternionIdentify res = new QuaternionIdentify("");

            addIntoInterCodes(Operation.FUNC_CALL_BEGIN, funcName, null, res);

            if (length == 4) {
                translateFunctionRealPara(node.children.get(2));
            }

            addIntoInterCodes(Operation.FUNC_CALL_END, null, null, null);
            setIdentifyToTreeNode(node, res);
        }

        private void translateRelExp(TreeNode node) {
            int length = node.children.size();
            int i = 0;
            QuaternionIdentify res;

            res = new QuaternionIdentify("");

            node.children.get(0).traverse(this);
            node.children.get(2).traverse(this);
            QuaternionIdentify init1 = node.children.get(0).getQuaternionIdentify();
            QuaternionIdentify init2 = node.children.get(2).getQuaternionIdentify();
            switch (node.children.get(1).value) {
                case "<" -> addIntoInterCodes(Operation.LITTLE, init1, init2, res);
                case "<=" -> addIntoInterCodes(Operation.LITTLE_EQUAL, init1, init2, res);
                case ">" -> addIntoInterCodes(Operation.GREAT, init1, init2, res);
                case ">=" -> addIntoInterCodes(Operation.GREAT_EQUAL, init1, init2, res);
            }

            for(i = 3; i < length; i += 2) {
                node.children.get(i+1).traverse(this);
                QuaternionIdentify temp = node.children.get(i+1).getQuaternionIdentify();
                switch (node.children.get(i).value) {
                    case "<" -> addIntoInterCodes(Operation.LITTLE, res, temp, res);
                    case "<=" -> addIntoInterCodes(Operation.LITTLE_EQUAL, res, temp, res);
                    case ">" -> addIntoInterCodes(Operation.GREAT, res, temp, res);
                    case ">=" -> addIntoInterCodes(Operation.GREAT_EQUAL, res, temp, res);
                }
            }

            setIdentifyToTreeNode(node, res);
        }

        private void translateEqExp(TreeNode node) {
            int length = node.children.size();
            int i = 0;
            QuaternionIdentify res;

            res = new QuaternionIdentify("");

            node.children.get(0).traverse(this);
            node.children.get(2).traverse(this);
            QuaternionIdentify init1 = node.children.get(0).getQuaternionIdentify();
            QuaternionIdentify init2 = node.children.get(2).getQuaternionIdentify();
            switch (node.children.get(1).value) {
                case "==" -> addIntoInterCodes(Operation.EQUAL, init1, init2, res);
                case "!=" -> addIntoInterCodes(Operation.NOT_EQUAL, init1, init2, res);
            }

            for(i = 3; i < length; i += 2) {
                node.children.get(i+1).traverse(this);
                QuaternionIdentify temp = node.children.get(i+1).getQuaternionIdentify();
                switch (node.children.get(i).value) {
                    case "==" -> addIntoInterCodes(Operation.EQUAL, res, temp, res);
                    case "!=" -> addIntoInterCodes(Operation.NOT_EQUAL, res, temp, res);
                }
            }

            setIdentifyToTreeNode(node, res);
        }

        private void translateLAndExp(TreeNode node) {
            int length = node.children.size();
            int i = 0;
            QuaternionIdentify res;

            res = new QuaternionIdentify("");

            node.children.get(0).traverse(this);
            node.children.get(2).traverse(this);
            QuaternionIdentify init1 = node.children.get(0).getQuaternionIdentify();
            QuaternionIdentify init2 = node.children.get(2).getQuaternionIdentify();
            addIntoInterCodes(Operation.AND, init1, init2, res);

            for(i = 3; i < length; i += 2) {
                node.children.get(i+1).traverse(this);
                QuaternionIdentify temp = node.children.get(i+1).getQuaternionIdentify();
                addIntoInterCodes(Operation.AND, res, temp, res);
            }

            setIdentifyToTreeNode(node, res);
        }

        private void translateLOrExp(TreeNode node) {
            int length = node.children.size();
            int i = 0;
            QuaternionIdentify res;

            res = new QuaternionIdentify("");

            node.children.get(0).traverse(this);
            node.children.get(2).traverse(this);
            QuaternionIdentify init1 = node.children.get(0).getQuaternionIdentify();
            QuaternionIdentify init2 = node.children.get(2).getQuaternionIdentify();
            addIntoInterCodes(Operation.OR, init1, init2, res);

            for(i = 3; i < length; i += 2) {
                node.children.get(i+1).traverse(this);
                QuaternionIdentify temp = node.children.get(i+1).getQuaternionIdentify();
                addIntoInterCodes(Operation.OR, res, temp, res);
            }

            setIdentifyToTreeNode(node, res);
        }

        @Override
        public void translateAllExp(TreeNode node) {
            int length = node.children.size();
            int i;

            if (length == 0) {
                String value = node.value;
                QuaternionIdentify identify;
                // 如果是个标识符（这个标识符一定是已经出现过了的）
                if (!value.matches("^-?\\d+$")) {
                    identify = getIdentifyOfSymbolName(value);
                }
                // 如果不是标识符，那就应该仅仅是一个数字罢了
                else {
                    identify = new QuaternionIdentify(value);
                }
                setIdentifyToTreeNode(node, identify);
                return;
            }

            // AddExp → MulExp { ('+' | '−') MulExp }
            if (length >= 3 && (
                node.children.get(1).value.equals("+") ||
                node.children.get(1).value.equals("-")
            )) {
                translateAddExp(node);
            }
            // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
            else if (length >= 3 && (
                node.children.get(1).value.equals("*") ||
                node.children.get(1).value.equals("/") ||
                node.children.get(1).value.equals("%")
            )) {
                translateMulExp(node);
            }
            // UnaryExp → UnaryOp UnaryExp
            else if (length == 2 && (
                node.children.get(0).value.equals("+") ||
                node.children.get(0).value.equals("-") ||
                node.children.get(0).value.equals("!")
            )) {
                translateUnaryOp(node);
            }
            // UnaryExp → Ident '(' [FuncRParams] ')'
            else if (length >= 3 && (
                node.children.get(1).value.equals("(") &&
                node.children.get(length-1).value.equals(")")
            )) {
                translateFunctionCall(node);
            }
            // PrimaryExp → '(' Exp ')'
            else if (node.children.get(0).value.equals("(")) {
                node.children.get(1).traverse(this);
                QuaternionIdentify identify = node.children.get(1).getQuaternionIdentify();
                setIdentifyToTreeNode(node, identify);
            }
            // LVal → Ident '[' Exp ']'
            else if (node.children.get(1).value.equals("[")) {
                translateLVal(node);
            }
            // RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
            else if (length >= 3 && (
                node.children.get(1).value.equals("<") ||
                node.children.get(1).value.equals(">") ||
                node.children.get(1).value.equals(">=") ||
                node.children.get(1).value.equals("<=")
            )) {
                translateRelExp(node);
            }
            // EqExp → RelExp { ('==' | '!=') RelExp }
            else if (length >= 3 && (
                node.children.get(1).value.equals("==") ||
                node.children.get(1).value.equals("!=")
            )) {
                translateEqExp(node);
            }
            // LAndExp → EqExp { '&&' EqExp }
            else if (length >= 3 && (
                node.children.get(1).value.equals("&&")
            )) {
                translateLAndExp(node);
            }
            // LOrExp → LAndExp { '||' LAndExp }
            else if (length >= 3 && (
                node.children.get(1).value.equals("||")
            )) {
                translateLOrExp(node);
            }
        }

        @Override
        public void translateMainFunc(TreeNode node) {
            addIntoInterCodes(Operation.MAIN_FUNC_BEGIN, null, null, null);

            node.children.get(4).traverse(this);

            addIntoInterCodes(Operation.MAIN_FUNC_END, null, null, null);
        }

        @Override
        public void translateBlockItem(TreeNode node) {
            int length = node.children.size();

            if (length >= 1 && node.children.get(0).value.equals("const")) {
                int i = 2;
                while (i < length) {
                    translateDeclare(node.children.get(i), 2);
                    i += 2;
                }

            }
            else if (length >= 1 && node.children.get(0).value.equals("int")) {
                int i = 1;
                while (i < length) {
                    translateDeclare(node.children.get(i), 1);
                    i += 2;
                }
            }
            else {
                translateStmt(node);
            }
        }

    }

    public void generateInterCode() {

        Tree.getInstance().traverse(new TraverseOperate());
    }

    private String displaySingle(QuaternionIdentify identify) {

        String output = "";

        if (identify == null) {
            output += "null";
        }
        else {
            if (identify.getValue().isEmpty()) {
                if (!identify.arrayValue.isEmpty()) {
                    output += "[";
                    int length = identify.arrayValue.size();
                    for (int i = 0; i < length; i++) {
                        QuaternionIdentify child = identify.arrayValue.get(i);
                        output += displaySingle(child);
                        if (i != length - 1) {
                            output += ",";
                        }
                    }
                    output += ']';
                }
                else {
                    output += identify.id;
                }
            }
            else {
                output += identify.getValue();
            }
        }

        return output;
    }

    public void display() {
        for (SingleQuaternion single :
            this.quaternions) {
            String output = "";
            output += single.operation.name();

            output += " ";

            output += displaySingle(single.param1);

            output += " ";

            output += displaySingle(single.param2);

            output += " ";

            output += displaySingle(single.result);

            output += "\n";

            OutputIntoFile.appendToFile(output, "intercode.txt");
        }
    }
}
