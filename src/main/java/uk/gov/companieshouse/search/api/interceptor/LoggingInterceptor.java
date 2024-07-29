package uk.gov.companieshouse.search.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

import uk.gov.companieshouse.logging.util.RequestLogger;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;

@Component
public class LoggingInterceptor implements HandlerInterceptor, RequestLogger {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        logStartRequestProcessing(request, LoggingUtils.getLogger());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {
        logEndRequestProcessing(request, response, LoggingUtils.getLogger());
    }
}
