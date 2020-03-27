package top.zrbcool.pepper.boot.motan;


import com.pepper.metrics.core.extension.Scope;
import com.pepper.metrics.core.extension.Spi;
/**
 * @author zhangrongbincool@163.com
 * @version 19-11-7
 */
@Spi(scope = Scope.SINGLETON)
public interface MotanExtRegister {
    public String name();
}
