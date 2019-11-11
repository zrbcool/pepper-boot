package top.zrbcool.pepper.boot.motan.extension;

import com.pepper.metrics.core.extension.SpiMeta;
import top.zrbcool.pepper.boot.motan.MotanExtRegister;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
@SpiMeta(name = MotanTracingFilter.NAME)
public class MotanTracingFilterRegister implements MotanExtRegister {
    @Override
    public String name() {
        return MotanTracingFilter.NAME;
    }
}
