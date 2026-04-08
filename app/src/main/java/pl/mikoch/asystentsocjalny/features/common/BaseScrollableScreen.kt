package pl.mikoch.asystentsocjalny.features.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BaseScrollableScreen(
    title: String,
    bottomBar: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { TopAppBar(title = { Text(title) }) },
        bottomBar = bottomBar
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}
