package com.dear.common.web

import com.dear.common.exception.CommonErrorType
import com.dear.common.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * Injects the request-scoped `CurrentUserId` (set by `CurrentUserIdInterceptor`)
 * into controller method parameters typed as `CurrentUserId`.
 */
@Component
class CurrentUserIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == CurrentUserId::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val req = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("HttpServletRequest unavailable")
        return req.getAttribute(CurrentUserIdInterceptor.ATTR_CURRENT_USER_ID) as? CurrentUserId
            ?: throw UnauthorizedException(CommonErrorType.MISSING_AUTH_HEADER)
    }
}
