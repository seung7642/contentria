package com.contentria.api.dashboard.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.dashboard.application.DashboardFacade
import com.contentria.api.dashboard.controller.dto.DashboardStatsResponse
import com.contentria.api.dashboard.controller.dto.PopularPostResponse
import com.contentria.api.dashboard.controller.dto.TrafficChartResponse
import com.contentria.api.dashboard.dto.TimeRange
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/blogs/{blogSlug}/dashboard")
class DashboardController(
    private val dashboardFacade: DashboardFacade
) {
    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    fun getStats(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable blogSlug: String
    ): ResponseEntity<DashboardStatsResponse> {
        val stats = dashboardFacade.getStats(userDetails.userId, blogSlug)
        return ResponseEntity.ok(DashboardStatsResponse.from(stats))
    }

    @GetMapping("/popular-posts")
    @PreAuthorize("isAuthenticated()")
    fun getPopularPosts(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable blogSlug: String,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<List<PopularPostResponse>> {
        val popularPosts = dashboardFacade.getPopularPosts(userDetails.userId, blogSlug, size)
        return ResponseEntity.ok(popularPosts.map { PopularPostResponse.from(it) })
    }

    @GetMapping("/traffic")
    @PreAuthorize("isAuthenticated()")
    fun getTrafficData(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable blogSlug: String,
        @RequestParam timeRange: TimeRange
    ): ResponseEntity<List<TrafficChartResponse>> {
        val trafficData = dashboardFacade.getVisitorTrend(userDetails.userId, blogSlug, timeRange)
        return ResponseEntity.ok(trafficData.map { TrafficChartResponse.from(it) })
    }
}