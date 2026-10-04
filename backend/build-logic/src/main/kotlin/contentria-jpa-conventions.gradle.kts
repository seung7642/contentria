plugins {
    id("org.jetbrains.kotlin.plugin.jpa")        // noarg - @Entity에 no-arg 생성자 합성
    id("org.jetbrains.kotlin.plugin.allopen")    // final 해제 - 지연 로딩 프록시용
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}