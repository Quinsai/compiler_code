package Syntactic.SyntacticComponents;

import Other.ParamResult;
import Output.OutputIntoFile;
import Result.AnalysisResult;
import SymbolTable.MasterTableItem;

import java.util.ArrayList;

public class FuncRParams extends SyntacticComponent {

    /**
     * 实参类型列表
     */
    private ArrayList<ComponentValueType> realParamsTypeList;

    public FuncRParams() {
        super();
        this.realParamsTypeList = new ArrayList<>();
    }

    @Override
    public AnalysisResult analyze(boolean whetherOutput) {
        AnalysisResult res;
        ParamResult<String> nextWordCategoryCode = new ParamResult<>("");
        ParamResult<String> nextWordValue = new ParamResult<>("");

        Exp exp = new Exp();
        res = exp.analyze(whetherOutput);
        if (res != AnalysisResult.SUCCESS) {
            return AnalysisResult.FAIL;
        }

        this.realParamsTypeList.add(exp.valueType);

        while (true) {
            res = lexicalAnalysis.peek(nextWordCategoryCode, nextWordValue);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }
            if (!nextWordCategoryCode.getValue().equals("COMMA")) {
                break;
            }
            res = lexicalAnalysis.next(whetherOutput, nextWordCategoryCode, nextWordValue);

            Exp exp1 = new Exp();
            res = exp1.analyze(whetherOutput);
            if (res != AnalysisResult.SUCCESS) {
                return AnalysisResult.FAIL;
            }

            this.realParamsTypeList.add(exp1.valueType);
        }

        if (whetherOutput) {
            OutputIntoFile.appendToFile("<FuncRParams>\n", "output.txt");
        }
        return AnalysisResult.SUCCESS;
    }

    public ArrayList<ComponentValueType> getRealParamsTypeList() {
        return realParamsTypeList;
    }
}
