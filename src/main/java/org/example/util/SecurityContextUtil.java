package org.example.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.common.context.SecurityContext;
import org.example.util.security.JwtTokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SecurityContextUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityContextUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public SecurityContext getSecurityContext() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        String token = jwtTokenProvider.resolveToken(request);

        return new SecurityContext(StringUtils.hasText(jwtTokenProvider.getUserId(token)) && StringUtils.hasText(jwtTokenProvider.getUsername(token)));
    }
}
