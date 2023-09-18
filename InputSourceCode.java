import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * 输入源代码工具类
 */
public class InputSourceCode {

    /**
     * 获取字符串形式的源代码
     * @return 源代码
     */
    public static String getSourceCode() {

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

        return sourceCodeString;
    }
}
