package Input;

import java.io.File;
import java.util.Scanner;

/**
 * 输入源代码工具类
 */
public class InputSourceCode {

    /**
     * 字符串形式的源代码
     */
    private static String sourceCode;

    /**
     * 读入源代码
     * @return 源代码
     */
    public static void readSourceCode() {

        String sourceCodeString = "";

        try {
            File sourceCodeFile = new File("./testfile.txt");
            Scanner scanner = new Scanner(sourceCodeFile);

            while (scanner.hasNextLine()) {
                sourceCodeString += scanner.nextLine() + "\n";
            }
        }
        catch (Exception e) {
            System.out.println("something wrong happen");
            throw new RuntimeException(e);
        }

        InputSourceCode.sourceCode = sourceCodeString;
    }

    /**
     * 获得字符串形式的源代码
     * @return
     */
    public static String getSourceCode() {
        return sourceCode;
    }
}
