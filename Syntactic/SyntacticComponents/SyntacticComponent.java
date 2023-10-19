package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysis;
import Result.AnalysisResult;
import SymbolTable.MasterTable;

/**
 * 各个语法成分的父类
 */
public class SyntacticComponent {
    /**
     * 词法分析器件
     */
    LexicalAnalysis lexicalAnalysis;

    /**
     * 符号表
     */
    MasterTable masterTable;

    /**
     * 这个非终结符的值
     * 或者0表示假，1表示真
     */
    int intValue;

    public SyntacticComponent() {
        this.lexicalAnalysis = LexicalAnalysis.getInstance();
        this.masterTable = MasterTable.getInstance();
        this.intValue = -1;
    }

    public AnalysisResult analyze(boolean whetherOutput) {
        System.out.println("can u see me");
        return AnalysisResult.SUCCESS;
    }
}