package top.zrbcool.pepper.boot.httpclient;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import io.prometheus.client.Gauge;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;
import top.zrbcool.pepper.boot.core.Loggers;
import top.zrbcool.pepper.boot.core.ThreadFactory;
import top.zrbcool.pepper.boot.util.HttpUtil;
import top.zrbcool.pepper.boot.util.Log4j2Utils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class HttpBioClient extends CloseableHttpClient implements DisposableBean {
    private static final String CONFIG_PREFIX = "** PEPPER BOOT CONFIG - ";
    private static String PREFIX = HttpBioClient.class.getSimpleName();
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactory("httpclient-bio-"));
    private static final ScheduledExecutorService idleConnectionExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory("httpclient-bio-"));
    private ThreadPoolExecutor EXECUTOR;
    private static final Logger ACCESS_LOGGER = Loggers.getAccessLogger();
    private CloseableHttpClient internalClient;
    private PoolingHttpClientConnectionManager cm;
    private final HttpBioClientProps props;
    private String namespace;
    private final Gauge CONN_STAT;
    private final Stats CHTTPCLIENT_STAT;
    private final Stats HTTP_STAT;
    private static final ResponseHandler<HttpResponse> DEFAULT_RESPONSE_HANDLER = new ResponseHandler<HttpResponse>() {
        @Override
        public HttpResponse handle(HttpResponse httpResponse) {
            return httpResponse;
        }
    };

    public HttpBioClient(String namespace, HttpBioClientProps cHttpBioClientProps) {
        this.props = cHttpBioClientProps;
        this.namespace = namespace;
        CONN_STAT = Gauge.build()
                .name("app_http_bio_outgoing_conn" +  "_" + namespace)
                .help("app_http_bio_outgoing_conn status")
                .labelNames("route", "state")
                .register();
//        CHTTPCLIENT_STAT = LatencyProfiler.Builder.build()
//                .name("chttpbioclient_requests" + "_" + namespace)
//                .defineLabels("url", "route")
//                .tag("chttpbioclient")
//                .create();
        CHTTPCLIENT_STAT = Profiler.Builder.builder()
                .type("httpclient-req")
                .subType("out")
                .namespace(namespace)
                .build();
//        HTTP_STAT = LatencyProfiler.Builder.build()
//                .name("app_http_bio_outgoing_requests" + "_" + namespace)
//                .defineLabels("url", "route")
//                .tag("http:outgoing")
//                .create();
        HTTP_STAT = Profiler.Builder.builder()
                .type("httpclient-http")
                .subType("out")
                .namespace(namespace)
                .build();
    }

    public void init() {
        final boolean proxyMode = "true".equalsIgnoreCase(System.getProperty("proxyMode", "false"));
        final String proxyHost = System.getProperty("proxyHost", "anyproxy");
        final String proxyPort = System.getProperty("proxyPort", "8001");
        final BlockingQueue<Runnable> workQueue =
                props.getCapacity() > 0 ?
                        new ArrayBlockingQueue<>(props.getCapacity()) :
                        new SynchronousQueue<>();
        EXECUTOR = new ThreadPoolExecutor(
                props.getCorePoolSize(),
                props.getMaximumPoolSize(),
                props.getKeepAliveTime(),
                TimeUnit.SECONDS,
                workQueue,
                new ThreadFactory("httpclient-bio-"),
                new ThreadPoolExecutor.AbortPolicy()
        );
        EXECUTOR.allowCoreThreadTimeOut(true);
        EXECUTOR.prestartAllCoreThreads();

        final SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(props.getSoTimeout())
                .setTcpNoDelay(true)
                .build();

        cm = getPoolingHttpClientConnectionManager(proxyMode);
        cm.setDefaultMaxPerRoute(props.getDefaultMaxPerRoute());
        cm.setMaxTotal(props.getMaxTotal());
        cm.setValidateAfterInactivity(props.getValidateAfterInactivity());
        cm.setDefaultSocketConfig(socketConfig);

        executorService.scheduleAtFixedRate(() -> {
            connStats();
            Logger logger = Loggers.getFrameworkLogger();
            Log4j2Utils.putContextColumn1("health");
            Log4j2Utils.putContextColumn2("chttpclient" + ":" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
            logger.info(Log4j2Utils.LINE);
            logger.info("{} = {} | {} = {} | {} = {}", "proxyMode", proxyMode, "proxyHost", proxyHost, "proxyPort", proxyPort);
            logger.info("{} | {} | {}", "Available", "Leased", "Pending");
            logger.info("{} | {} | {}", cm.getTotalStats().getAvailable(), cm.getTotalStats().getLeased(), cm.getTotalStats().getPending());
            logger.info("{} | {} | {} | {}", "CorePoolSize", "MaximumPoolSize", "PoolSize", "LargestPoolSize");
            logger.info("{} | {} | {} | {}", EXECUTOR.getCorePoolSize(), EXECUTOR.getMaximumPoolSize(), EXECUTOR.getPoolSize(), EXECUTOR.getLargestPoolSize());
            logger.info("{} | {} | {} | {}", "ActiveCount", "CompletedTaskCount", "TaskCount", "QueueSize");
            logger.info("{} | {} | {} | {}", EXECUTOR.getActiveCount(), EXECUTOR.getCompletedTaskCount(), EXECUTOR.getTaskCount(), EXECUTOR.getQueue().size());
            logger.info(Log4j2Utils.LINE);
            Log4j2Utils.clearContext();
        }, 30, 30, TimeUnit.SECONDS);

        final HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(cm)
                .setConnectionManagerShared(false)
                //.setUserAgent(this.config.getUserAgent())
                //.disableContentCompression()
                //.evictExpiredConnections()
                .disableAutomaticRetries();
        if (proxyMode) {
            clientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
        }

        internalClient = clientBuilder
                .build();

        idleConnectionExecutor.scheduleAtFixedRate(() -> {
            final Logger logger = Loggers.getFrameworkLogger();
            try {
                logger.info(Log4j2Utils.LINE);
                Log4j2Utils.putContextColumn1("pool");
                Log4j2Utils.putContextColumn2("httpclient" + ":" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
                logger.info("closeExpiredConnections");
                cm.closeExpiredConnections();
                logger.info("closeIdleConnections");
                cm.closeIdleConnections(props.getConnIdleTimeout(), TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                logger.info(Log4j2Utils.LINE);
                Log4j2Utils.clearContext();
            }
        }, 30, 30, TimeUnit.SECONDS);

        Log4j2Utils.putContextColumn1("config");
        Log4j2Utils.putContextColumn2("httpclient:" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "defaultMaxPerRoute", props.getDefaultMaxPerRoute());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "maxTotal", props.getMaxTotal());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "soTimeout", props.getSoTimeout());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "validateAfterInactivity", props.getValidateAfterInactivity());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "corePoolSize", props.getCorePoolSize());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "maximumPoolSize", props.getMaximumPoolSize());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "capacity", props.getCapacity());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "keepAliveTime", props.getKeepAliveTime());
        Loggers.getFrameworkLogger().info(Log4j2Utils.LINE);
        Log4j2Utils.clearContext();


    }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(boolean proxyMode) {
        if (proxyMode) {
            System.setProperty("jsse.enableSNIExtension", "false");
            SSLContext sslContext = null;
            try {
                sslContext = SSLContexts.custom()
                        .loadTrustMaterial((chain, authType) -> true).build();
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ignored) {
            }

            assert sslContext != null;
            SSLConnectionSocketFactory sslConnectionSocketFactory =
                    new SSLConnectionSocketFactory(sslContext, new String[]
                            {"SSLv2Hello", "SSLv3", "TLSv1","TLSv1.1", "TLSv1.2" }, null,
                            NoopHostnameVerifier.INSTANCE);
            return new PoolingHttpClientConnectionManager(RegistryBuilder.
                    <ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build());
        } else {
            return new PoolingHttpClientConnectionManager();
        }
    }

    private void connStats() {
        for (HttpRoute route : cm.getRoutes()) {
            PoolStats stats = cm.getStats(route);
            String hostName = route.getTargetHost().getHostName();
            CONN_STAT.labels(hostName, "Available").set(stats.getAvailable());
            CONN_STAT.labels(hostName, "Leased").set(stats.getLeased());
            CONN_STAT.labels(hostName, "Max").set(stats.getMax());
            CONN_STAT.labels(hostName, "Pending").set(stats.getPending());
        }
    }

    @Override
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        return internalClient.execute(target, request, context);
    }

    public static abstract class ResponseHandler<T> {
        abstract public T handle(HttpResponse httpResponse);
    }

    class HttpExecutionTask<V> implements Callable<V> {
        private ResponseHandler<V> responseHandler;
        private HttpUriRequest request;
        private final long begin;
        private final String reqId;
        private final String url;
        private final String route;

        public HttpExecutionTask(HttpUriRequest request, long begin, String reqId, String url, String route, ResponseHandler<V> responseHandler) {
            this.responseHandler = responseHandler;
            this.request = request;
            this.begin = begin;
            this.reqId = reqId;
            this.url = url;
            this.route = route;
        }

        @Override
        public V call() throws IOException {
            final String[] tags = new String[]{"url", url, "route", route};
            HTTP_STAT.incConc(tags);
            long httpBegin = System.currentTimeMillis();
            ACCESS_LOGGER.info("{}|{}|{}|before", PREFIX, reqId, System.currentTimeMillis() - begin);
            CloseableHttpResponse httpResponse = null;
            V response = null;
            try {
                httpResponse = internalClient.execute(request);
                ACCESS_LOGGER.info("{}|{}|{}|after|{}", PREFIX, reqId, System.currentTimeMillis() - begin, httpResponse != null ? httpResponse.getStatusLine().getStatusCode() : -1);
                response = responseHandler.handle(httpResponse);
                ACCESS_LOGGER.info("{}|{}|{}|handle", PREFIX, reqId, System.currentTimeMillis() - begin);
            } finally {
                if (httpResponse == null) {
                    ACCESS_LOGGER.info("{}|{}|{}|abort", PREFIX, reqId, System.currentTimeMillis() - begin);
                    HTTP_STAT.error(tags);
                } else {
                    if (!(response instanceof HttpResponse)) {
                        EntityUtils.consumeQuietly(httpResponse.getEntity());
                    }
                }
                HTTP_STAT.observe(System.currentTimeMillis() - httpBegin, tags);
                HTTP_STAT.decConc(tags);
            }
            return response;
        }
    }

    public HttpResponse execute(String reqId, HttpUriRequest request, int timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException, BlockException {
        return this.execute(reqId, request, timeout, timeUnit, DEFAULT_RESPONSE_HANDLER);
    }

    public <T> T execute(String reqId, HttpUriRequest request, int timeout, TimeUnit timeUnit, ResponseHandler<T> handler) throws InterruptedException, ExecutionException, TimeoutException, BlockException {
        Assert.isTrue(StringUtils.isNotEmpty(reqId), "must specify reqId!!!");
        long begin = System.currentTimeMillis();
        ACCESS_LOGGER.info("{}|{}|{}|start", PREFIX, reqId, 0);
        String route = HttpClientUtil.determineTarget(request);
        String url = request.getMethod() + " " + HttpUtil.getPatternUrl(request.getURI().getPath());
        Entry entry = null;
        Entry methodEntry = null;
        T httpResponse;
        final String[] tags = new String[]{"url", url, "route", route};
        CHTTPCLIENT_STAT.incConc(tags);
        try {
            entry = SphU.entry("Http::Bio::Out");
            methodEntry = SphU.entry(PREFIX + ":" + route + ":" + url);
            Future<T> future = EXECUTOR.submit(
                    new HttpExecutionTask<>(request, begin, reqId, url, route, handler)
            );
            httpResponse = future.get(timeout, timeUnit);
        } catch (InterruptedException e) {
            Tracer.trace(e);
            request.abort();
            CHTTPCLIENT_STAT.error(tags);
            ACCESS_LOGGER.error(
                    "{}|{}|{}|fail, with exception: {}",
                    PREFIX,
                    reqId,
                    System.currentTimeMillis() - begin,
                    e.getMessage()
            );
            throw e;
        } catch (ExecutionException e) {
            Tracer.trace(e);
            request.abort();
            CHTTPCLIENT_STAT.error(tags);
            ACCESS_LOGGER.error(
                    "{}|{}|{}|fail, with exception {} - {}",
                    PREFIX,
                    reqId,
                    System.currentTimeMillis() - begin,
                    ExecutionException.class.getSimpleName(),
                    e.getCause().getMessage()
            );
//            ACCESS_LOGGER.error("", e);
            throw e;
        } catch (TimeoutException e) {
            Tracer.trace(e);
            request.abort();
            CHTTPCLIENT_STAT.error(tags);
            throw e;
        } finally {
            if (methodEntry != null) {
                methodEntry.exit();
            }
            if (entry != null) {
                entry.exit();
            }
            ACCESS_LOGGER.info(
                    "{}|{}|{}|return",
                    PREFIX,
                    reqId,
                    System.currentTimeMillis() - begin
            );
            CHTTPCLIENT_STAT.observe(System.currentTimeMillis() - begin, tags);
            CHTTPCLIENT_STAT.decConc(tags);
        }
        return httpResponse;
    }

    public void closeResponseSilently(HttpResponse httpResponse) {
        if (httpResponse == null)
            return;
        if (httpResponse instanceof CloseableHttpResponse) {
            try {
                ((CloseableHttpResponse) httpResponse).close();
            } catch (IOException ioe) {
                /* silently close the response. */
            }
        }
    }

    @Override
    public void close() throws IOException {
        internalClient.close();
    }

    @Override
    public HttpParams getParams() {
        return internalClient.getParams();
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return internalClient.getConnectionManager();
    }

    @Override
    public void destroy() throws Exception {
        internalClient.close();
    }

    public static boolean allRunning() {
        return true;
    }

    public static boolean readyForRequest() {
        return allRunning();
    }
}
