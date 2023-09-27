package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysis;

/**
 * 各个语法成分的父类
 */
public class SyntacticComponent {
    /**
     * 词法分析器件
     */
    LexicalAnalysis lexicalAnalysis;

    public SyntacticComponent() {
        this.lexicalAnalysis = LexicalAnalysis.getInstance();
    }

    public int analyze() {
        System.out.println("can u see me");
        return 0;
    }

}