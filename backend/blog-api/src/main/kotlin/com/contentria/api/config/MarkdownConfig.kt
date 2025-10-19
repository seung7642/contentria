package com.contentria.api.config

import com.contentria.api.config.properties.MarkdownProperties
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.definition.DefinitionExtension
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.jsoup.safety.Safelist
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MarkdownProperties::class)
class MarkdownConfig(
    private val markdownProperties: MarkdownProperties
) {

    @Bean
    fun flexmarkParser(): Parser {
        val options = MutableDataSet().apply {
            // GFM 확장 기능 설정
            set(
                Parser.EXTENSIONS,
                listOf(
                    AbbreviationExtension.create(),
                    DefinitionExtension.create(),
                    FootnoteExtension.create(),
                    TablesExtension.create(),
                    TypographicExtension.create(),
//                    StrikethroughExtension.create(),
//                    TaskListExtension.create(),
//                    AutolinkExtension.create()
                )
            )

            // 성능 최적화 옵션
            set(Parser.BLANK_LINES_IN_AST, false)
            set(Parser.PARSE_INNER_HTML_COMMENTS, false)
        }

        return Parser.builder(options).build()
    }

    @Bean
    fun flexmarkRenderer(): HtmlRenderer {
        val options = MutableDataSet().apply {
            set(
                Parser.EXTENSIONS,
                listOf(
                    AbbreviationExtension.create(),
                    DefinitionExtension.create(),
                    FootnoteExtension.create(),
                    TablesExtension.create(),
                    TypographicExtension.create(),
                )
            )

            // HTML 렌더링 옵션
            set(HtmlRenderer.SOFT_BREAK, "<br />\n")
            set(HtmlRenderer.HARD_BREAK, "<br />\n")
            set(HtmlRenderer.GENERATE_HEADER_ID, true)  // 헤딩에 id 자동 생성
            set(HtmlRenderer.RENDER_HEADER_ID, true)
        }

        return HtmlRenderer.builder(options).build()
    }

    /**
     * Jsoup Safelist 설정
     * 허용할 HTML 태그 및 속성 정의
     */
    @Bean
    fun htmlSafelist(): Safelist {
        return Safelist.relaxed()
            // 기본 텍스트 태그
            .addTags("h1", "h2", "h3", "h4", "h5", "h6")
            .addTags("p", "br", "hr", "pre", "code", "em", "strong", "del", "ins")

            // 리스트
            .addTags("ul", "ol", "li")

            // 테이블
            .addTags("table", "thead", "tbody", "tr", "th", "td")

            // 체크박스
            .addTags("input")
            .addAttributes("input", "type", "checked", "disabled")

            // 링크
            .addAttributes("a", "href", "title", "rel", "target")

            // 이미지
            .addAttributes("img", "src", "alt", "title", "width", "height")

            // 코드 블록 (syntax highlighting 을 위한 class 속성 허용)
            .addAttributes("pre", "class")
            .addAttributes("code", "class")

            // 헤딩에 id 속성 허용 (목차 생성 등 활용 목적)
            .addAttributes("h1", "id")
            .addAttributes("h2", "id")
            .addAttributes("h3", "id")
            .addAttributes("h4", "id")
            .addAttributes("h5", "id")
            .addAttributes("h6", "id")

            // 테이블 정렬
            .addAttributes("th", "align", "style")
            .addAttributes("td", "align", "style")
            .apply {
                addProtocols("img", "src", *markdownProperties.allowedImageProtocols.toTypedArray())

                if (markdownProperties.allowExternalLinks) {
                    addProtocols("a", "href", "http", "https", "mailto")
                } else {
                    addProtocols("a", "href", "mailto")
                }
            }
    }
}