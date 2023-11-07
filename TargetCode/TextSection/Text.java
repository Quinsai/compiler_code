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

    public void generateTextCode() {
        StringBuilder textCodeBuilder = new StringBuilder(textCode);
        for (SingleFunc func :
            funcs) {
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
