package Lexical;

import java.util.HashMap;

/**
 * 保留字工具类
 */
public class ReserveWord {

    /**
     * 保留字映射表
     * 键是单词的值，值是保留字的类别码
     */
    private static HashMap<String, String> reserveWordMap;

    static {
        reserveWordMap = new HashMap<String, String>();

        reserveWordMap.put("main", "MAINTK");
        reserveWordMap.put("const", "CONSTTK");
        reserveWordMap.put("int", "INTTK");
        reserveWordMap.put("break", "BREAKTK");
        reserveWordMap.put("continue", "CONTINUETK");
        reserveWordMap.put("if", "IFTK");
        reserveWordMap.put("else", "ELSETK");
        reserveWordMap.put("for", "FORTK");
        reserveWordMap.put("getint", "GETINTTK");
        reserveWordMap.put("printf", "PRINTFTK");
        reserveWordMap.put("return", "RETURNTK");
        reserveWordMap.put("void", "VOIDTK");
    }

    /**
     * 是否是保留字
     * @param word
     * @return 是的话返回类别码，不是的话返回NOT_RESERVE_WORD
     */
    public static String getReserveWord(String word) {
        return reserveWordMap.getOrDefault(word, "NOT_RESERVE_WORD");
    }
}
