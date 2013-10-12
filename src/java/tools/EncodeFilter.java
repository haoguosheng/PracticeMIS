/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 *
 * @author Idea
 */
@WebFilter(filterName = "encodeFilter", urlPatterns = {"/faces/*"})
public class EncodeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    public EncodeFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        try {
            request.setCharacterEncoding("UTF-8");
           // response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        } catch (Throwable t) {
        }
    }
}
