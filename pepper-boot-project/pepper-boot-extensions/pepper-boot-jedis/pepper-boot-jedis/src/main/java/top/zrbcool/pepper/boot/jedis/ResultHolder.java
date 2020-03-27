package top.zrbcool.pepper.boot.jedis;

/**
 * @author zhangrongbincool@163.com
 * @version 19-10-24
 */
public class ResultHolder<T> {
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
