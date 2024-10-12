package lab4.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lab4.connection.RequestAttribute;
import lab4.dotslogic.interfaces.IValidator;
import lab4.security.jwt.service.IJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final String BEARER_HEADER = "Authorization";

    @Autowired
    private IJwtService jwtService;

    @Autowired
    private IValidator<String> tokenValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTFromRequest(request);
        if (tokenValidator.validate(token)) {
            Long userId = jwtService.getUserIdFromToken(token);
            request.setAttribute(RequestAttribute.USER_ID.getName(), userId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        return request.getHeader(BEARER_HEADER);
    }
}
