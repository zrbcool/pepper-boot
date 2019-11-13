package top.zrbcool.pepper.boot.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.util.Args;

import java.net.URI;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-11
 */
public class HttpClientUtil {

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
