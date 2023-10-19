package Result.Error;

import Lexical.LexicalAnalysis;
import Output.OutputIntoFile;

public class HandleError {

    public static void lexicalError() {
        System.out.println("error");
    }

    public static void handleError(AnalysisErrorType errorType) {
        OutputIntoFile.appendToFile(LexicalAnalysis.getInstance().getCurrentLine() + " " + errorType.name() + "\n", "error.txt");
        LexicalAnalysis.getInstance().skipToNextLine();
    }
}
