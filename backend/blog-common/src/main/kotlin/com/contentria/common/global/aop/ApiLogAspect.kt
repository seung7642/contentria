package com.contentria.common.global.aop

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

private val log = KotlinLogging.logger {}

@Aspect
@Component
class ApiLogAspect(
    private val objectMapper: ObjectMapper
) {
    @Around("@annotation(com.contentria.common.global.aop.ApiLog)")
    @Throws(Throwable::class)
    fun logApi(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val methodName = signature.method.name
        val args = joinPoint.args

        val params = if (args.isNotEmpty()) {
            try {
                objectMapper.writeValueAsString(args)
            } catch (e: Exception) {
                args.contentToString()
            }
        } else {
            "[]"
        }

        log.debug { "[API START] $methodName args=$params" }

        var result: Any? = null

        try {
            result = joinPoint.proceed()
            return result
        } finally {
            val responseLog = if (result is ResponseEntity<*>) {
                "status=${result.statusCode}"
            } else {
                "returned=${result != null}"
            }

            log.debug { "[API END] $methodName $responseLog" }
        }
    }

    private fun toJson(obj: Any): String {
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (e: Exception) {
            obj.toString()
        }
    }
}