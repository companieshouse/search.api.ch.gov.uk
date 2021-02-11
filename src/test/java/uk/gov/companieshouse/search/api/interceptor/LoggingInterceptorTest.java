package uk.gov.companieshouse.search.api.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggingInterceptorTest {

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private LoggingInterceptor loggingInterceptor;

    @Test
    @DisplayName("Test the pre-handle")
    void testPreHandle(){
        when(httpServletRequestMock.getSession()).thenReturn(httpSession);
        boolean value = loggingInterceptor.preHandle(httpServletRequestMock,httpServletResponseMock,new Object());
        assertTrue(value);
    }

    @Test
    @DisplayName("Test the post handle")
    void testPostHandle() {
        when(httpServletRequestMock.getSession()).thenReturn(httpSession);
        when(httpServletRequestMock.getSession().getAttribute(anyString())).thenReturn(1L);
        loggingInterceptor.postHandle(httpServletRequestMock,httpServletResponseMock,new Object(), new ModelAndView());
        verify(httpServletRequestMock, times(2)).getSession();
    }
}
