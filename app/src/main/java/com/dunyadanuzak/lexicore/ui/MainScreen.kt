package com.dunyadanuzak.lexicore.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dunyadanuzak.lexicore.R
// import com.google.android.gms.ads.AdRequest
// import com.google.android.gms.ads.AdSize
// import com.google.android.gms.ads.AdView

sealed class ListItem {
    data class Header(val length: Int, val count: Int) : ListItem()
    data class WordRow(val words: List<String>, val length: Int, val index: Int) : ListItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LexiCoreMainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "EPSL666",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.noRippleClickable {
                            uriHandler.openUri("https://www.dunyadanuzak.com/")
                        }
                    )
                }

                uiState.errorMessage?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(0.1f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = msg,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = uiState.input,
                            onValueChange = { viewModel.onInputChange(it) },
                            placeholder = {
                                Text(
                                    stringResource(R.string.enter_letters),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (uiState.input.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onInputChange("") }) {
                                        Text(
                                            text = "✕",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.tertiary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.tertiary.copy(0.6f),
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                val listState = rememberLazyListState()
                
                LaunchedEffect(uiState.results) {
                    if (uiState.results.isNotEmpty()) {
                        listState.scrollToItem(0)
                    }
                }
 
                val listItems by remember {
                    derivedStateOf {
                        buildList {
                            uiState.results.entries
                                .sortedByDescending { it.key }
                                .forEach { (length, words) ->
                                    add(ListItem.Header(length, words.size))
                                    val columnsPerRow = when {
                                        length >= 22 -> 1
                                        length >= 13 -> 2
                                        else -> 3
                                    }
                                    words.chunked(columnsPerRow).forEachIndexed { index, rowWords ->
                                        add(ListItem.WordRow(rowWords, length, index))
                                    }
                                }
                        }
                    }
                }
 
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() + 24.dp)
                ) {
                    items(
                        items = listItems,
                        key = { item ->
                            when (item) {
                                is ListItem.Header -> "header_${item.length}"
                                is ListItem.WordRow -> "row_${item.length}_${item.index}"
                            }
                        },
                        contentType = { item ->
                            when (item) {
                                is ListItem.Header -> "header"
                                is ListItem.WordRow -> "row"
                            }
                        }
                    ) { item ->
                        when (item) {
                            is ListItem.Header -> ResultHeader(item.length, item.count)
                            is ListItem.WordRow -> ResultRow(item.words, item.length)
                        }
                    }

                    if (uiState.results.isEmpty() && uiState.input.isNotEmpty()) {
                        item(key = "no_results") {
                            EmptyResultsPlaceholder()
                        }
                    }
                }

                // AdBanner(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
    }
}

@Composable
fun ResultHeader(length: Int, count: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$length Harfli",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "$count kelime",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 2.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ResultRow(words: List<String>, wordLength: Int) {
    val columnsPerRow = when {
        wordLength >= 22 -> 1
        wordLength >= 13 -> 2
        else -> 3
    }
    val fontSize = when {
        wordLength >= 20 -> 13.sp
        else -> 14.sp
    }
    val cardHeight = when {
        wordLength >= 15 -> 48.dp
        else -> 44.dp
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        words.forEach { word ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = word,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = fontSize,
                        maxLines = 1
                    )
                }
            }
        }
        repeat(columnsPerRow - words.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun EmptyResultsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.no_words_found),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

//@Composable
//fun AdBanner(modifier: Modifier = Modifier) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(50.dp),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        AndroidView(
//            modifier = Modifier.fillMaxWidth(),
//            factory = { context ->
//                AdView(context).apply {
//                    setAdSize(AdSize.BANNER)
//                    adUnitId = "ca-app-pub-4822153353761072/7966371844"
//                    loadAd(AdRequest.Builder().build())
//                }
//            }
//        )
//    }
//}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
