package com.foryouandme.entity.page

import com.foryouandme.entity.mock.Mock

data class Page(
    val id: String,
    val title: String,
    val body: String,
    val image: String?,
    val link1: PageRef?,
    val link1Label: String?,
    val link2: PageRef?,
    val link2Label: String?,
    val externalLinkLabel: String?,
    val externalLinkUrl: String?,
    val specialLinkLabel: String?,
    val specialLinkValue: String?,
    val linkModalLabel: String?,
    val linkModalValue: PageRef?
) {

    fun asList(pages: List<Page>): MutableList<Page> {

        var page = pages.firstOrNull { it.id == link1?.id }

        val items = mutableListOf(this)

        while (page != null) {

            items.add(page)
            page = pages.firstOrNull { it.id == page?.link1?.id }

        }

        return items

    }

    companion object {

        fun mock(): Page =
            Page(
                id = "id",
                title = Mock.title,
                body = Mock.body,
                image = null,
                link1 = null,
                link1Label = null,
                link2 = null,
                link2Label = null,
                externalLinkLabel = null,
                externalLinkUrl = null,
                specialLinkLabel = null,
                specialLinkValue = null,
                linkModalLabel = null,
                linkModalValue = null
            )

    }

}


data class PageRef(val id: String)