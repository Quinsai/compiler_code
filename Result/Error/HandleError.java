package Result.Error;

import Lexical.LexicalAnalysis;
import Output.OutputIntoFile;

public class HandleError {

    public static void lexicalError() {
        System.out.println("error");
    }

    public static void handleError(AnalysisErrorType errorType) {

        char c = '*';
        if (errorType == AnalysisErrorType.FORMAT_STRING_WITH_ILLEGAL_CHAR) {
            c = 'a';
        }
        else if (errorType == AnalysisErrorType.NAME_REPEAT) {
            c = 'b';
        }
        else if (errorType == AnalysisErrorType.IDENTIFIER_NOT_DEFINE) {
            c = 'c';
        }
        else if (errorType == AnalysisErrorType.FUNCTION_PARAMS_NUMBER_NOT_MATCH) {
            c = 'd';
        }
        else if (errorType == AnalysisErrorType.FUNCTION_PARAMS_TYPE_NOT_MATCH) {
            c = 'e';
        }
        else if (errorType == AnalysisErrorType.VOID_FUNCTION_WITH_RETURN) {
            c = 'f';
        }
        else if (errorType == AnalysisErrorType.INT_FUNCTION_WITHOUT_RETURN) {
            c = 'g';
        }
        else if (errorType == AnalysisErrorType.ASSIGN_TO_CONST) {
            c = 'h';
        }
        else if (errorType == AnalysisErrorType.LACK_OF_SEMICN) {
            c = 'i';
        }
        else if (errorType == AnalysisErrorType.LACK_OF_RPARENT) {
            c = 'j';
        }
        else if (errorType == AnalysisErrorType.LACK_OF_RBRACK) {
            c = 'k';
        }
        else if (errorType == AnalysisErrorType.FORMAT_CHAR_NUMBER_NOT_MATCH) {
            c = 'l';
        }
        else if (errorType == AnalysisErrorType.UNEXPECTED_BREAK_OR_CONTINUE) {
            c = 'm';
        }

        // 自己调试用，能看的出来到底是什么报错
        // OutputIntoFile.appendToFile(LexicalAnalysis.getInstance().getCurrentLine() + " " + errorType.name() + "\n", "error.txt");
        // 测评用，仅仅输出题目要求的
        OutputIntoFile.appendToFile(LexicalAnalysis.getInstance().getCurrentLine() + " " + c + "\n", "error.txt");
        LexicalAnalysis.getInstance().skipToNextLBraceOrLine();
    }
}
