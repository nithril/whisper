package demo.configuration;

import demo.service.UsersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nlabrot on 08/01/15.
 */
@Configuration
public class FilterConfiguration {

    @Autowired
    private UsersManager usersManager;

    @Bean
    public Filter security(){
        return new GenericFilterBean() {

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

                HttpServletRequest servletRequest = (HttpServletRequest) request;
                HttpServletResponse servletResponse = (HttpServletResponse) response;

                if (!servletRequest.getRequestURI().startsWith("/ui/")){
                    chain.doFilter(request , response);
                    return;
                }

                String sessionId = servletRequest.getSession(true).getId();

                if (!usersManager.findUserBySessionId(sessionId).isPresent()){
                    servletResponse.sendError(401);
                }else{
                    chain.doFilter(request , response);
                }
            }

        };
    }
}
