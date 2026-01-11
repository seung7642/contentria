package com.contentria.api.dashboard.controller

import com.contentria.api.dashboard.controller.dto.DashboardStatsResponse
import com.contentria.api.dashboard.controller.dto.PopularPostResponse
import com.contentria.api.dashboard.dto.TimeRange
import com.contentria.api.dashboard.controller.dto.TrafficChartDataResponse
import com.contentria.api.dashboard.application.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
//@RequestMapping("/blogs/{slug}/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) {
    @GetMapping("/blogs/{slug}/dashboard/stats")
    @PreAuthorize("isAuthenticated()") // 추후에 isOwner(#slug) 권한 검증 추가
    fun getStats(@PathVariable slug: String): ResponseEntity<DashboardStatsResponse> {
        val stats = dashboardService.getStats(slug)
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/blogs/{slug}/dashboard/popular-posts")
    @PreAuthorize("isAuthenticated()") // 추후에 isOwner(#slug) 권한 검증 추가
    fun getPopularPosts(@PathVariable slug: String): ResponseEntity<List<PopularPostResponse>> {
        val popularPosts = dashboardService.getPopularPosts(slug)
        return ResponseEntity.ok(popularPosts)
    }

    @GetMapping("/blogs/{slug}/dashboard/traffic")
    @PreAuthorize("isAuthenticated()") // 추후에 isOwner(#slug) 권한 검증 추가
    fun getTrafficData(
        @PathVariable slug: String,
        @RequestParam timeRange: TimeRange
    ): ResponseEntity<List<TrafficChartDataResponse>> {
        val trafficData = dashboardService.getTrafficData(slug, timeRange)
        return ResponseEntity.ok(trafficData)
    }
}