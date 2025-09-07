package com.contentria.api.utils

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component("ipResolver")
class IpResolver {

    fun getClientIp(request: HttpServletRequest): String? {
        var ipAddress = request.getHeader("X-Forwarded-For")
        if (ipAddress.isNullOrBlank() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("Proxy-Client-IP")
        }
        if (ipAddress.isNullOrBlank() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ipAddress.isNullOrBlank() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.remoteAddr
        }
        // X-Forwarded-For 헤더는 여러 IP가 콤마로 구분되어 올 수 있으므로 첫 번째 IP를 사용
        return ipAddress?.split(",")?.firstOrNull()?.trim()
    }
}