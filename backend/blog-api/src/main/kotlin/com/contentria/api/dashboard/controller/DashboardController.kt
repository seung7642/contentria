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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
//@RequestMapping("/blogs/{slug}/dashboard")
class DashboardController(
    private val dashboardFacade: DashboardFacade
) {
    @GetMapping("/blogs/{blogSlug}/dashboard/stats")
    @PreAuthorize("isAuthenticated()") // 추후에 isOwner(#slug) 권한 검증 추가
    fun getStats(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable blogSlug: String
    ): ResponseEntity<DashboardStatsResponse> {
        val stats = dashboardFacade.getStats(userDetails.userId, blogSlug)
        return ResponseEntity.ok(DashboardStatsResponse.from(stats))
    }

    @GetMapping("/blogs/{blogSlug}/dashboard/popular-posts")
    @PreAuthorize("isAuthenticated()") // 추후에 isOwner(#slug) 권한 검증 추가
    fun getPopularPosts(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable blogSlug: String
    ): ResponseEntity<List<PopularPostResponse>> {
        val popularPosts = dashboardFacade.getPopularPosts(userDetails.userId, blogSlug)
        return ResponseEntity.ok(popularPosts.map { PopularPostResponse.from(it) })
    }

    @GetMapping("/blogs/{blogSlug}/dashboard/traffic")
    @PreAuthorize("isAuthenticated()") // 추후에 isOwner(#slug) 권한 검증 추가
    fun getTrafficData(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable blogSlug: String,
        @RequestParam timeRange: TimeRange
    ): ResponseEntity<List<TrafficChartResponse>> {
        val trafficData = dashboardFacade.getTrafficData(userDetails.userId, blogSlug, timeRange)
        return ResponseEntity.ok(trafficData.map { TrafficChartResponse.from(it) })
    }
}