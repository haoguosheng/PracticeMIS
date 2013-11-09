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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author PnP
 */
@WebFilter(filterName = "authFilter", urlPatterns = {"/faces/operation/*"})
public class AuthFilter implements Filter {

    public AuthFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        try {
            
            HttpSession session = ((HttpServletRequest) request).getSession(true);
            HttpServletResponse response1 = (HttpServletResponse) response;
            if (null == session.getAttribute("myUser")) {
                response1.sendRedirect(StaticFields.loginURL+"/faces/login/login.xhtml");
            }
            chain.doFilter(request, response);
        } catch (IOException | ServletException t) {
            
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
