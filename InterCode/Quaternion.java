package InterCode;

import Other.ParamResult;
import Output.OutputIntoFile;
import SymbolTable.MasterTable;
import SymbolTable.MasterTableItem;
import SymbolTable.Scope.ScopeStack;
import SymbolTable.SymbolConst;
import SymbolTable.SymbolTableResult;
import SyntacticTree.ITraverseOperate;
import SyntacticTree.Tree;
import SyntacticTree.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
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
    private LinkedList<SingleQuaternion> quaternions;

    private QuaternionIdentify currentFunc;

    public LinkedList<SingleQuaternion> getQuaternions() {
        return quaternions;
    }

    /*
    这里存在一个问题，就是我在link符号表条目和四元式变量的时候，
    我可以直接使用这个树节点所在的作用域来判断重名
    但是在由符号名获取对印的四元式变量的时候不行
    因为这个符号极有可能是在更外层定义的
    一个可行的解决方法是：
    记录下每一个作用域的 **能访问的外层**
    然后一次遍历这些个外层......
     */
    /**
     * 获取这个name对应的符号表条目对应的四元式的变量
     */
    private QuaternionIdentify getIdentifyOfSymbolName(String name, int scope) {
        SymbolTableResult res;
        ParamResult<MasterTableItem> identify = new ParamResult<>(null);

        ArrayList<Integer> allVisibleScope = ScopeStack.getInstance().getAllVisible(scope);
        for (Integer outer :
            allVisibleScope) {
            int visible = outer;
            res = MasterTable.getMasterTable().getItemByNameInAllTable(name, visible, identify);
            if (res == SymbolTableResult.EXIST) {
                /*
                我tm知道了
                我确实能够完美地找到对应的符号表中的条目
                那我为什么找不到其对应的四元式变量呢
                因为我是傻逼
                我tm没给函数形参分配四元式变量
                wocsb
                 */
                return identify.getValue().getQuaternionIdentify();
            }
        }

        return null;
    }

    /**
     * 设置这个name对应的符号表条目对应的四元式的变量
     */
    private void linkIdentifyWithSymbolTableItem(String name, QuaternionIdentify quaternionIdentify, int scope) {
        SymbolTableResult res;
        ParamResult<MasterTableItem> identify = new ParamResult<>(null);

        res = MasterTable.getMasterTable().getItemByNameInAllTable(name, scope, identify);
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

        /**
         * for语句中的跟新表达式的开始标签的栈
         */
        static Stack<QuaternionIdentify> updateLabelStack;

        static {
            beginLabelStack = new Stack<>();
            endLabelStack = new Stack<>();
            updateLabelStack = new Stack<>();
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
                    identify = getIdentifyOfSymbolName(value, node.getScope());
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
        public void translateDeclare(TreeNode node, int varOrConst, boolean isGlobal) {

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
                linkIdentifyWithSymbolTableItem(name, identify, node.getScope());
                addIntoInterCodes(Operation.VAR_INT_DECLARE, null, null, identify);
                return;
            }

            name = children.get(0).value;
            identify = new QuaternionIdentify(name);
            if (isGlobal) {
                identify.setType(QuaternionIdentifyType.GLOBAL);
            }
            setIdentifyToTreeNode(children.get(0), identify);
            linkIdentifyWithSymbolTableItem(name, identify, node.getScope());

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
                translateConst(children.get(2));
                QuaternionIdentify arraySizeOne = children.get(2).getQuaternionIdentify();
                Operation operation = Operation.VAR_ARRAY_DECLARE;
                if (varOrConst == 2) {
                    operation = Operation.CONST_ARRAY_DECLARE;
                }
                // 二维数组
                if (length > 4 && children.get(4).value.equals("[")) {
                    dimension = 2;
                    translateConst(children.get(5));
                    QuaternionIdentify arraySizeTwo = children.get(5).getQuaternionIdentify();
                    identify.getSymbolTableItem().setArraySize(arraySizeOne, arraySizeTwo);
                    identify.isArrayIdentify = true;
                    addIntoInterCodes(operation, arraySizeOne, arraySizeTwo, identify);
                }
                // 一维数组
                else {
                    dimension = 1;
                    identify.getSymbolTableItem().setArraySize(arraySizeOne, null);
                    identify.isArrayIdentify = true;
                    addIntoInterCodes(operation, arraySizeOne, null, identify);
                }
            }

            // 初始化赋值
            if (children.get(length - 2).value.equals("=")) {
                // 给int初始化一个int值
                if (dimension == 0) {
                    switch (node.children.get(length - 1).name) {
                        case ConstInitVal -> translateConst(node.children.get(length - 1));
                        case InitVal -> translateAllExp(node.children.get(length - 1));
                    }
                    QuaternionIdentify intInitialValue = node.children.get(length - 1).getQuaternionIdentify();
                    addIntoInterCodes(Operation.SET_VALUE, identify, intInitialValue, null);
                }
                // 给数组初始化一个数组
                else {
                    switch (node.children.get(length - 1).name) {
                        case ConstInitVal -> translateConstArrayInit(node.children.get(length - 1));
                        case InitVal -> translateArrayInit(node.children.get(length - 1));
                    }
                    QuaternionIdentify arrayInitialValue = node.children.get(length - 1).getQuaternionIdentify();
                    addIntoInterCodes(Operation.ARRAY_INIT, identify, arrayInitialValue, null);
                }
            }
        }

        private void translateConstArrayInit(TreeNode node) {
            QuaternionIdentify identify = new QuaternionIdentify("");
            // 一维
            if (!node.constOneDArrayValue.isEmpty()) {
                for (int value :
                    node.constOneDArrayValue) {
                    identify.arrayValue.add(new QuaternionIdentify(String.valueOf(value)));
                }
            }
            // 二维
            else {
                for (ArrayList<Integer> dimensionOne :
                    node.constTwoDArrayValue) {
                    QuaternionIdentify temp = new QuaternionIdentify("");
                    for (int value :
                        dimensionOne) {
                        temp.arrayValue.add(new QuaternionIdentify(String.valueOf(value)));
                    }
                    identify.arrayValue.add(temp);
                }
            }
            setIdentifyToTreeNode(node,identify);
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
            currentFunc = func;
            setIdentifyToTreeNode(node.children.get(1), func);
            linkIdentifyWithSymbolTableItem(name, func, node.getScope());
            QuaternionIdentify returnType = new QuaternionIdentify(node.children.get(0).value);
            setIdentifyToTreeNode(node.children.get(1), func);
            setIdentifyToTreeNode(node.children.get(0), returnType);
            addIntoInterCodes(Operation.FUNC_BEGIN, func, returnType, null);

            for (; i < length; i++) {
                node.children.get(i).traverse(this);
            }

            addIntoInterCodes(Operation.FUNC_END, func, null, null);
        }

        public void translateFuncFParam(TreeNode node, int count) {

            int length = node.children.size();
            int dimension = 0;

            traverseAllChildren(node);

            QuaternionIdentify paramName;
            QuaternionIdentify countIdentify = new QuaternionIdentify(String.valueOf(count));
            QuaternionIdentify secondSize = new QuaternionIdentify("");

            if (length == 2) {

                paramName = new QuaternionIdentify(node.children.get(1).value);

                addIntoInterCodes(Operation.FORMAL_PARA_INT, paramName, null, countIdentify);
            }
            else if (length == 4) {

                paramName = new QuaternionIdentify(node.children.get(1).value);
                dimension = 1;

                addIntoInterCodes(Operation.FORMAL_PARA_ARRAY, paramName, null, countIdentify);
            }
            else {

                paramName = new QuaternionIdentify(node.children.get(1).value);
                dimension = 2;

                secondSize = node.children.get(5).getQuaternionIdentify();

                addIntoInterCodes(Operation.FORMAL_PARA_ARRAY, paramName, secondSize, countIdentify);
            }

            setIdentifyToTreeNode(node.children.get(1), paramName);
            linkIdentifyWithSymbolTableItem(node.children.get(1).value, paramName, node.getScope());

            if (dimension == 2) {
                paramName.getSymbolTableItem().setArraySize(new QuaternionIdentify(""), secondSize);
            }
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
                addIntoInterCodes(Operation.RETURN, currentFunc, null, null);
            }
            else if (length == 3) {
                QuaternionIdentify returnValue = node.children.get(1).getQuaternionIdentify();

                addIntoInterCodes(Operation.RETURN, currentFunc, returnValue, null);
            }
        }

        private void translateGetint(TreeNode node) {
            translateLVal(node.children.get(0), true);
            QuaternionIdentify value = new QuaternionIdentify("");
            QuaternionIdentify identify = node.children.get(0).getQuaternionIdentify();
            if (identify.isAddress) {
                addIntoInterCodes(Operation.GETINT_TO_ADDRESS, identify, null, null);
            }
            else {
                addIntoInterCodes(Operation.GETINT, value, null, null);
                addIntoInterCodes(Operation.SET_VALUE, identify, value, null);
            }
        }

        private void translateBreak(TreeNode node) {
            QuaternionIdentify endLabel = endLabelStack.peek();
            addIntoInterCodes(Operation.SKIP, endLabel, null, null);
            /*
            这一整个循环结束了
            不仅是结束标签要出栈，开始标签也要出栈
             */
            /*
            你出栈出个鬼鬼啊
            你在这里出栈，其他的break和continue用什么啊
            中间代码又不是实际的控制流
             */
            // beginLabelStack.pop();
            // endLabelStack.pop();
        }

        private void translateContinue(TreeNode node) {
            QuaternionIdentify updateLabel = updateLabelStack.peek();
            addIntoInterCodes(Operation.SKIP, updateLabel, null, null);
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
                translateAllExp(node.children.get(i));
                addIntoInterCodes(Operation.PRINT_INT, node.children.get(i).getQuaternionIdentify(), null, null);
                i += 2;
            }
            int numberOfSubString = subStrings.length;
            // 没有字符串输出，全tm是数字
            if (numberOfSubString == 0) {
                while (!node.children.get(i).value.equals(";")) {
                    translateAllExp(node.children.get(i));
                    addIntoInterCodes(Operation.PRINT_INT, node.children.get(i).getQuaternionIdentify(), null, null);
                    i += 2;
                }
            }
            else {

                int j = 0;

                if (subStrings[0].isEmpty()) {
                    j ++;
                }
                for (; j < numberOfSubString; j++) {
                    addIntoInterCodes(Operation.PRINT_STRING, new QuaternionIdentify(subStrings[j]), null, null);
                    if (!node.children.get(i).value.equals(";")) {
                        translateAllExp(node.children.get(i));
                        addIntoInterCodes(Operation.PRINT_INT, node.children.get(i).getQuaternionIdentify(), null, null);
                        i += 2;
                    }
                }
            }

        }

        private void translateFor(TreeNode node) {
            int i;
            int length = node.children.size();
            QuaternionIdentify beginLabel = new QuaternionIdentify("");
            QuaternionIdentify endLabel = new QuaternionIdentify("");
            QuaternionIdentify updateLabel = new QuaternionIdentify("");
            QuaternionIdentify condition;

            /*
            要在这里把开始标签和结束标签加入栈中
            这样好让每一个break和continue都知道它们要去哪一个结束标签/开始标签
            但是这个标签具体在哪
            不知道
             */
            beginLabelStack.push(beginLabel);
            endLabelStack.push(endLabel);
            updateLabelStack.push(updateLabel);

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

            node.children.get(length - 1).traverse(this);

            addIntoInterCodes(Operation.LABEL, updateLabel, null, null);
            // 更新表达式
            if (!node.children.get(i).value.equals(")")) {
                node.children.get(i).traverse(this);
            }

            addIntoInterCodes(Operation.SKIP, beginLabel, null, null);

            addIntoInterCodes(Operation.LABEL, endLabel, null, null);

            if (!beginLabelStack.isEmpty()) {
                beginLabelStack.pop();
            }
            if (!endLabelStack.isEmpty()) {
                endLabelStack.pop();
            }
            if (!updateLabelStack.isEmpty()) {
                updateLabelStack.pop();
            }
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
                    // node.children.get(i).traverse(this);
                    translateStmt(node.children.get(i));
                }
            }

            addIntoInterCodes(Operation.LABEL, endLabel, null, null);
        }

        @Override
        public void translateStmt(TreeNode node) {

            int i = 0;
            int length = node.children.size();

            if (length == 0) {
                return;
            }

            // 'return' [Exp] ';'
            if (node.children.get(0).value.equals("return")) {
                translateReturn(node);
            }
            // LVal '=' 'getint''('')'';'
            else if (length >= 4 && node.children.get(length - 4).value.equals("getint")) {
                translateGetint(node);
            }
            // LVal '=' Exp ';'
            else if (length == 4 && node.children.get(1).value.equals("=")) {

                translateLVal(node.children.get(0), true);
                translateAllExp(node.children.get(2));

                QuaternionIdentify left = node.children.get(0).getQuaternionIdentify();
                QuaternionIdentify right = node.children.get(2).getQuaternionIdentify();
                if (left.isAddress) {
                    addIntoInterCodes(Operation.STORE_TO_ADDRESS, left, right, null);
                }
                else {
                    addIntoInterCodes(Operation.SET_VALUE, left, right, null);
                }
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
            // Block
            else if (node.children.get(0).value.equals("{")) {
                for (int j = 1; j < length - 1; j++) {
                    translateBlockItem(node.children.get(j));
                }
            }
            else {
                translateAllExp(node.children.get(0));
            }
        }

        // TODO 这里有一个问题，就是把二维数组的一部分作为参数传递给函数
        @Override
        public void translateLVal(TreeNode node, boolean isAssign) {

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
                identify = getIdentifyOfSymbolName(nameString, node.getScope());
                setIdentifyToTreeNode(node, identify);
                return;
            }

            QuaternionIdentify name = getIdentifyOfSymbolName(node.children.get(0).value, node.children.get(0).getScope());

            if (name == null) {
                return;
            }

            // a[1]
            if (length == 4) {
                // 如果是只取二维数组某一维的值
                if (name.getSymbolTableItem().isTwoDimensionArray()) {

                    QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                    identify = new QuaternionIdentify("");

                    ParamResult<QuaternionIdentify> size1;
                    ParamResult<QuaternionIdentify> size2;
                    size1 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                    size2 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                    name.getSymbolTableItem().getArraySize(size1, size2);

                    QuaternionIdentify dimension1 = new QuaternionIdentify("");
                    QuaternionIdentify address = new QuaternionIdentify("");

                    addIntoInterCodes(Operation.MULT, size2.getValue(), offset1, dimension1);

                    addIntoInterCodes(Operation.GET_ADDRESS, name, dimension1, address);
                    addIntoInterCodes(Operation.SET_VALUE, identify, address, null);
                    identify.isAddress = true;

                    setIdentifyToTreeNode(node, identify);
                }
                // 如果真的是个一维数组就好了
                else {
                    // 如果是赋值，必须拿到地址
                    if (isAssign) {
                        QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                        identify = new QuaternionIdentify("");
                        addIntoInterCodes(Operation.GET_ADDRESS, name, offset1, identify);
                        identify.isAddress = true;
                    }
                    // 如果是引用，还需要拿到值
                    else {
                        QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                        QuaternionIdentify address = new QuaternionIdentify("");
                        identify = new QuaternionIdentify("");
                        addIntoInterCodes(Operation.GET_ADDRESS, name, offset1, address);
                        addIntoInterCodes(Operation.GET_VALUE, address, null, identify);
                    }

                    setIdentifyToTreeNode(node, identify);
                }
            }
            // a[1][1]
            else if (length == 7) {

                QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                QuaternionIdentify offset2 = node.children.get(5).getQuaternionIdentify();
                identify = new QuaternionIdentify("");

                ParamResult<QuaternionIdentify> size1;
                ParamResult<QuaternionIdentify> size2;
                size1 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                size2 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                name.getSymbolTableItem().getArraySize(size1, size2);

                QuaternionIdentify offset = new QuaternionIdentify("");

                QuaternionIdentify dimension1 = new QuaternionIdentify("");

                // 我是傻逼
                // 这里要乘上的是第二维的大小！
                // 所以完全不用考虑第一维的大小
                // 啊啊啊啊啊啊啊啊啊
                // 困扰了这么多天
                // 终于明白了
                addIntoInterCodes(Operation.MULT, size2.getValue(), offset1, dimension1);
                addIntoInterCodes(Operation.PLUS, dimension1, offset2, offset);

                // 如果是赋值，必须拿到地址
                if (isAssign) {
                    addIntoInterCodes(Operation.GET_ADDRESS, name, offset, identify);
                    identify.isAddress = true;
                }
                // 如果是引用，还需要拿到值
                else {
                    QuaternionIdentify address = new QuaternionIdentify("");
                    addIntoInterCodes(Operation.GET_ADDRESS, name, offset, address);
                    addIntoInterCodes(Operation.GET_VALUE, address, null, identify);
                }

                setIdentifyToTreeNode(node, identify);
            }

            /*// 如果是全局中的数组
            if (name.getType() == QuaternionIdentifyType.GLOBAL) {
                // a[1]
                if (length == 4) {
                    // 如果是赋值，必须拿到地址
                    if (isAssign) {
                        QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                        identify = new QuaternionIdentify("");
                        addIntoInterCodes(Operation.GET_ADDRESS, name, offset1, identify);
                        identify.isAddress = true;
                    }
                    // 如果是引用，还需要拿到值
                    else {
                        QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                        QuaternionIdentify address = new QuaternionIdentify("");
                        identify = new QuaternionIdentify("");
                        addIntoInterCodes(Operation.GET_ADDRESS, name, offset1, address);
                        addIntoInterCodes(Operation.GET_VALUE, address, null, identify);
                    }

                    setIdentifyToTreeNode(node, identify);
                }
                // a[1][1]
                else if (length == 7) {

                    QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                    QuaternionIdentify offset2 = node.children.get(5).getQuaternionIdentify();
                    identify = new QuaternionIdentify("");

                    ParamResult<QuaternionIdentify> size1;
                    ParamResult<QuaternionIdentify> size2;
                    size1 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                    size2 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                    name.getSymbolTableItem().getArraySize(size1, size2);

                    QuaternionIdentify offset = new QuaternionIdentify("");

                    QuaternionIdentify dimension1 = new QuaternionIdentify("");
                    addIntoInterCodes(Operation.MULT, size2.getValue(), offset1, dimension1);
                    addIntoInterCodes(Operation.PLUS, dimension1, offset2, offset);

                    // 如果是赋值，必须拿到地址
                    if (isAssign) {
                        addIntoInterCodes(Operation.GET_ADDRESS, name, offset, identify);
                        identify.isAddress = true;
                    }
                    // 如果是引用，还需要拿到值
                    else {
                        QuaternionIdentify address = new QuaternionIdentify("");
                        addIntoInterCodes(Operation.GET_ADDRESS, name, offset, address);
                        addIntoInterCodes(Operation.GET_VALUE, address, null, identify);
                    }

                    setIdentifyToTreeNode(node, identify);
                }
            }
            // 如果是局部定义的数组
            else if (name.getType() == QuaternionIdentifyType.LOCAL) {
                // a[1]
                if (length == 4) {
                    QuaternionIdentify headAddress = new QuaternionIdentify("");
                    QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                    QuaternionIdentify address = new QuaternionIdentify("");
                    identify = new QuaternionIdentify("");
                    // 如果是赋值也就是左边，数组以地址的姿态出现即可
                    if (isAssign) {
                        addIntoInterCodes(Operation.GET_ARRAY_HEAD_ADDRESS, name, null, headAddress);
                        addIntoInterCodes(Operation.GET_ADDRESS, headAddress, offset1, identify);
                        identify.isAddress = true;
                    }
                    // 如果是引用，也就是右边，则必须要获取到值
                    else {
                        addIntoInterCodes(Operation.GET_ARRAY_HEAD_ADDRESS, name, null, headAddress);
                        addIntoInterCodes(Operation.GET_ADDRESS, headAddress, offset1, address);
                        addIntoInterCodes(Operation.GET_VALUE, address, null, identify);
                    }

                    setIdentifyToTreeNode(node, identify);
                }
                // a[1][1]
                else if (length == 7) {
                    QuaternionIdentify offset1 = node.children.get(2).getQuaternionIdentify();
                    QuaternionIdentify offset2 = node.children.get(5).getQuaternionIdentify();
                    identify = new QuaternionIdentify("");
                    QuaternionIdentify headAddress = new QuaternionIdentify("");
                    QuaternionIdentify address = new QuaternionIdentify("");

                    ParamResult<QuaternionIdentify> size1;
                    ParamResult<QuaternionIdentify> size2;
                    size1 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                    size2 = new ParamResult<QuaternionIdentify>(new QuaternionIdentify(""));
                    name.getSymbolTableItem().getArraySize(size1, size2);

                    // 事到如今我们不得不重新考虑二维数组存的形式了
                    // 反复的实践证明了一件事情，试图把二维数组连起来变成一维数组只有死路一条
                    // 因为在函数形参中，我绝无可能在四元式阶段及其之前获取到二维数组第一维度的尺寸
                    // 我们不得不试图从另一个角度看待二维数组：
                    // 二维数组是一维数组的首地址组成的数组
                    // 也许这才是符合逻辑的方法
                    // 也许这会更复杂
                    // 但还有什么办法呢
                    // 我们只能这么做了
                    if (isAssign) {
                        QuaternionIdentify dimension1 = new QuaternionIdentify("");
                        QuaternionIdentify dimensionHeadAddress1 = new QuaternionIdentify("");
                        addIntoInterCodes(Operation.GET_ARRAY_HEAD_ADDRESS, name, null, headAddress);
                        addIntoInterCodes(Operation.GET_ADDRESS, headAddress, offset1, dimension1);
                        addIntoInterCodes(Operation.GET_VALUE, dimension1, null, dimensionHeadAddress1);
                        addIntoInterCodes(Operation.GET_ADDRESS, dimensionHeadAddress1, offset2, identify);
                        identify.isAddress = true;
                    } else {
                        QuaternionIdentify dimension1 = new QuaternionIdentify("");
                        QuaternionIdentify dimensionHeadAddress1 = new QuaternionIdentify("");
                        addIntoInterCodes(Operation.GET_ARRAY_HEAD_ADDRESS, name, null, headAddress);
                        addIntoInterCodes(Operation.GET_ADDRESS, headAddress, offset1, dimension1);
                        addIntoInterCodes(Operation.GET_VALUE, dimension1, null, dimensionHeadAddress1);
                        addIntoInterCodes(Operation.GET_ADDRESS, dimensionHeadAddress1, offset2, address);
                        addIntoInterCodes(Operation.GET_VALUE, address, null, identify);
                    }
                    setIdentifyToTreeNode(node, identify);
                }
            }*/
        }

        @Override
        public void translateForStmt(TreeNode node) {
            translateLVal(node.children.get(0), true);
            translateAllExp(node.children.get(2));

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
            int counter = 1;

            if (length == 0) {
                String value = node.value;
                // 如果是个标识符（这个标识符一定是已经出现过了的）
                if (!value.matches("^-?\\d+$")) {
                    identify = getIdentifyOfSymbolName(value, node.getScope());
                }
                // 如果不是标识符，那就应该仅仅是一个数字罢了
                else {
                    identify = new QuaternionIdentify(value);
                }
                setIdentifyToTreeNode(node, identify);
                addIntoInterCodes(Operation.REAL_PARA, identify, new QuaternionIdentify(String.valueOf(counter)), null);
                return;
            }
            else if (length >= 2 && !node.children.get(1).value.equals(",")) {

                translateAllExp(node);
                identify = node.getQuaternionIdentify();
                addIntoInterCodes(Operation.REAL_PARA, identify, new QuaternionIdentify("1"), null);

                return;
            }

            translateAllExp(node.children.get(0));
            identify = node.children.get(0).getQuaternionIdentify();
            addIntoInterCodes(Operation.REAL_PARA, identify, new QuaternionIdentify(String.valueOf(counter)), null);
            counter ++;

            for (int i = 1; i < length - 1; i += 2) {
                translateAllExp(node.children.get(i+1));
                identify = node.children.get(i+1).getQuaternionIdentify();
                addIntoInterCodes(Operation.REAL_PARA, identify, new QuaternionIdentify(String.valueOf(counter)), null);
                counter ++;
            }
        }

        private void translateFunctionCall(TreeNode node) {

            int length = node.children.size();
            QuaternionIdentify funcName = getIdentifyOfSymbolName(node.children.get(0).value, node.getScope());
            QuaternionIdentify res = new QuaternionIdentify("");

            if (funcName.getSymbolTableItem().getFunctionReturnType() == SymbolConst.VOID) {
                res = null;
            }

            addIntoInterCodes(Operation.FUNC_CALL_BEGIN, funcName, null, res);

            if (length == 4) {
                translateFunctionRealPara(node.children.get(2));
            }

            addIntoInterCodes(Operation.FUNC_CALL_END, funcName, null, res);
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
                    identify = getIdentifyOfSymbolName(value, node.getScope());
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
                translateLVal(node, false);
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

            currentFunc = new QuaternionIdentify("main");
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
                    translateDeclare(node.children.get(i), 2, false);
                    i += 2;
                }

            }
            else if (length >= 1 && node.children.get(0).value.equals("int")) {
                int i = 1;
                while (i < length) {
                    translateDeclare(node.children.get(i), 1, false);
                    i += 2;
                }
            }
            else {
                translateStmt(node);
            }
        }

        @Override
        public void translateCompUnit(TreeNode node) {
            int length = node.children.size();
            if (length >= 2 && node.children.get(1).value.equals("main")) {
                translateMainFunc(node);
                return;
            }
            for (TreeNode childNode :
                node.children) {
                childNode.traverse(this);
            }
        }

        @Override
        public void translateFuncFParams(TreeNode node) {
            int length = node.children.size();

            // 只有一个参数，被执行单传节点优化后就成了这样
            if (length >= 2 && !node.children.get(1).value.equals(",")) {
                translateFuncFParam(node, 1);
                return;
            }

            for (int i = 0, j = 1; i < length; i += 2, j++) {
                translateFuncFParam(node.children.get(i), j);
            }
        }

        @Override
        public void translateConst(TreeNode node) {
            node.setQuaternionIdentify(new QuaternionIdentify(String.valueOf(node.constValue)));
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
                if (!identify.getValue().matches("^-?\\d+$")) {
                    output += "_" + identify.id;
                }
            }
        }

        return output;
    }

    public void display() {
        for (SingleQuaternion single :
            this.quaternions) {
            String output = "";
            output += single.getOperation().name();

            output += " ";

            output += displaySingle(single.getParam1());

            output += " ";

            output += displaySingle(single.getParam2());

            output += " ";

            output += displaySingle(single.getResult());

            output += "\n";

            OutputIntoFile.appendToFile(output, "intercode.txt");
        }
    }
}
