package top.zrbcool.pepper.boot.httpclient;


/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class HttpBioClientProps {
    // 连接池中每个路由（目标host）的连接数
    private int defaultMaxPerRoute = 1000;
    // 连接池总的连接数
    private int maxTotal = defaultMaxPerRoute * 2;
    // 设置通过打开的连接传输数据的超时时间（单位：毫秒）
    private int soTimeout = 50 * 1000;
    /**
     * Defines period of inactivity in milliseconds after which persistent connections must
     * be re-validated prior to being {@link #leaseConnection(java.util.concurrent.Future,
     *   long, java.util.concurrent.TimeUnit) leased} to the consumer. Non-positive value passed
     * to this method disables connection validation. This check helps detect connections
     * that have become stale (half-closed) while kept inactive in the pool.
     */
    private int validateAfterInactivity = 2 * 1000;

    /**
     * thread pool params
     */
    private int corePoolSize = 50;
    private int maximumPoolSize = 200;
    private long keepAliveTime = 60;
    private int capacity = 0;

    private int connIdleTimeout = 60;

    public int getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getValidateAfterInactivity() {
        return validateAfterInactivity;
    }

    public void setValidateAfterInactivity(int validateAfterInactivity) {
        this.validateAfterInactivity = validateAfterInactivity;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getConnIdleTimeout() {
        return connIdleTimeout;
    }

    public void setConnIdleTimeout(int connIdleTimeout) {
        this.connIdleTimeout = connIdleTimeout;
    }
}
