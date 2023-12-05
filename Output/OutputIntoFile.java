package Output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class OutputIntoFile {

    /**
     * 在文件后面继续输出
     * @param string 输出内容
     * @param filePath 文件路径
     */
    public static void appendToFile(String string, String filePath) {
        try {

            File outputFile = new File(filePath);
            FileOutputStream fos = new FileOutputStream(outputFile, true);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            writer.append(string);

            writer.close();
            fos.close();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanFile(String filePath) {
        try {

            File outputFile = new File(filePath);
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            writer.write("");

            writer.close();
            fos.close();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 在文件后面继续输出，区别是开头先缩进一个制表符
     */
    public static void appendToFileWithTabAhead(String string, String filePath) {
        appendToFile("\t" + string, filePath);
    }
}
