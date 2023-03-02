package com.nikhil.here.youtube_poc.data

import com.google.gson.annotations.SerializedName

data class PlayListResponse(
    @SerializedName("items") val items: List<PlayListItem>
) {
    data class PlayListItem(
        @SerializedName("id") val id: String,
        @SerializedName("snippet") val snippet: Snippet,
        @SerializedName("contentDetails") val contentDetails: ContentDetails
    ) {
        data class Snippet(
            @SerializedName("title") val title: String,
            @SerializedName("publishedAt") val publishedAt: String,
            @SerializedName("description") val description: String?,
            @SerializedName("thumbnails") val thumbnails: Thumbnails
        ) {
            data class Thumbnails(
                @SerializedName("default") val default: Default
            ) {
                data class Default(
                    @SerializedName("url") val url: String
                )
            }
        }

        data class ContentDetails(
            @SerializedName("itemCount") val itemCount: Int
        )

    }
}
