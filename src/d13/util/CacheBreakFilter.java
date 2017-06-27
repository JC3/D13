package d13.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CacheBreakFilter implements Filter {
    
    @Override public void destroy () {
    }

    @Override public void init (FilterConfig config) throws ServletException {
    }

    @Override public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) 
        throws IOException, ServletException 
    {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest r = (HttpServletRequest)request;
            String olduri = r.getServletPath();
            String newuri = olduri.replaceAll("/v/[^/]*/", "/");
            request.getRequestDispatcher(newuri).forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
        
    }

}
