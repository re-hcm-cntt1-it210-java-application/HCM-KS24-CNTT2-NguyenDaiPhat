package com.example.academic.config;
import com.example.academic.enums.Role;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override public void addInterceptors(InterceptorRegistry registry) { registry.addInterceptor(new RoleInterceptor()).addPathPatterns("/**").excludePathPatterns("/login","/register","/css/**","/h2-console/**"); }
  static class RoleInterceptor implements HandlerInterceptor {
    @Override public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
      String uri = req.getRequestURI(); Object roleObj = req.getSession().getAttribute("ROLE");
      if (uri.startsWith("/student") || uri.startsWith("/lecturer") || uri.startsWith("/admin") || uri.startsWith("/profile")) {
        if (roleObj == null) { res.sendRedirect("/login"); return false; }
        Role role = (Role) roleObj;
        if (uri.startsWith("/student") && role != Role.STUDENT) { res.sendError(403); return false; }
        if (uri.startsWith("/lecturer") && role != Role.LECTURER) { res.sendError(403); return false; }
        if (uri.startsWith("/admin") && role != Role.ADMIN) { res.sendError(403); return false; }
      }
      return true;
    }
  }
}
