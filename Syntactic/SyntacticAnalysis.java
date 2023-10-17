package Syntactic;

import Input.InputSourceCode;
import Lexical.LexicalAnalysis;
import Syntactic.SyntacticComponents.CompUnit;

/**
 * 语法分析器类
 */
public class SyntacticAnalysis {

    /**
     * 词法分析器
     */
    private LexicalAnalysis lexicalAnalysis;
    /**
     * 字符串形式的源代码
     */
    private String source;
    /**
     * 字符串形式的源代码的长度
     */
    private int sourceLength;

    /**
     * 语法分析器唯一单例
     */
    static SyntacticAnalysis syntacticAnalysis;

    /**
     * 语法分析器构造方法
     */
    private SyntacticAnalysis() {
        this.lexicalAnalysis = LexicalAnalysis.getInstance();
        this.source = InputSourceCode.getSourceCode();
        this.sourceLength = InputSourceCode.getSourceCodeLength();
    }

    static {
        SyntacticAnalysis.syntacticAnalysis = new SyntacticAnalysis();
    }

    /**
     * 获取语法分析器唯一单例
     * @return
     */
    public static SyntacticAnalysis getInstance() {
        return SyntacticAnalysis.syntacticAnalysis;
    }

    /**
     * 运行语法分析器
     */
    public void run(boolean whetherOutput) {
        CompUnit compUnit = new CompUnit();
        if (compUnit.analyze(whetherOutput) != SyntacticAnalysisResult.SUCCESS) {
            System.out.println("error!");
        }
    }
}
