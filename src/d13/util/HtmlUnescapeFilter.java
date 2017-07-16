package d13.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlUnescapeFilter implements Filter {
    
    static class UnescapedRequest extends HttpServletRequestWrapper {
        
        UnescapedRequest (ServletRequest request) {
            super((HttpServletRequest)request);
        }

        @Override public String getParameter (String name) {
            return unescape(super.getParameter(name));
        }

        @Override public Map<String, String[]> getParameterMap() {
            Map<String,String[]> map = super.getParameterMap();
            if (map != null && !map.isEmpty()) {
                Map<String,String[]> umap = new LinkedHashMap<String,String[]>();
                for (Map.Entry<String,String[]> entry : map.entrySet()) {
                    //System.out.printf("%s => %s => ", entry.getKey(), Arrays.toString(entry.getValue()));
                    umap.put(entry.getKey(), unescape(entry.getValue()));
                    //System.out.printf("%s\n", Arrays.toString(umap.get(entry.getKey())));
                }
                map = umap;
            }
            return map;
        }

        @Override public String[] getParameterValues (String name) {
            return unescape(super.getParameterValues(name));
        }
        
        static String unescape (String str) {
            if (str == null)
                return null;
            else
                return StringEscapeUtils.unescapeHtml4(str);
        }
        
        static String[] unescape (String[] strs) {
            if (strs != null && strs.length > 0) {
                String[] ustrs = new String[strs.length];
                for (int n = 0; n < strs.length; ++ n)
                    ustrs[n] = unescape(strs[n]);
                strs = ustrs;
            }
            return strs;
        }
        
    }

    @Override public void destroy () {
    }

    @Override public void init (FilterConfig config) throws ServletException {
    }

    @Override public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) 
        throws IOException, ServletException 
    {
        if (request.getParameterMap().isEmpty())
            chain.doFilter(request, response);
        else
            chain.doFilter(new UnescapedRequest(request), response);
    }

}
