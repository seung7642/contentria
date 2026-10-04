package com.contentria.core.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ConstructorTest {

    @Test
    fun testConstructor() {
        val person = Person("John", 30)

        assertThat(person.name).isEqualTo("John")
        assertThat(person.age).isEqualTo(30)
    }

    @Test
    fun testRegisterGender() {
        val person = Person("John", 30)

        assertThat(person.isGenderInitialized()).isFalse()

        person.registerGender("male")

        assertThat(person.isGenderInitialized()).isTrue()
    }

    @Test
    fun testConstructorValidation() {
        assertThatThrownBy { Person("", 0) }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { Person("John", -1) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("기본 생성자(인자 없음) 호출 시: 주 생성자(init) -> 2차 부 생성자 -> 1차 부 생성자 순으로 실행된다")
    fun testNoArgConstructorOrder() {
        val logs = mutableListOf<String>()

        val person = Person(logs)

        assertThat(person.name).isEqualTo("Bob")
        assertThat(person.age).isEqualTo(10)
        assertThat(logs).containsExactly(
            "1. 주 생성자 / init 블록 실행 (name=Bob, age=10)",
            "2. 부 생성자 실행 (name=Bob)",
            "3. 부 생성자 실행 (name=Bob)"
        )
    }

    @Test
    @DisplayName("인자 1개 부 생성자 호출 시: 주 생성자(init) -> 부 생성자 바디 순으로 실행된다")
    fun testSingleArgConstructorOrder() {
        val logs = mutableListOf<String>()

        Person("Alice", logs)

        assertThat(logs).containsExactly(
            "1. 주 생성자 / init 블록 실행 (name=Alice, age=10)",
            "2. 부 생성자 실행 (name=Alice)"
        )
    }

    @Test
    @DisplayName("주 생성자 호출 시: init 블록만 실행된다")
    fun testPrimaryConstructorOrder() {
        val logs = mutableListOf<String>()

        Person("Charlie", 30, logs)

        assertThat(logs).containsExactly("1. 주 생성자 / init 블록 실행 (name=Charlie, age=30)")
    }

    @Test
    @DisplayName("부 생성자에서 잘못된 기본값(0)을 주 생성자로 넘기면 init 블록 단계에서 예외가 발생한다")
    fun testInitValidationFailsBeforeSecondaryConstructorBody() {
        val logs = mutableListOf<String>()

        assertThatThrownBy { Person("David", 0, logs) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("나이는 0보다 커야 합니다.")

        assertThat(logs).containsExactly(
            "1. 주 생성자 / init 블록 실행 (name=David, age=0)"
        )
    }
}