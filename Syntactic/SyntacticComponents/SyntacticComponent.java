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

    /**
     * 这个非终结符的值
     * 或者0表示假，1表示真
     */
    int value;

    public SyntacticComponent() {
        this.lexicalAnalysis = LexicalAnalysis.getInstance();
        this.value = -1;
    }

    public int analyze(boolean whetherOutput) {
        System.out.println("can u see me");
        return 0;
    }
}