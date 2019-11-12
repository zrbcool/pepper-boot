package top.zrbcool.pepper.boot.sentinel;


import javax.servlet.*;
import java.io.IOException;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-12
 */
public class SentinelHttpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}