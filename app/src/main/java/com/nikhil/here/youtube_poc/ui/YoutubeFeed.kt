package com.nikhil.here.youtube_poc.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nikhil.here.youtube_poc.data.PlayListResponse

@Composable
fun YoutubeFeed(items: List<PlayListResponse.PlayListItem>) {

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                AsyncImage(
                    model = item.snippet.thumbnails.default.url,
                    contentDescription = "Thumbnail",
                    modifier = Modifier
                        .width(120.dp)
                        .height(80.dp)
                )
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                ) {
                    Text(text = item.snippet.title)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = item.snippet.publishedAt)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "${item.contentDetails.itemCount} videos")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}