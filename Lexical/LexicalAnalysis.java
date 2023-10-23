package Lexical;

import java.util.LinkedList;

import Result.Error.AnalysisErrorType;
import Result.Error.HandleError;
import Input.InputSourceCode;
import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;

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

            if (next(whetherOutput, categoryCode, value) == AnalysisResult.END) {
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
    public AnalysisResult next(boolean whetherOutput, ParamResult<String> categoryCode, ParamResult<String> value) {
        if (currentIndex >= sourceLength) {
            return AnalysisResult.END;
        }

        String token = ""; // 获取到的单词的值
        char c = source.charAt(currentIndex);
        AnalysisResult res;

        while (true) {
            if (c == ' ' || c == '\n' || c == '\t') {
                if (c == '\n') {
                    currentLine ++;
                }
                currentIndex ++;
                if (currentIndex >= sourceLength) {
                    return AnalysisResult.END;
                }
                c = source.charAt(currentIndex);
            }
            else {
                break;
            }
        }

        if (c == '_' || Character.isLetter(c)) {
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
                res = AnalysisResult.SUCCESS;
            }
            else {
                storeWordResult("IDENFR", token, whetherOutput);
                categoryCode.setValue("IDENFR");
                value.setValue(token);
                res = AnalysisResult.SUCCESS;
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
            res = AnalysisResult.SUCCESS;
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

            res = checkFormatString(token);
            if (res == AnalysisResult.FAIL) {
                return AnalysisResult.FAIL;
            }

            storeWordResult("STRCON", token, whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("STRCON");
            value.setValue(token);
        }
        else if (c == '!') {
            if (currentIndex == sourceLength - 1 || source.charAt(currentIndex + 1) != '=') {
                storeWordResult("NOT", "!", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("NOT");
                value.setValue("!");
            }
            else if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("NEQ", "!=", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("NEQ");
                value.setValue("!=");
                currentIndex ++;
            }
            else {
                res = AnalysisResult.FAIL;
            }
        }
        else if (c == '&') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '&') {
                storeWordResult("AND", "&&", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("AND");
                value.setValue("&&");
                currentIndex ++;
            }
            else {
                res = AnalysisResult.FAIL;
                HandleError.lexicalError();
            }
        }
        else if (c == '|') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '|') {
                storeWordResult("OR", "||", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("OR");
                value.setValue("||");
                currentIndex ++;
            }
            else {
                res = AnalysisResult.FAIL;
                HandleError.lexicalError();
            }
        }
        else if (c == '+') {
            storeWordResult("PLUS", "+", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("PLUS");
            value.setValue("+");
        }
        else if (c == '-') {
            storeWordResult("MINU", "-", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("MINU");
            value.setValue("-");
        }
        else if (c == '*') {
            storeWordResult("MULT", "*", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("MULT");
            value.setValue("*");
        }
        else if (c == '/') {
            if (currentIndex == sourceLength - 1) {
                HandleError.lexicalError();
                res = AnalysisResult.FAIL;
            }
            else {
                char nextC = source.charAt(currentIndex + 1);
                if (nextC == '/') {
                    int tempIndex = currentIndex;
                    for (; tempIndex < sourceLength && source.charAt(tempIndex) != '\n'; tempIndex ++);
                    currentIndex = tempIndex + 1;
                    currentLine ++;
                    return next(whetherOutput, categoryCode, value);
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
                    currentIndex = tempIndex + 1;
                    return next(whetherOutput, categoryCode, value);
                }
                else {
                    storeWordResult("DIV", "/", whetherOutput);
                    res = AnalysisResult.SUCCESS;
                    categoryCode.setValue("DIV");
                    value.setValue("/");
                }
            }
        }
        else if (c == '%') {
            storeWordResult("MOD", "%", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("MOD");
            value.setValue("%");
        }
        else if (c == '<') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("LEQ", "<=", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("LEQ");
                value.setValue("<=");
                currentIndex ++;
            }
            else {
                storeWordResult("LSS", "<", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("LSS");
                value.setValue("<");
            }
        }
        else if (c == '>') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("GEQ", ">=", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("GEQ");
                value.setValue(">=");
                currentIndex ++;
            }
            else {
                storeWordResult("GRE", ">", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("GRE");
                value.setValue(">");
            }
        }
        else if (c == '=') {
            if (currentIndex != sourceLength - 1 && source.charAt(currentIndex + 1) == '=') {
                storeWordResult("EQL", "==", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("EQL");
                value.setValue("==");
                currentIndex ++;
            }
            else {
                storeWordResult("ASSIGN", "=", whetherOutput);
                res = AnalysisResult.SUCCESS;
                categoryCode.setValue("ASSIGN");
                value.setValue("=");
            }
        }
        else if (c == ';') {
            storeWordResult("SEMICN", ";", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("SEMICN");
            value.setValue(";");
        }
        else if (c == ',') {
            storeWordResult("COMMA", ",", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("COMMA");
            value.setValue(",");
        }
        else if (c == '(') {
            storeWordResult("LPARENT", "(", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("LPARENT");
            value.setValue("(");
        }
        else if (c == ')') {
            storeWordResult("RPARENT", ")", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("RPARENT");
            value.setValue(")");
        }
        else if (c == '[') {
            storeWordResult("LBRACK", "[", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("LBRACK");
            value.setValue("[");
        }
        else if (c == ']') {
            storeWordResult("RBRACK", "]", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("RBRACK");
            value.setValue("]");
        }
        else if (c == '{') {
            storeWordResult("LBRACE", "{", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("LBRACE");
            value.setValue("{");
        }
        else if (c == '}') {
            storeWordResult("RBRACE", "}", whetherOutput);
            res = AnalysisResult.SUCCESS;
            categoryCode.setValue("RBRACE");
            value.setValue("}");
        }
        else {
            res = AnalysisResult.FAIL;
        }

        if (res == AnalysisResult.SUCCESS) {
            currentIndex ++;
        }

        return res;
    }

    /**
     * 偷看下一个单词
     * 与next的区别是不改变currentIndex的值，也不输出
     * @param categoryCode 参数中的返回值，单词类别码
     * @param value 参数中的返回值，单词值
     */
    public AnalysisResult peek(ParamResult<String> categoryCode, ParamResult<String> value) {
        int formerIndex = this.currentIndex;
        int formerLine = this.currentLine;
        AnalysisResult res = next(false, categoryCode, value);
        this.currentIndex = formerIndex;
        this.currentLine = formerLine;
        return res;
    }

    /**
     * 批量偷看多个单词
     * @param number 偷看的数目
     * @param categoryCodeArray 参数中的返回值，单词类别码数组
     * @param valueArray 参数中的返回值，单词值数组
     */
    public AnalysisResult peekMany(int number, ParamResult<String>[] categoryCodeArray, ParamResult<String>[] valueArray) {
        int formerIndex = this.currentIndex;
        int formerLine = this.currentLine;
        AnalysisResult res = AnalysisResult.SUCCESS;
        for (int i = 0; i < number; i ++) {
            res = next(false, categoryCodeArray[i], valueArray[i]);
            if (res != AnalysisResult.SUCCESS) {
                return res;
            }
        }
        this.currentIndex = formerIndex;
        this.currentLine = formerLine;
        return res;
    }

    public boolean haveAssignBeforeSemicn() {
        boolean res = false;
        int formerIndex = this.currentIndex;
        int formerLine = this.currentLine;

        while (true) {
            ParamResult<String> categoryCode = new ParamResult<>("");
            ParamResult<String> value = new ParamResult<>("");
            next(false, categoryCode, value);
            if (categoryCode.getValue().equals("ASSIGN")) {
                res = true;
                break;
            }
            if (categoryCode.getValue().equals("SEMICN")) {
                break;
            }
        }

        this.currentIndex = formerIndex;
        this.currentLine = formerLine;
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

    public int getCurrentLine() {
        return currentLine;
    }

    /**
     * 跳过错误部分
     */
    public void skipErrorPart() {
        final int NEXT_LINE = 1322;
        final int NEXT_LBRACE = 8690;
        final int NEXT_SEMICN = 3244;

        int delta = 0;

        while (currentIndex < sourceLength) {
            char c = source.charAt(currentIndex);
            if (c == '\n') {
                this.currentLine ++;
            }
            if (c == '\n') {
                delta = NEXT_LINE;
                break;
            }
            else if (c == '{') {
                delta = NEXT_LBRACE;
                break;
            }
            else if (c ==';') {
                delta = NEXT_SEMICN;
                break;
            }
            currentIndex ++;
        }

        if (delta == NEXT_LINE || delta == NEXT_SEMICN) {
            currentIndex ++;
        }
    }

    /**
     * 检查格式化字符串是否没有错误
     * @param formatString 待检查地串
     */
    private AnalysisResult checkFormatString(String formatString) {
        char[] arrayString = formatString.toCharArray();
        int length = arrayString.length;
        for (int i = 1; i< length - 1; i++) {
            char c = arrayString[i];
            int delta = c;
            if (delta == 32 || delta == 33 || (delta >= 40 && delta <= 126 && delta != 92)) {
                continue;
            }
            else if (delta == 92) {
                if (i + 1 < length - 1 && arrayString[i+1] == 'n') {
                    i++;
                    continue;
                }
                else {
                    HandleError.handleError(AnalysisErrorType.FORMAT_STRING_WITH_ILLEGAL_CHAR);
                    return AnalysisResult.FAIL;
                }
            }
            else if (delta == 37) {
                if (i + 1 < length - 1 && arrayString[i+1] == 'd') {
                    i++;
                    continue;
                }
                else {
                    HandleError.handleError(AnalysisErrorType.FORMAT_STRING_WITH_ILLEGAL_CHAR);
                    return AnalysisResult.FAIL;
                }
            }
            else {
                HandleError.handleError(AnalysisErrorType.FORMAT_STRING_WITH_ILLEGAL_CHAR);
                return AnalysisResult.FAIL;
            }
        }

        return AnalysisResult.SUCCESS;
    }

    public void insertSemicn() {
        StringBuilder stringBuilder =  new StringBuilder(this.source);
        stringBuilder.insert(this.currentIndex, ';');
        this.source = new String(stringBuilder);
        this.sourceLength ++;
    }
}

