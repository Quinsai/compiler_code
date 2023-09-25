package Lexical;

import java.io.*;
import java.util.LinkedList;

import Error.HandleError;

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
     * @param sourceCode 源代码
     */
    public LexicalAnalysis(String sourceCode) {
        this.source = sourceCode;
        this.sourceLength = sourceCode.length();
        this.currentIndex = 0;
        this.currentLine = 1;
        this.lexicalTable = new LinkedList<LexicalTableItem>();
    }

    /**
     * 运行词法分析器
     */
    public void run() {
        while(true) {
            if (next() != 0) {
                break;
            }
        }
        output();
    }

    /**
     * 词法分析
     * @return 0表示正常，-1表示结束，-2表示出错
     */
    public int next() {
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
                addIntoLexicalTable(type, token);
            }
            else {
                addIntoLexicalTable("IDENFR", token);
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

            addIntoLexicalTable("INTCON", token);
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

            addIntoLexicalTable("STRCON", token);
        }
        else if (c == '!') {
            if (currentIndex == sourceLength - 1 || source.charAt(currentIndex + 1) != '=') {
                addIntoLexicalTable("NOT", "!");
            }
            else {
                addIntoLexicalTable("NEQ", "!=");
                currentIndex ++;
            }
        }
        else if (c == '&') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '&') {
                addIntoLexicalTable("AND", "&&");
                currentIndex ++;
            }
            else {
                HandleError.lexicalError();
            }
        }
        else if (c == '|') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '|') {
                addIntoLexicalTable("OR", "||");
                currentIndex ++;
            }
            else {
                HandleError.lexicalError();
            }
        }
        else if (c == '+') {
            addIntoLexicalTable("PLUS", "+");
        }
        else if (c == '-') {
            addIntoLexicalTable("MINU", "-");
        }
        else if (c == '*') {
            addIntoLexicalTable("MULT", "*");
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
                    addIntoLexicalTable("DIV", "/");
                }
            }
        }
        else if (c == '%') {
            addIntoLexicalTable("MOD", "%");
        }
        else if (c == '<') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                addIntoLexicalTable("LEQ", "<=");
                currentIndex ++;
            }
            else {
                addIntoLexicalTable("LSS", "<");
            }
        }
        else if (c == '>') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                addIntoLexicalTable("GEQ", ">=");
                currentIndex ++;
            }
            else {
                addIntoLexicalTable("GRE", ">");
            }
        }
        else if (c == '=') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                addIntoLexicalTable("EQL", "==");
                currentIndex ++;
            }
            else {
                addIntoLexicalTable("ASSIGN", "=");
            }
        }
        else if (c == ';') {
            addIntoLexicalTable("SEMICN", ";");
        }
        else if (c == ',') {
            addIntoLexicalTable("COMMA", ",");
        }
        else if (c == '(') {
            addIntoLexicalTable("LPARENT", "(");
        }
        else if (c == ')') {
            addIntoLexicalTable("RPARENT", ")");
        }
        else if (c == '[') {
            addIntoLexicalTable("LBRACK", "[");
        }
        else if (c == ']') {
            addIntoLexicalTable("RBRACK", "]");
        }
        else if (c == '{') {
            addIntoLexicalTable("LBRACE", "{");
        }
        else if (c == '}') {
            addIntoLexicalTable("RBRACE", "}");
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
     */
    private void addIntoLexicalTable(String categoryCode, String value) {
        LexicalTableItem newItem = new LexicalTableItem(categoryCode, value);
        lexicalTable.add(newItem);
    }

    /**
     * 输出分析结果
     */
    private void output() {
        try {

            File outputFile = new File("./output.txt");
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            int size = lexicalTable.size();
            for (int i = 0; i < size; i++) {
                String categoryCode = lexicalTable.get(i).categoryCode;
                String value = lexicalTable.get(i).value;
                writer.write(categoryCode + " " + value + "\n");
            }

            writer.close();
            fos.close();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

