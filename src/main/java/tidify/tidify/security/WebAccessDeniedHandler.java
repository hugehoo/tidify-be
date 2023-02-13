package tidify.tidify.security;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class WebAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        if (exception instanceof AccessDeniedException) { // 이 라인 꼭 필요한가? 무의미해보이는데.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                SecurityUser securityUser = (SecurityUser)auth.getPrincipal();
                Set<RoleType> roleTypes = securityUser.getRoleTypes();
                if (!roleTypes.isEmpty()) {
                    request.setAttribute("msg", "접근 권한이 없습니다.");
                    if (roleTypes.contains(RoleType.ROLE_VIEW)) {
                        request.setAttribute("nextPage", "/v");
                    }
                }
            }
        }
    }
}
