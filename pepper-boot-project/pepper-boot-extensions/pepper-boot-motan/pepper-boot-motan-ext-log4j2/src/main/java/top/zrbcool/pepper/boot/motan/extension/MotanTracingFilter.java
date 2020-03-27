package top.zrbcool.pepper.boot.motan.extension;

import com.weibo.api.motan.common.URLParamType;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static top.zrbcool.pepper.boot.util.Log4j2Utils.CONTEXT_COLUMN_1;
import static top.zrbcool.pepper.boot.util.Log4j2Utils.CONTEXT_COLUMN_2;


/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
@SpiMeta(name = MotanTracingFilter.NAME)
@Activation(sequence = 30)
public class MotanTracingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(MotanTracingFilter.class);
    public static final String NAME = "pepperTracing";
    public static final String LABEL_TRACE_ID = CONTEXT_COLUMN_1;
    public static final String LABEL_TRACE_SOURCE = CONTEXT_COLUMN_2;

    @Override
    public Response filter(Caller<?> caller, Request request) {
        //String hostAddress = NetUtils.getLocalAddress().getHostAddress();
        if (caller instanceof Provider) {
            // comes from remote
            String source = request.getAttachments().get(URLParamType.host.getName());
            String traceId = request.getAttachments().get(LABEL_TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                traceId = System.currentTimeMillis() + "";
            }
            ThreadContext.put(LABEL_TRACE_ID, traceId);
            ThreadContext.put(LABEL_TRACE_SOURCE, source);
        } else {
            // go to call remote
            String traceId = ThreadContext.get(LABEL_TRACE_ID);
            if (StringUtils.isNotEmpty(traceId)) {
                request.getAttachments().put(LABEL_TRACE_ID, traceId);
            }
        }
        return caller.call(request);
    }

}
