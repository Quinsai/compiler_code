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

    public SyntacticComponent() {
        this.lexicalAnalysis = LexicalAnalysis.getInstance();
        this.masterTable = MasterTable.getInstance();
    }

    public AnalysisResult analyze(boolean whetherOutput) {
        System.out.println("can u see me");
        return AnalysisResult.SUCCESS;
    }
}