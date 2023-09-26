package Lexical;

import java.io.*;
import java.util.LinkedList;

import Error.HandleError;
import Input.InputSourceCode;
import Other.ParamResult;
import Output.OutputIntoFile;

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
        this.source = InputSourceCode.getSourceCode();
        this.sourceLength = InputSourceCode.getSourceCodeLength();
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

            ParamResult<String> categoryCode = new ParamResult<>("");
            ParamResult<String> value = new ParamResult<>("");

            if (next(whetherOutput, categoryCode, value) != 0) {
                break;
            }

            if (categoryCode.whetherValid() && value.whetherValid()) {
                System.out.println(categoryCode.getValue() + " " + value.getValue());
            }

        }
    }

    /**
     * 读取下一个单词
     * @param whetherOutput 是否输出读取到的单词的类别码和值
     * @param categoryCode 参数中的返回值，单词类别码
     * @param value 参数中的返回值，单词值
     * @return 0表示正常，-1表示读到源代码尾巴，-2表示出错
     */
    public int next(boolean whetherOutput, ParamResult<String> categoryCode, ParamResult<String> value) {
        if (currentIndex >= sourceLength) {
            return LexicalAnalysisResult.END;
        }

        String token = ""; // 获取到的单词的值
        char c = source.charAt(currentIndex);
        int res = LexicalAnalysisResult.SUCCESS;

        if (c == ' ') {
            res = LexicalAnalysisResult.SUCCESS;
        }
        else if (c == '\n') {
            currentLine ++;
            res = LexicalAnalysisResult.SUCCESS;
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
                storeWordResult(type, token, whetherOutput);
                categoryCode.setValue(type);
                value.setValue(token);
                res = LexicalAnalysisResult.SUCCESS;
            }
            else {
                storeWordResult("IDENFR", token, whetherOutput);
                categoryCode.setValue("IDENFR");
                value.setValue(token);
                res = LexicalAnalysisResult.SUCCESS;
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

            storeWordResult("INTCON", token, whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("INTCON");
            value.setValue(token);
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

            storeWordResult("STRCON", token, whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("STRCON");
            value.setValue(token);
        }
        else if (c == '!') {
            if (currentIndex == sourceLength - 1 || source.charAt(currentIndex + 1) != '=') {
                storeWordResult("NOT", "!", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("NOT");
                value.setValue("!");
            }
            else if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("NEQ", "!=", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("NEQ");
                value.setValue("!=");
                currentIndex ++;
            }
            else {
                res = LexicalAnalysisResult.ERROR;
            }
        }
        else if (c == '&') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '&') {
                storeWordResult("AND", "&&", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("AND");
                value.setValue("&&");
                currentIndex ++;
            }
            else {
                res = LexicalAnalysisResult.ERROR;
                HandleError.lexicalError();
            }
        }
        else if (c == '|') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '|') {
                storeWordResult("OR", "||", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("OR");
                value.setValue("||");
                currentIndex ++;
            }
            else {
                res = LexicalAnalysisResult.ERROR;
                HandleError.lexicalError();
            }
        }
        else if (c == '+') {
            storeWordResult("PLUS", "+", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("PLUS");
            value.setValue("+");
        }
        else if (c == '-') {
            storeWordResult("MINU", "-", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("MINU");
            value.setValue("-");
        }
        else if (c == '*') {
            storeWordResult("MULT", "*", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("MULT");
            value.setValue("*");
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
                    storeWordResult("DIV", "/", whetherOutput);
                    res = LexicalAnalysisResult.SUCCESS;
                    categoryCode.setValue("DIV");
                    value.setValue("/");
                }
            }
        }
        else if (c == '%') {
            storeWordResult("MOD", "%", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("MOD");
            value.setValue("%");
        }
        else if (c == '<') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("LEQ", "<=", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("LEQ");
                value.setValue("<=");
                currentIndex ++;
            }
            else {
                storeWordResult("LSS", "<", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("LSS");
                value.setValue("<");
            }
        }
        else if (c == '>') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("GEQ", ">=", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("GEQ");
                value.setValue(">=");
                currentIndex ++;
            }
            else {
                storeWordResult("GRE", ">", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("GRE");
                value.setValue(">");
            }
        }
        else if (c == '=') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("EQL", "==", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("EQL");
                value.setValue("==");
                currentIndex ++;
            }
            else {
                storeWordResult("ASSIGN", "=", whetherOutput);
                res = LexicalAnalysisResult.SUCCESS;
                categoryCode.setValue("ASSIGN");
                value.setValue("=");
            }
        }
        else if (c == ';') {
            storeWordResult("SEMICN", ";", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("SEMICN");
            value.setValue(";");
        }
        else if (c == ',') {
            storeWordResult("COMMA", ",", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("COMMA");
            value.setValue(",");
        }
        else if (c == '(') {
            storeWordResult("LPARENT", "(", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("LPARENT");
            value.setValue("(");
        }
        else if (c == ')') {
            storeWordResult("RPARENT", ")", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("RPARENT");
            value.setValue(")");
        }
        else if (c == '[') {
            storeWordResult("LBRACK", "[", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("LBRACK");
            value.setValue("[");
        }
        else if (c == ']') {
            storeWordResult("RBRACK", "]", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("RBRACK");
            value.setValue("]");
        }
        else if (c == '{') {
            storeWordResult("LBRACE", "{", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("LBRACE");
            value.setValue("{");
        }
        else if (c == '}') {
            storeWordResult("RBRACE", "}", whetherOutput);
            res = LexicalAnalysisResult.SUCCESS;
            categoryCode.setValue("RBRACE");
            value.setValue("}");
        }
        else {
            res = LexicalAnalysisResult.ERROR;
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
     * 偷看下一个单词
     * 与next的区别是不改变currentIndex的值，也不输出
     * @param categoryCode 参数中的返回值，单词类别码
     * @param value 参数中的返回值，单词值
     * @return 0表示正常，-1表示结束，-2表示出错
     */
    public int peek(ParamResult<String> categoryCode, ParamResult<String> value) {
        int formerIndex = this.currentIndex;
        int res = next(false, categoryCode, value);
        this.currentIndex = formerIndex;
        return res;
    }

    /**
     * 存储分析的单词的结果
     * @param categoryCode 类别码
     * @param value 单词的值
     * @param whetherOutput 是否输出读取到的单词的类别码和值
     */
    private void storeWordResult(String categoryCode, String value, boolean whetherOutput) {
        LexicalTableItem newItem = new LexicalTableItem(categoryCode, value);
        lexicalTable.add(newItem);
        if (whetherOutput) {
            output(categoryCode, value);
        }
    }

    /**
     * 输出分析结果
     */
    private void output(String categoryCode, String value) {
        OutputIntoFile.appendToFile(categoryCode + " " + value + "\n", "output.txt");
    }

}

