package InterCode;

import java.util.ArrayList;

/**
 * 四元式中使用的变量的类型
 */
public class QuadrupleVariable {

    private final String name;

    public int dimension;

    private ArrayList<QuadrupleVariable> valueOfOneDimensionArray;

    private ArrayList<ArrayList<QuadrupleVariable>> valueOfTwoDimensionArray;

    public QuadrupleVariable(String name) {
        this.name = name;
        this.dimension = 0;
    }

    public ArrayList<QuadrupleVariable> getValueOfOneDimensionArray() {
        return valueOfOneDimensionArray;
    }

    public ArrayList<ArrayList<QuadrupleVariable>> getValueOfTwoDimensionArray() {
        return valueOfTwoDimensionArray;
    }
}
