package com.contentria.api.config

import com.contentria.api.config.properties.MarkdownProperties
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.footnotes.FootnotesExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.text.LineBreakRendering
import org.commonmark.renderer.text.TextContentRenderer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MarkdownProperties::class)
class MarkdownConfig(
    private val markdownProperties: MarkdownProperties
) {

    @Bean
    fun parser(): Parser {
        return Parser.builder()
            .extensions(
                listOf(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    AutolinkExtension.create(),
                    FootnotesExtension.create(),
                    HeadingAnchorExtension.create(),
                    InsExtension.create(),
                    YamlFrontMatterExtension.create(),
                    ImageAttributesExtension.create(),
                    TaskListItemsExtension.create()
                )
            )
            .build()
    }

    @Bean
    fun textContentRenderer(): TextContentRenderer {
        return TextContentRenderer.builder()
            .lineBreakRendering(LineBreakRendering.STRIP)
            .build()
    }
}