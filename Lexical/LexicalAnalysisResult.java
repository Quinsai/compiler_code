package Lexical;

/**
 * 词法分析结果返回值
 */
public class LexicalAnalysisResult {
    public static int SUCCESS = 0;
    public static int END = -1;
    public static int ERROR = -2;

    private LexicalAnalysisResult() {
        return;
    }
}
