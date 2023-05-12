package usw.suwiki.global.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import usw.suwiki.global.annotation.JWTVerify;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtAgent jwtAgent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            JWTVerify annotation = AnnotationUtils.findAnnotation(method, JWTVerify.class);
            if (annotation != null) {
                String token = request.getHeader("Authorization");
                if (annotation.option().equals("ADMIN")) {
                    if (jwtAgent.getUserRole(token).equals("ADMIN")) {
                        return true;
                    }
                    throw new AccountException(USER_RESTRICTED);
                }
                jwtAgent.validateJwt(token);
            }
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) throws Exception {

    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler, Exception ex
    ) throws Exception {
    }
}
