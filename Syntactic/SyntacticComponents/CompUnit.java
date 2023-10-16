package Syntactic.SyntacticComponents;

import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Lexical.LexicalAnalysisResult;
import Other.ParamResult;
import Output.OutputIntoFile;
import Syntactic.SyntacticAnalysisResult;

public class CompUnit extends SyntacticComponent {

    /**
     * 唯一实例
     */
    private static CompUnit compUnit;

    private CompUnit() {
        super();
    }

    static {
        compUnit = new CompUnit();
    }

    public static CompUnit getInstance() {
        return compUnit;
    }

    private static final int DECL = 665;
    private static final int FUNC_DEF = 878;
    private static final int MAIN_FUNC_DEF = 847;

    private int whichComponent() {
        int res;
        ParamResult<String>[] nextWordsCategoryCode = new ParamResult[3];
        ParamResult<String>[] nextWordsValue = new ParamResult[3];
        for (int i = 0; i < 3; i++) {
            nextWordsCategoryCode[i] = new ParamResult<String>("");
            nextWordsValue[i] = new ParamResult<String>("");
        }

        res = lexicalAnalysis.peekMany(3, nextWordsCategoryCode, nextWordsValue);

        if (res == LexicalAnalysisResult.ERROR) {
            return SyntacticAnalysisResult.ERROR;
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
    public int analyze(boolean whetherOutput) {
        int res = 0;

        while (true) {
            res = whichComponent();
            if (res == CompUnit.DECL) {
                res = Decl.getInstance().analyze(whetherOutput);
                if (res == SyntacticAnalysisResult.ERROR) {
                    return res;
                }
            }
            else {
                break;
            }
        }

        while (true) {
            res = whichComponent();
            if (res == CompUnit.FUNC_DEF) {
                res = FuncDef.getInstance().analyze(whetherOutput);
                if (res == SyntacticAnalysisResult.ERROR) {
                    return res;
                }
            }
            else {
                break;
            }
        }

        res = MainFuncDef.getInstance().analyze(whetherOutput);
        if (res == SyntacticAnalysisResult.ERROR) {
            return res;
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<CompUnit>\n", "output.txt");
        }
        return SyntacticAnalysisResult.SUCCESS;

    }
}
