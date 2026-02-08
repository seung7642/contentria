package com.contentria.batch

interface BatchJobExecutor {

    fun getJobName(): String
    fun execute()
}