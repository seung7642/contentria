package com.contentria.core.learning.kotlin

class Person constructor(
    val name: String,
    val age: Int,
    private val logs: MutableList<String> = mutableListOf()
) {
    init {
        logs.add("1. 주 생성자 / init 블록 실행 (name=$name, age=$age)")
        require(name.isNotBlank()) { "이름은 비어있을 수 없습니다." }
        require(age > 0) { "나이는 0보다 커야 합니다." }
    }

    constructor(name: String, logs: MutableList<String>) : this(name, 10, logs) {
        logs.add("2. 부 생성자 실행 (name=$name)")
        require(name.isNotBlank()) { "이름은 비어있을 수 없습니다." }
    }

    constructor(logs: MutableList<String>) : this("Bob", logs) {
        logs.add("3. 부 생성자 실행 (name=Bob)")
    }

    lateinit var gender: String
        private set

    fun registerGender(gender: String) {
        if (::gender.isInitialized) {
            throw IllegalStateException("성별은 이미 초기화되었습니다.")
        }
        this.gender = gender
    }

    fun isGenderInitialized(): Boolean = ::gender.isInitialized
}