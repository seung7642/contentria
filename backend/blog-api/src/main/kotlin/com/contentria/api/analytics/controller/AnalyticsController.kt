package com.contentria.api.analytics.controller

import com.contentria.api.analytics.application.AnalyticsFacade
import com.contentria.api.global.util.IpResolver
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/analytics")
class AnalyticsController(
    private val analyticsFacade: AnalyticsFacade,
    private val ipResolver: IpResolver
) {

    @PostMapping("/visit")
    fun trackVisit(httpServletRequest: HttpServletRequest): ResponseEntity<Unit> {
        val clientIp = ipResolver.getClientIp(httpServletRequest)
        val userAgent = httpServletRequest.getHeader("User-Agent")
//        analyticsFacade.trackVisit()
        return ResponseEntity.noContent().build()
    }
}