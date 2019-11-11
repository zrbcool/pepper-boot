package top.zrbcool.pepper.boot.motan.extension;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.pepper.metrics.integration.motan.MotanUtils;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.exception.MotanBizException;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.*;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
@SpiMeta(name = MotanSentinelFilter.NAME)
@Activation(sequence = 100)
public class MotanSentinelFilter implements Filter {
    public static final String NAME = "sentinelProfiler";
    private static final String PREFIX = "motan:in:";
    private static final String FALLBACK_POSTFIX = "Fallback";
    private static final String BLOCK_POSTFIX = "Block";
    @Override
    public Response filter(Caller<?> caller, Request request) {
//        if (!MonitorConfig.DYNAMIC_ENABLE_MOTAN_SENTINEL) {
//            return caller.call(request);
//        }
        Entry methodEntry = null;
        Entry motanEntry = null;
        if (caller instanceof Provider && request instanceof DefaultRequest) {
            final String category = MotanUtils.getShortName(request.getInterfaceName());
            final String metrics = category + "." + request.getMethodName() + "(" + MotanUtils.getShortName(request.getParamtersDesc()) + ")";
            try {
                motanEntry = SphU.entry("Motan::In");
                methodEntry = SphU.entry(PREFIX + metrics);
                return caller.call(request);
            } catch (BlockException ex) {
                String methodName;
                if (ex instanceof DegradeException) {
                    methodName = request.getMethodName() + FALLBACK_POSTFIX;
                } else {
                    methodName = request.getMethodName() + BLOCK_POSTFIX;
                }

                if (((Provider<?>) caller).lookupMethod(methodName, request.getParamtersDesc()) != null) {
                    ((DefaultRequest) request).setMethodName(methodName);
                    return caller.call(request);
                } else {
                    DefaultResponse response = new DefaultResponse();
                    response.setException(new MotanBizException("request blocked by sentinel!"));
                    return response;
//                    throw new MotanServiceException("request blocked!", ex);
                }
            } finally {
                if (methodEntry != null)
                    methodEntry.exit();
                if (motanEntry != null)
                    motanEntry.exit();
            }
        }
        return caller.call(request);
    }


}
