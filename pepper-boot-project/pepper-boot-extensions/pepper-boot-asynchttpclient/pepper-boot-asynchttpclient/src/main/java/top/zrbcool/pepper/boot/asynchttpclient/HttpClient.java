package top.zrbcool.pepper.boot.asynchttpclient;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.reactor.IOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;
import top.zrbcool.pepper.boot.core.Loggers;
import top.zrbcool.pepper.boot.util.HttpUtil;
import top.zrbcool.pepper.boot.util.Log4j2Utils;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;


/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class HttpClient extends CloseableHttpAsyncClient implements DisposableBean {
    private static final String CONFIG_PREFIX = "** PEPPER BOOT CONFIG - ";
    private static final Logger ACCESS_LOGGER = Loggers.getAccessLogger();
    private CloseableHttpAsyncClient internalClient;
    private HttpClientProps props;
    private String namespace;
    private static final Set<HttpClient> HOLDER = Sets.newConcurrentHashSet();
    private static final Set<IOReactor> REACTOR_HOLDER = Sets.newConcurrentHashSet();
    
//    private final LatencyStat CHTTPCLIENT_STAT = LatencyProfiler.Builder.build()
//            .name("chttpclient_requests" + "_" + namespace)
//            .defineLabels("url", "route")
//            .tag("chttpclient")
//            .create();
    private final Stats CHTTPCLIENT_STAT = Profiler.Builder.builder()
            .type("httpnioclient-req")
            .subType("out")
            .namespace(namespace)
            .build();
    
//    private final LatencyStat HTTP_STAT = LatencyProfiler.Builder.build()
//            .name("app_http_outgoing_requests" + "_" + namespace)
//            .defineLabels("url", "route")
//            .tag("http:outgoing")
//            .create();
    private final Stats HTTP_STAT = Profiler.Builder.builder()
            .type("httpnioclient-http")
                .subType("out")
                .namespace(namespace)
                .build();
    
    private final Gauge CONN_STAT = Gauge.build()
            .name("app_http_outgoing_conn" +  "_" + namespace)
            .help("app_http_outgoing_conn status")
            .labelNames("route", "state")
            .register();
    
    private final Counter CHTTPCLIENT_MULTI_COUNTER = Counter.build()
            .name("chttpclient_multi_request_counter" + "_" + namespace)
            .help("chttpclient_multi_request_counter")
            .labelNames("metrics", "category")
            .register();

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService idleConnectionExecutor = Executors.newScheduledThreadPool(1);
    private ManagedNHttpClientConnectionFactory connectionFactory;
    private PoolingNHttpClientConnectionManager cm;
    private static final String PREFIX = "http:out:";

    public HttpClient(String namespace, HttpClientProps props) {
        this.namespace = namespace;
        this.props = props;
    }


    @Override
    public Future<HttpResponse> execute(HttpUriRequest request, FutureCallback<HttpResponse> callback) {
        return super.execute(request, callback);
    }

    public void init() throws IOReactorException {
        HttpAsyncClients.createDefault();
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(props.getReactorIoThreadCount())
                .setConnectTimeout(props.getReactorConnectTimeout())
                .setSoTimeout(props.getReactorSoTimeout())
                .setTcpNoDelay(true)
                //.setSelectInterval(500)
                .build();
        DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

//        ioReactor.setExceptionHandler(new IOReactorExceptionHandler() {
//            public boolean handle(IOException ex) {
//                if (ex.getCause() != null && ex.getCause() instanceof IllegalStateException) {
//                    Loggers.getFrameworkLogger().warn(HttpClient.class.getSimpleName(), ex);
//                    return true;
//                }
//
//                Loggers.getFrameworkLogger().error(HttpClient.class.getSimpleName(), ex);
//                return false;
//            }
//            public boolean handle(RuntimeException ex) {
//                Loggers.getFrameworkLogger().error(HttpClient.class.getSimpleName(), ex);
//                return false;
//            }
//
//        });


        connectionFactory = new ManagedNHttpClientConnectionFactory();
        final Registry<SchemeIOSessionStrategy> registry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", SSLIOSessionStrategy.getDefaultStrategy())
                .build();

        cm = new PoolingNHttpClientConnectionManager(ioReactor, connectionFactory, registry, null, null, props.getTimeToLive(), TimeUnit.SECONDS);

        cm.setDefaultMaxPerRoute(props.getDefaultMaxPerRoute());
        cm.setMaxTotal(props.getMaxTotal());

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(props.getConnectTimeout())
                .setSocketTimeout(props.getSocketTimeout())
                .setConnectionRequestTimeout(props.getConnectionRequestTimeout())
                .build();
        internalClient = HttpAsyncClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        HOLDER.add(this);
        REACTOR_HOLDER.add(ioReactor);
        internalClient.start();

        Log4j2Utils.putContextColumn1("config");
        Log4j2Utils.putContextColumn2("chttpclient:" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "reactorIoThreadCount", props.getReactorIoThreadCount());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "reactorConnectTimeout", props.getReactorConnectTimeout());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "reactorSoTimeout", props.getReactorSoTimeout());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "connectTimeout", props.getConnectTimeout());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "socketTimeout", props.getSocketTimeout());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "connectionRequestTimeout", props.getConnectionRequestTimeout());

        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "defaultMaxPerRoute", props.getDefaultMaxPerRoute());
        Loggers.getFrameworkLogger().info("{} {} - {}", CONFIG_PREFIX, "maxTotal", props.getMaxTotal());
        Loggers.getFrameworkLogger().info(Log4j2Utils.LINE);
        Log4j2Utils.clearContext();

        idleConnectionExecutor.scheduleAtFixedRate(() -> {
            final Logger logger = Loggers.getFrameworkLogger();
            try {
                logger.info(Log4j2Utils.LINE);
                Log4j2Utils.putContextColumn1("pool");
                Log4j2Utils.putContextColumn2("chttpclient" + ":" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
                logger.info("validatePendingRequests");
//                cm.validatePendingRequests();
                logger.info("closeExpiredConnections");
                cm.closeExpiredConnections();
//                cm.closeIdleConnections(60, TimeUnit.SECONDS);
                logger.info("closeIdleConnections");
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                logger.info(Log4j2Utils.LINE);
                Log4j2Utils.clearContext();
            }
        }, 30, 30, TimeUnit.SECONDS);

        executorService.scheduleAtFixedRate(() -> {
            connStats();
            Logger logger = Loggers.getFrameworkLogger();
            Log4j2Utils.putContextColumn1("health");
            Log4j2Utils.putContextColumn2("chttpclient" + ":" + namespace + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
            logger.info(Log4j2Utils.LINE);
            logger.info("{} : {}", "Available", cm.getTotalStats().getAvailable());
            logger.info("{} : {}", "Leased", cm.getTotalStats().getLeased());
            logger.info("{} : {}", "Pending", cm.getTotalStats().getPending());
            logger.info("{} : {}", "Max", cm.getTotalStats().getMax());
            logger.info(Log4j2Utils.LINE);
            Log4j2Utils.clearContext();
        }, 30, 30, TimeUnit.SECONDS);
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
    public Future<HttpResponse> execute(HttpUriRequest request, HttpContext context, FutureCallback<HttpResponse> callback) {
        return super.execute(request, context, callback);
    }

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, FutureCallback<T> callback) {
        return super.execute(requestProducer, responseConsumer, callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpHost target, HttpRequest request, HttpContext context, FutureCallback<HttpResponse> callback) {
        return super.execute(target, request, context, callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpHost target, HttpRequest request, FutureCallback<HttpResponse> callback) {
        return super.execute(target, request, callback);
    }

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
        return internalClient.execute(requestProducer, responseConsumer, context, callback);
    }

    public List<HttpResponse> execute(String reqId, List<HttpUriRequest> requests, int timeout, TimeUnit timeUnit) {
        Assert.isTrue(StringUtils.isNotEmpty(reqId), "must specify reqId!!!");
        Assert.isTrue(CollectionUtils.isEmpty(requests), "requests can not be empty!!!");
        long begin = System.currentTimeMillis();
        final String[] tags = new String[]{"url", "multiExecute", "route", "multiExecute"};
        CHTTPCLIENT_STAT.incConc(tags);
        ACCESS_LOGGER.info("{} start", reqId);

        List<HttpResponse> result = Lists.newArrayList();
        try {
            if (CollectionUtils.isNotEmpty(requests)) {
                CountDownLatch latch = new CountDownLatch(requests.size());
                List<Future<HttpResponse>> futures = Lists.newArrayList();
                int sub = 1;
                for (HttpUriRequest request : requests) {
                    String route = determineTarget(request);
                    String url = request.getMethod() + " " + HttpUtil.getPatternUrl(request.getURI().getPath());
                    HTTP_STAT.incConc(tags);
                    Future<HttpResponse> future = this.execute(request, new CHttpClientFutureCallback(begin, reqId + "-" + sub++, url, route, latch));
                    CHTTPCLIENT_MULTI_COUNTER.labels("submitted", "sub").inc();
                    futures.add(future);
                }
                try {
                    latch.await(timeout, timeUnit);
                } catch (InterruptedException e) {
                    /*
                     * <p>If the current thread:
                     * <ul>
                     * <li>has its interrupted status set on entry to this method; or
                     * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
                     * </ul>
                     * then {@link InterruptedException} is thrown and the current thread's
                     * interrupted status is cleared.
                     */
                    ACCESS_LOGGER.warn("");
                }

                for (Future<HttpResponse> future : futures) {
                    if (future.isDone()) {
                        try {
                            HttpResponse httpResponse = future.get();
                            if (httpResponse != null) {
                                CHTTPCLIENT_MULTI_COUNTER.labels("done", "sub").inc();
                                result.add(httpResponse);
                            } else {
                                CHTTPCLIENT_MULTI_COUNTER.labels("fail", "sub").inc();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            ACCESS_LOGGER.warn("");
                        }
                    } else {
                        CHTTPCLIENT_MULTI_COUNTER.labels("timeout", "sub").inc();
                        future.cancel(false);
                    }
                }
            }
        } finally {
            if (CollectionUtils.isEmpty(result)) {
                CHTTPCLIENT_STAT.error(tags);
                CHTTPCLIENT_MULTI_COUNTER.labels("fail", "main").inc();
            } else {
                if (CollectionUtils.size(result) < CollectionUtils.size(requests)) {
                    CHTTPCLIENT_MULTI_COUNTER.labels("partial", "main").inc();
                } else {
                    CHTTPCLIENT_MULTI_COUNTER.labels("complete", "main").inc();
                }
            }
            CHTTPCLIENT_STAT.observe(System.currentTimeMillis() - begin, tags);
            CHTTPCLIENT_STAT.decConc(tags);
        }
        return result;
    }

    class CHttpClientFutureCallback implements FutureCallback<HttpResponse> {
        private final long begin;
        private final String reqId;
        private final String url;
        private final String route;
        private final CountDownLatch latch;
        private final String[] tags;

        public CHttpClientFutureCallback(long begin, String reqId, String url, String route, CountDownLatch latch) {
            this.begin = begin;
            this.reqId = reqId;
            this.url = url;
            this.route = route;
            this.latch = latch;
            this.tags = new String[]{"url", url, "route", route};
        }

        @Override
        public void completed(HttpResponse httpResponse) {
            ACCESS_LOGGER.info(
                    "{} completed, with status code: {}, cost: {}ms",
                    reqId,
                    httpResponse != null ? httpResponse.getStatusLine().getStatusCode() : -1,
                    System.currentTimeMillis() - begin
            );
            HTTP_STAT.observe(System.currentTimeMillis() - begin, tags);
            HTTP_STAT.decConc(tags);
            if (latch != null)
                latch.countDown();
        }

        @Override
        public void failed(Exception ex) {
            ACCESS_LOGGER.info(
                    "{} failed, with exception: {}, cost: {}ms",
                    reqId,
                    ex != null ? ex.toString() : null,
                    System.currentTimeMillis() - begin
            );
            HTTP_STAT.observe(System.currentTimeMillis() - begin, tags);
            HTTP_STAT.decConc(tags);
            HTTP_STAT.error(tags);
            if (latch != null)
                latch.countDown();
        }

        @Override
        public void cancelled() {
            ACCESS_LOGGER.info(
                    "{} cancelled, cost: {}ms",
                    reqId,
                    System.currentTimeMillis() - begin
            );
            HTTP_STAT.observe(System.currentTimeMillis() - begin, tags);
            HTTP_STAT.decConc(tags);
            HTTP_STAT.error(tags);
            if (latch != null)
                latch.countDown();
        }
    }


    public HttpResponse execute(String reqId, HttpUriRequest request, int timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException, BlockException {
        Entry methodEntry = null;
        Assert.isTrue(StringUtils.isNotEmpty(reqId), "must specify reqId!!!");
        long begin = System.currentTimeMillis();
        ACCESS_LOGGER.info("{} start", reqId);
        String route = determineTarget(request);
        String url = request.getMethod() + " " + HttpUtil.getPatternUrl(request.getURI().getPath());
        HttpResponse httpResponse = null;
        final String[] tags = new String[]{"url", url, "route", route};
        CHTTPCLIENT_STAT.incConc(tags);
        HTTP_STAT.incConc(tags);

        try {
            ContextUtil.enter("Http::Out");
            methodEntry = SphU.entry(PREFIX + route + ":" + url);
            Future<HttpResponse> future = super.execute(request, new CHttpClientFutureCallback(begin, reqId, url, route, null));
            try {
                httpResponse = future.get(timeout, timeUnit);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Tracer.trace(e);
                CHTTPCLIENT_STAT.error(tags);
                future.cancel(false);
                throw e;
            }
        } finally {
            if (methodEntry != null) {
                methodEntry.exit();
            }
            ContextUtil.exit();
            ACCESS_LOGGER.info(
                    "{} return, with status code: {}, cost: {}ms",
                    reqId,
                    httpResponse != null ? httpResponse.getStatusLine().getStatusCode() : -1,
                    System.currentTimeMillis() - begin
            );
            CHTTPCLIENT_STAT.observe(System.currentTimeMillis() - begin, tags);
            CHTTPCLIENT_STAT.decConc(tags);
        }
        return httpResponse;
    }

    public CloseableHttpAsyncClient getInternalClient() {
        return internalClient;
    }

    public void setInternalClient(CloseableHttpAsyncClient internalClient) {
        this.internalClient = internalClient;
    }

    public HttpClientProps getProps() {
        return props;
    }

    public void setProps(HttpClientProps props) {
        this.props = props;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean isRunning() {
        return internalClient.isRunning();
    }

    @Override
    public void start() {
        internalClient.start();
    }

    @Override
    public void close() throws IOException {
        internalClient.close();
    }

    @Override
    public void destroy() throws Exception {
        this.internalClient.close();
    }

    public static boolean allRunning() {
        for (HttpClient cHttpClient : HOLDER) {
            if (!cHttpClient.isRunning()) {
                return false;
            }
        }
        return true;
    }

    public static boolean readyForRequest() {
        for (IOReactor ioReactor : REACTOR_HOLDER) {
            if (ioReactor.getStatus() != IOReactorStatus.ACTIVE) {
                return false;
            }
        }
        return allRunning();
    }

    public static String determineTarget(final HttpUriRequest request) {
        Args.notNull(request, "HTTP request");
        // A null target may be acceptable if there is a default target.
        // Otherwise, the null target is detected in the director.
        HttpHost target = null;
        final URI requestURI = request.getURI();
        if (requestURI.isAbsolute()) {
            target = URIUtils.extractHost(requestURI);
            if (target == null) {
                return "unKnown";
            }
        }
        return target.getHostName();
    }
}
