package uk.gov.companieshouse.search.api.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter implements RequestLogger {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        logStartRequestProcessing(request, LOG);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {
        logEndRequestProcessing(request, response, LOG);
    }
}
