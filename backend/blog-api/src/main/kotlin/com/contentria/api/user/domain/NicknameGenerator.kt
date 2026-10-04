package com.contentria.api.user.domain

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Random

@Component
class NicknameGenerator {

    private val adjectives = listOf(
        "행복한", "즐거운", "멋진", "귀여운", "배고픈", "용감한", "상냥한", "친절한",
        "똑똑한", "빛나는", "차가운", "따뜻한", "고요한", "활기찬", "신비로운", "평화로운",
        "명랑한", "순수한", "소박한", "거대한", "작은", "빠른", "느린", "강력한", "온화한"
    )
    private val nouns = listOf(
        "다람쥐", "고양이", "강아지", "호랑이", "판다", "여우", "토끼", "부엉이",
        "돌고래", "햄스터", "코알라", "북극곰", "펭귄", "사자", "코끼리", "기린",
        "물범", "알파카", "수달", "라쿤", "쿼카", "나무늘보", "고슴도치", "두더지", "비둘기"
    )
    private val random: Random = SecureRandom()

    fun generate(): String {
        val adj = adjectives[random.nextInt(adjectives.size)]
        val noun = nouns[random.nextInt(nouns.size)]
        val number = random.nextInt(9000) + 1000
        return "${adj}_${noun}_${number}"
    }
}