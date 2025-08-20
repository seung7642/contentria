package com.contentria.api.utils

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component("ipResolver")
class IpResolver {

    /**
     * Returns the actual client IP address considering reverse proxy environments.
     */
    fun getClientIp(request: HttpServletRequest): String {
        val headersToCheck = listOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        )

        for (header in headersToCheck) {
            val ipAddress = request.getHeader(header)
            if (ipAddress != null && ipAddress.isNotBlank() && !"unknown".equals(ipAddress, ignoreCase = true)) {
                // 여러 IP가 콤마로 구분되어 올 수 있으므로 첫 번째 유효한 IP를 사용
                return ipAddress.split(",").first().trim()
            }
        }

        return request.remoteAddr
    }
}