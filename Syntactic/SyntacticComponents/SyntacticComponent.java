package Syntactic.SyntacticComponents;

import Lexical.LexicalAnalysis;
import Result.AnalysisResult;
import SymbolTable.MasterTable;
import SyntacticTree.TreeNode;

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
     * 值的类型
     */
    ComponentValueType valueType;

    /**
     * 对应的在语法树中的结点
     */
    TreeNode treeNode;

    public SyntacticComponent() {
        this.lexicalAnalysis = LexicalAnalysis.getInstance();
        this.masterTable = MasterTable.getMasterTable();
        this.valueType = ComponentValueType.NO_MEANING;
    }

    public AnalysisResult analyze(boolean whetherOutput) {
        System.out.println("can u see me");
        return AnalysisResult.SUCCESS;
    }
}