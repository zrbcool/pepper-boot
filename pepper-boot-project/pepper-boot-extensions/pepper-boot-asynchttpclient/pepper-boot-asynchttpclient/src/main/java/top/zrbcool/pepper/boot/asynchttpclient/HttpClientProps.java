package top.zrbcool.pepper.boot.asynchttpclient;

/**
 * Created by zhangrongbin on 2019/4/26.
 */
public class HttpClientProps {
    /**
     *  Returns the timeout in milliseconds used when requesting a connection from the connection manager.
     */
    private int connectionRequestTimeout = 100;
    /**
     *  Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
     */
    private int socketTimeout = 2000;
    /**
     * Determines the timeout in milliseconds until a connection is established.
     */
    private int connectTimeout = 2000;
    private int defaultMaxPerRoute = 50;
    private int maxTotal = defaultMaxPerRoute * 10;

    private int reactorIoThreadCount = Runtime.getRuntime().availableProcessors() > 8 ? 8 : Runtime.getRuntime().availableProcessors();
    private int reactorConnectTimeout = 1000;
    private int reactorSoTimeout = 1000;

    private int timeToLive = 60;

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

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

    public int getReactorIoThreadCount() {
        return reactorIoThreadCount;
    }

    public void setReactorIoThreadCount(int reactorIoThreadCount) {
        this.reactorIoThreadCount = reactorIoThreadCount;
    }

    public int getReactorConnectTimeout() {
        return reactorConnectTimeout;
    }

    public void setReactorConnectTimeout(int reactorConnectTimeout) {
        this.reactorConnectTimeout = reactorConnectTimeout;
    }

    public int getReactorSoTimeout() {
        return reactorSoTimeout;
    }

    public void setReactorSoTimeout(int reactorSoTimeout) {
        this.reactorSoTimeout = reactorSoTimeout;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }
}
