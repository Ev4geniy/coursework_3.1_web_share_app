package my.cursework.controller;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

public class AuthFilter implements Filter {

    private ServletContext context;

    public void init(FilterConfig config) throws ServletException {
        this.context = config.getServletContext();
        this.context.log("AuthenticationFilter initialized");
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        System.out.println("PATH: " + path);

        String[] values = {"/login", "/register", "/listpublic"};
        boolean contains = Arrays.stream(values).anyMatch(path::equals);
        System.out.println("FILTER:CONTAINS_VAL " + contains);

        // if(!excludedUrls.contains(path))
        // {
        //     // Authenticate the request through LDAP
        //     System.out.println("Authenticating the request through LDAP");
        // }

        HttpSession session = req.getSession(false);
        if (session == null && !contains) {
            System.out.println("Unauthorized access request");
            res.sendRedirect(req.getContextPath() + "/login");
        } else {
            // pass the request along the filter chain
            chain.doFilter(req, res);
        }
    }
}
