package com.contentria.common.global.aop

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

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

        log.info { "===> [START] Method: $methodName, Args: $params" }

        var result: Any? = null

        try {
            result = joinPoint.proceed()
            return result
        } finally {
            val responseLog = if (result is ResponseEntity<*>) {
                val status = result.statusCode
                val body = result.body
                "Status: $status, Body: ${body?.let { toJson(it) } ?: "null" }"
            } else {
                "Return: ${result?.let { toJson(it) } ?: "null" }"
            }

            log.info { "===> [END] Method: $methodName, Result: $result" }
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