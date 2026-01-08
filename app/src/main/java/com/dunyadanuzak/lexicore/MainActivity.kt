package com.dunyadanuzak.lexicore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dunyadanuzak.lexicore.ui.LexiCoreMainScreen
import com.dunyadanuzak.lexicore.ui.MainViewModel
import com.dunyadanuzak.lexicore.ui.theme.TürkçeKelimeBulucuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TürkçeKelimeBulucuTheme {
                val viewModel: MainViewModel = hiltViewModel()
                LexiCoreMainScreen(viewModel = viewModel)
            }
        }
    }
}
