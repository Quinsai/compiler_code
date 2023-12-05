package Other;

/**
 * 参数列表中的返回值类
 * 目的是解决JAVA中一个函数只能有一个返回值的问题
 */
public class ParamResult<T> {
    /**
     * 返回值本值
     */
    private T value;
    /**
     * 用以表示这个返回值是否有效
     */
    private boolean isValid;

    public ParamResult(T initialValue) {
        this.value = initialValue;
        this.isValid = false;
    }

    /**
     * 设置返回值
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
        this.isValid = true;
    }

    /**
     * 获取放回值
     * @return
     */
    public T getValue() {
        return value;
    }

    public boolean whetherValid() {
        return this.isValid;
    }
}
