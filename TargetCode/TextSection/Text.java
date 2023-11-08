package TargetCode.TextSection;

import java.util.ArrayList;

/**
 * MIPS中的.text段
 */
public class Text {

    private ArrayList<SingleFunc> funcs;

    private String textCode;

    public Text() {
        this.funcs = new ArrayList<>();
        this.textCode = ".text\n";
    }

    /**
     * 全局数据中有部分内容需要.text段来辅助实现，比如说int a = 1 + 2;这种写法
     * 所以需要这个函数
     */
    public void addGlobalDataIntoTextCode(String code) {
        this.textCode += code;
    }

    public void generateTextCode() {
        StringBuilder textCodeBuilder = new StringBuilder(textCode);
        for (SingleFunc func :
            funcs) {
            func.generateFuncCode(new GenerateText());
            textCodeBuilder.append(func.getFuncCode());
        }
        this.textCode = textCodeBuilder.toString();
    }

    public void addIntoFuncs(SingleFunc func) {
        this.funcs.add(func);
    }

    public String getTextCode() {
        return textCode;
    }
}
