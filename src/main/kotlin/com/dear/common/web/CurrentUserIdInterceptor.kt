package com.dear.common.web

import com.dear.common.exception.CommonErrorType
import com.dear.common.exception.ForbiddenException
import com.dear.common.exception.UnauthorizedException
import com.dear.user.domain.UserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Reads the X-User-Id header, resolves the user via cache, exposes a
 * CurrentUserId as a request attribute, and enforces ADMIN role on
 * the /admin/ path prefix.
 */
@Component
class CurrentUserIdInterceptor(
    private val resolver: CurrentUserResolver,
) : HandlerInterceptor {

    override fun preHandle(req: HttpServletRequest, res: HttpServletResponse, handler: Any): Boolean {
        val raw = req.getHeader(X_USER_ID_HEADER)
            ?: throw UnauthorizedException(CommonErrorType.MISSING_AUTH_HEADER)
        val id = raw.toLongOrNull()
            ?: throw UnauthorizedException(CommonErrorType.INVALID_AUTH_HEADER, detail = "value=$raw")
        val currentUserId = resolver.resolve(id)

        if (req.requestURI.startsWith(ADMIN_PATH_PREFIX) && currentUserId.role != UserRole.ADMIN) {
            throw ForbiddenException(detail = "ADMIN role required for ${req.requestURI}")
        }

        req.setAttribute(ATTR_CURRENT_USER_ID, currentUserId)
        return true
    }

    companion object {
        const val X_USER_ID_HEADER = "X-User-Id"
        const val ATTR_CURRENT_USER_ID = "dear.currentUserId"
        const val ADMIN_PATH_PREFIX = "/admin/"
    }
}
