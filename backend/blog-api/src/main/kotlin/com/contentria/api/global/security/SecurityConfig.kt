package com.contentria.api.global.security

import com.contentria.common.global.error.ErrorCode
import com.contentria.api.global.error.ErrorResponse
import com.contentria.api.global.properties.AppProperties
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

private val log = KotlinLogging.logger {}

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customAuthenticationSuccessHandler: AuthenticationSuccessHandler,
    private val customLogoutHandler: LogoutHandler,
    private val appProperties: AppProperties,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/", "/error", "/auth/**", "/blogs/**", "/posts/**", "/analytics/visit").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2.successHandler(customAuthenticationSuccessHandler)
                oauth2.failureHandler { request, response, exception ->
                    val frontendUrl = "https://www.contentria.com/login?error=${exception.message}"
                    response.sendRedirect(frontendUrl)
                }
            }
            .logout { logout ->
                logout
                    .logoutUrl("/auth/logout")
                    .addLogoutHandler(customLogoutHandler)
                    .logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
            }
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(customAuthenticationEntryPoint())
                    .accessDeniedHandler(customAccessDeniedHandler())
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = appProperties.cors.allowedOrigins
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    private fun customAuthenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { request, response, authException ->
            log.warn { "Authentication required for ${request.requestURI}: ${authException.message}" }
            sendErrorResponse(response, ErrorCode.AUTHENTICATION_REQUIRED, request.requestURI)
        }
    }

    private fun customAccessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { request, response, accessDeniedException ->
            log.warn { "Access denied for ${request.requestURI}: ${accessDeniedException.message}" }
            sendErrorResponse(response, ErrorCode.AUTHORIZATION_FAILED, request.requestURI)
        }
    }

    private fun sendErrorResponse(response: HttpServletResponse, errorCode: ErrorCode, path: String) {
        response.status = errorCode.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()

        val errorResponse = ErrorResponse(
            status = errorCode.status.value(),
            code = errorCode.code,
            error = errorCode.status.reasonPhrase,
            message = errorCode.message,
            path = path
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}