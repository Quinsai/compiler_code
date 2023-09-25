package Lexical;

import java.io.*;
import java.util.LinkedList;

import Error.HandleError;
import Input.InputSourceCode;

/**
 * 词法分析器类
 */
public class LexicalAnalysis {

    /**
     * 源代码
     */
    private String source;
    /**
     * 源代码字符串长度
     */
    private int sourceLength;
    /**
     * 位置指针，指向即将被解析的单词的起始字符的下标
     * 从0开始
     */
    private int currentIndex;
    /**
     * 当前行号
     */
    private int currentLine;
    /**
     * 当前单词的值
     */
    private String currentWordValue;
    /**
     * 当前单词的类别码
     */
    private String currentWordCategoryCode;

    /**
     * 词法分析器唯一单例
     */
    static LexicalAnalysis lexicalAnalysis;

    class LexicalTableItem {
        public String categoryCode;
        public String value;

        LexicalTableItem(String categoryCode, String value) {
            this.categoryCode = categoryCode;
            this.value = value;
        }
    }

    /**
     * 词法分析结果
     */
    private LinkedList<LexicalTableItem> lexicalTable;

    /**
     * 词法分析器构造方法
     */
    private LexicalAnalysis() {
        String sourceCode = InputSourceCode.getSourceCode();
        this.source = sourceCode;
        this.sourceLength = sourceCode.length();
        this.currentIndex = 0;
        this.currentLine = 1;
        this.lexicalTable = new LinkedList<LexicalTableItem>();
    }

    static {
        LexicalAnalysis.lexicalAnalysis = new LexicalAnalysis();
    }

    /**
     * 获取词法分析器唯一单例
     * @return
     */
    public static LexicalAnalysis getInstance() {
        return LexicalAnalysis.lexicalAnalysis;
    }

    /**
     * 运行词法分析器
     * @param whetherOutput 是否输出读取到的单词的类别码和值
     */
    public void run(boolean whetherOutput) {
        while(true) {
            if (next(whetherOutput) != 0) {
                break;
            }
        }
    }

    /**
     * 词法分析
     * @param whetherOutput 是否输出读取到的单词的类别码和值
     * @return 0表示正常，-1表示结束，-2表示出错
     */
    public int next(boolean whetherOutput) {
        if (currentIndex >= sourceLength) {
            return -1;
        }

        String token = ""; // 获取到的单词的值
        char c = source.charAt(currentIndex);
        int res = 0;

        if (c == ' ') {
            res = 0;
        }
        else if (c == '\n') {
            currentLine ++;
            res = 0;
        }
        else if (c == '_' || Character.isLetter(c)) {
            int tempIndex = currentIndex;

            for (; tempIndex < sourceLength; tempIndex ++) {
                char tempC = source.charAt(tempIndex);
                if (tempC != '_' && !Character.isLetter(tempC) && !Character.isDigit(tempC)) {
                    break;
                }

                token += tempC;
            }

            String type = ReserveWord.getReserveWord(token);

            if (!type.equals("NOT_RESERVE_WORD")) {
                addIntoLexicalTable(type, token, whetherOutput);
            }
            else {
                addIntoLexicalTable("IDENFR", token, whetherOutput);
            }

            currentIndex = tempIndex - 1;
        }
        else if (Character.isDigit(c)) {
            int tempIndex = currentIndex;

            for (; tempIndex < sourceLength; tempIndex ++) {
                char tempC = source.charAt(tempIndex);
                if (!Character.isDigit(tempC)) {
                    break;
                }

                token += tempC;
            }

            currentIndex = tempIndex - 1;

            addIntoLexicalTable("INTCON", token, whetherOutput);
        }
        else if (c == '"') {
            int tempIndex = currentIndex + 1;
            token += '"';

            for (; tempIndex < sourceLength; tempIndex ++) {
                char tempC = source.charAt(tempIndex);

                if (tempC == '"') {
                    break;
                }

                token += tempC;
            }

            token += '"';

            currentIndex = tempIndex;

            addIntoLexicalTable("STRCON", token, whetherOutput);
        }
        else if (c == '!') {
            if (currentIndex == sourceLength - 1 || source.charAt(currentIndex + 1) != '=') {
                addIntoLexicalTable("NOT", "!", whetherOutput);
            }
            else {
                addIntoLexicalTable("NEQ", "!=", whetherOutput);
                currentIndex ++;
            }
        }
        else if (c == '&') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '&') {
                addIntoLexicalTable("AND", "&&", whetherOutput);
                currentIndex ++;
            }
            else {
                HandleError.lexicalError();
            }
        }
        else if (c == '|') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '|') {
                addIntoLexicalTable("OR", "||", whetherOutput);
                currentIndex ++;
            }
            else {
                HandleError.lexicalError();
            }
        }
        else if (c == '+') {
            addIntoLexicalTable("PLUS", "+", whetherOutput);
        }
        else if (c == '-') {
            addIntoLexicalTable("MINU", "-", whetherOutput);
        }
        else if (c == '*') {
            addIntoLexicalTable("MULT", "*", whetherOutput);
        }
        else if (c == '/') {
            if (currentIndex == sourceLength - 1) {
                HandleError.lexicalError();
            }
            else {
                char nextC = source.charAt(currentIndex + 1);
                if (nextC == '/') {
                    int tempIndex = currentIndex;
                    for (; tempIndex < sourceLength && source.charAt(tempIndex) != '\n'; tempIndex ++);
                    currentIndex = tempIndex;
                    currentLine ++;
                }
                else if (nextC == '*') {
                    int tempIndex = currentIndex + 1;
                    for (; tempIndex < sourceLength; tempIndex ++) {
                        if (source.charAt(tempIndex - 1) == '*' && source.charAt(tempIndex) == '/') {
                            break;
                        }
                        if (source.charAt(tempIndex) == '\n') {
                            currentLine ++;
                        }
                    }
                    currentIndex = tempIndex;
                }
                else {
                    addIntoLexicalTable("DIV", "/", whetherOutput);
                }
            }
        }
        else if (c == '%') {
            addIntoLexicalTable("MOD", "%", whetherOutput);
        }
        else if (c == '<') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                addIntoLexicalTable("LEQ", "<=", whetherOutput);
                currentIndex ++;
            }
            else {
                addIntoLexicalTable("LSS", "<", whetherOutput);
            }
        }
        else if (c == '>') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                addIntoLexicalTable("GEQ", ">=", whetherOutput);
                currentIndex ++;
            }
            else {
                addIntoLexicalTable("GRE", ">", whetherOutput);
            }
        }
        else if (c == '=') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                addIntoLexicalTable("EQL", "==", whetherOutput);
                currentIndex ++;
            }
            else {
                addIntoLexicalTable("ASSIGN", "=", whetherOutput);
            }
        }
        else if (c == ';') {
            addIntoLexicalTable("SEMICN", ";", whetherOutput);
        }
        else if (c == ',') {
            addIntoLexicalTable("COMMA", ",", whetherOutput);
        }
        else if (c == '(') {
            addIntoLexicalTable("LPARENT", "(", whetherOutput);
        }
        else if (c == ')') {
            addIntoLexicalTable("RPARENT", ")", whetherOutput);
        }
        else if (c == '[') {
            addIntoLexicalTable("LBRACK", "[", whetherOutput);
        }
        else if (c == ']') {
            addIntoLexicalTable("RBRACK", "]", whetherOutput);
        }
        else if (c == '{') {
            addIntoLexicalTable("LBRACE", "{", whetherOutput);
        }
        else if (c == '}') {
            addIntoLexicalTable("RBRACE", "}", whetherOutput);
        }
        else {
            System.out.println("------------------");
            System.out.println(c);
            System.out.println("------------------");
        }

        if (res == 0) {
            currentIndex ++;
        }

        return res;
    }

    /**
     * 添加到分析结果表中
     * @param categoryCode 类别码
     * @param value 单词的值
     * @param whetherOutput 是否输出读取到的单词的类别码和值
     */
    private void addIntoLexicalTable(String categoryCode, String value, boolean whetherOutput) {
        LexicalTableItem newItem = new LexicalTableItem(categoryCode, value);
        lexicalTable.add(newItem);
        this.currentWordValue = value;
        this.currentWordCategoryCode = categoryCode;
        if (whetherOutput) {
            output(categoryCode, value);
        }
    }

    /**
     * 输出分析结果
     */
    private void output(String categoryCode, String value) {
        try {

            File outputFile = new File("./output.txt");
            FileOutputStream fos = new FileOutputStream(outputFile, true);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            int size = lexicalTable.size();

            writer.append(categoryCode + " " + value + "\n");

            writer.close();
            fos.close();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentWordValue() {
        return currentWordValue;
    }

    public String getCurrentWordCategoryCode() {
        return currentWordCategoryCode;
    }
}

