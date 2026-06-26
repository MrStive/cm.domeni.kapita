package com.domeni.kapita.api.interceptor;

import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.SubscriptionStatusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class SubscriptionAccessHandlerInterceptor implements HandlerInterceptor {

  private final SubscriptionStatusService subscriptionStatusService;
  private final CurrentUserProvider currentUserProvider;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String method = request.getMethod();
    if (HttpMethod.GET.matches(method)
        || HttpMethod.OPTIONS.matches(method)
        || HttpMethod.HEAD.matches(method)) {
      return true;
    }

    var userId =
        currentUserProvider
            .getCurrentUserId()
            .orElseThrow(
                () -> new IllegalStateException("current user id is unavailable"));
    subscriptionStatusService.validateWriteAccess(userId);
    return true;
  }
}
