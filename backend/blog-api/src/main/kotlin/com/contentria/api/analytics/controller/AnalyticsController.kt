package com.contentria.api.analytics.controller

import com.contentria.api.analytics.application.AnalyticsFacade
import com.contentria.api.analytics.controller.dto.TrackVisitRequest
import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.global.util.IpResolver
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/analytics")
class AnalyticsController(
    private val analyticsFacade: AnalyticsFacade,
    private val ipResolver: IpResolver
) {

    @PostMapping("/visit")
    fun trackVisit(
        @RequestBody request: TrackVisitRequest,
        // permitAll 엔드포인트 — 로그인 상태면 principal이 채워지고, 익명이면 null
        @AuthenticationPrincipal userDetails: AuthUserDetails?,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Unit> {
        val clientIp = ipResolver.getClientIp(httpServletRequest)
        val userAgent = httpServletRequest.getHeader("User-Agent")
        analyticsFacade.trackVisit(request.toCommand(clientIp, userAgent, userDetails?.userId))
        return ResponseEntity.noContent().build()
    }
}