package gr.android.survey.ui.composable.modals

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.android.survey.R
import kotlinx.coroutines.delay

@Composable
fun NetworkBanner() {
    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var isConnected by remember { mutableStateOf(false) }
    var showBanner by remember { mutableStateOf(false) }
    var initialCheckDone by remember { mutableStateOf(false) }
    var firstLoad by remember { mutableStateOf(true) }

    fun checkNetworkConnectivity() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        isConnected =
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    LaunchedEffect(Unit) {
        checkNetworkConnectivity()
        initialCheckDone = true
        if (!isConnected) {
            showBanner = true
        }
    }

    DisposableEffect(Unit) {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
                if (!firstLoad) {
                    showBanner = true
                }
                firstLoad = false
            }

            override fun onLost(network: Network) {
                isConnected = false
                showBanner = true
                firstLoad = false
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    LaunchedEffect(isConnected) {
        if (initialCheckDone && isConnected) {
            delay(3000L)
            showBanner = false
        }
    }

    if (showBanner && initialCheckDone) {
        val backgroundColor = if (isConnected) Color.Green else Color.Red
        val statusText = if (isConnected)
            stringResource(id = R.string.network_connected_message)
        else
            stringResource(id = R.string.network_disconnected_message
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkBannerScreen() {
    NetworkBanner()
}