package com.contentria.common.extensions

import com.github.slugify.Slugify

private val slg: Slugify = Slugify.builder().build()

fun String.toSlug(): String {
    return slg.slugify(this)
}