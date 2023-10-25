package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import Syntactic.SyntacticTree.Tree;
import Syntactic.SyntacticTree.TreeNode;

public class CompUnit extends SyntacticComponent {

    public CompUnit(TreeNode parent) {
        super();
        this.treeNode = Tree.getInstance().getRoot();
    }

    private static final int DECL = 665;
    private static final int FUNC_DEF = 878;
    private static final int MAIN_FUNC_DEF = 847;
    private static final int FAIL = 868;

    private int whichComponent() {
        AnalysisResult res;
        ParamResult<String>[] nextWordsCategoryCode = new ParamResult[3];
        ParamResult<String>[] nextWordsValue = new ParamResult[3];
        for (int i = 0; i < 3; i++) {
            nextWordsCategoryCode[i] = new ParamResult<String>("");
            nextWordsValue[i] = new ParamResult<String>("");
        }

        res = lexicalAnalysis.peekMany(3, nextWordsCategoryCode, nextWordsValue);

        if (res == AnalysisResult.FAIL) {
            return FAIL;
        }
        if (nextWordsCategoryCode[0].getValue().equals("CONSTTK")) {
            return CompUnit.DECL;
        }
        else if (nextWordsCategoryCode[0].getValue().equals("VOIDTK")) {
            return CompUnit.FUNC_DEF;
        }
        else if (nextWordsCategoryCode[0].getValue().equals("INTTK")) {
            if (nextWordsCategoryCode[2].getValue().equals("LPARENT")) {
                if (nextWordsCategoryCode[1].getValue().equals("MAINTK")) {
                    return CompUnit.MAIN_FUNC_DEF;
                }
                else {
                    return CompUnit.FUNC_DEF;
                }
            }
            else {
                return CompUnit.DECL;
            }
        }
        else {
            return CompUnit.DECL;
        }
    }

    /**
     * 分析CompUnit
     * @return 0表示正常，-1表示结束，-2表示出错
     */
    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        int comp = 0;

        while (true) {
            comp = whichComponent();
            if (comp == CompUnit.DECL) {
                Decl decl = new Decl(treeNode);
                res = decl.analyze(whetherOutput);
                if (res == AnalysisResult.FAIL) {
                    return res;
                }
            }
            else {
                break;
            }
        }

        while (true) {
            comp = whichComponent();
            if (comp == CompUnit.FUNC_DEF) {
                FuncDef funcDef = new FuncDef(treeNode);
                res = funcDef.analyze(whetherOutput);
                if (res == AnalysisResult.FAIL) {
                    return res;
                }
            }
            else {
                break;
            }
        }

        MainFuncDef mainFuncDef = new MainFuncDef(treeNode);
        res = mainFuncDef.analyze(whetherOutput);
        if (res == AnalysisResult.FAIL) {
            return res;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<CompUnit>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;

    }
}
