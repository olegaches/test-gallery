package com.example.imagesproject.presentation.shared_url

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.IntentSender
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.domain.type.Screen
import com.example.imagesproject.presentation.gallery_screen.components.TransparentSystemBars
import com.example.imagesproject.presentation.shared_url.components.SharedUrlScreenBottomBar
import com.example.imagesproject.presentation.shared_url.components.SharedUrlScreenTopBar
import com.example.imagesproject.ui.theme.ImagesProjectTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState


fun checkLocationSetting(
    context: Context,
    onDisabled: (IntentSenderRequest) -> Unit,
    onEnabled: () -> Unit
) {

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
        .setWaitForAccurateLocation(false)
        .setMinUpdateIntervalMillis(1000)
        .build()

    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
        .Builder()
        .addLocationRequest(locationRequest)

    val gpsSettingTask: Task<LocationSettingsResponse> =
        client.checkLocationSettings(builder.build())

    gpsSettingTask.addOnSuccessListener { onEnabled() }
    gpsSettingTask.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                val intentSenderRequest = IntentSenderRequest
                    .Builder(exception.resolution)
                    .build()
                onDisabled(intentSenderRequest)
            } catch (sendEx: IntentSender.SendIntentException) {
                // ignore here
            }
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun SharedUrlScreen(
    navController: NavController,
    viewModel: SharedUrlViewModel = hiltViewModel()
) {
    TransparentSystemBars(true)
    val context = LocalContext.current
    val notificationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_COARSE_LOCATION,
        onPermissionResult = viewModel::onPermissionResult
    )
    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK)
            viewModel.onSwitchToggle(notificationPermissionState, true)
        else {
            //Log.d("appDebug", "Denied")
        }
    }
    val state = viewModel.state.collectAsState().value
    var isSuccess by remember {
        mutableStateOf(true)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    val activity = remember {
        (context as? Activity)
    }
    val imageUrl = state.url
    val visibleBars = state.visibleBars


    ImagesProjectTheme(darkTheme = true) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Black,
            bottomBar = {
                SharedUrlScreenBottomBar(
                    isSuccess = isSuccess,
                    onSaveImage = {
                        viewModel.onSaveImage(imageUrl)
                        val imagesScreenRoute = Screen.ImagesScreen.route
                        navController.navigate(imagesScreenRoute) {
                            popUpTo(imagesScreenRoute) {
                                inclusive = true
                            }
                        }
                    },
                    isVisible = visibleBars && !isLoading,
                    onCancel = {
                        activity?.finishAndRemoveTask()
                    },
                    onSwitchToggle = {
                        if(!it) {
                            viewModel.onSwitchToggle(notificationPermissionState, false)
                        } else {
                            checkLocationSetting(
                                context = context,
                                onDisabled = { intentSenderRequest ->
                                    settingResultRequest.launch(intentSenderRequest)
                                },
                                onEnabled = {
                                    viewModel.onSwitchToggle(notificationPermissionState, true)
                                }
                            )
                        }
                    },
                    isLocationTracking = state.isLocationTracking
                )
            },
            topBar = {
                SharedUrlScreenTopBar(
                    isVisible = visibleBars,
                    title = imageUrl,
                    onBackClicked = {
                        navController.navigateUp()
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(state.snackbarHostState)
            },

            ) {
            BackHandler {
                activity?.finishAndRemoveTask()
            }
            Box(modifier = Modifier
                .fillMaxSize()) {
                var imageSize by remember {
                    mutableStateOf<Size?>(null)
                }
                val overZoomConfig = OverZoomConfig(
                    minSnapScale = 1f,
                    maxSnapScale = 1.7f
                )
                val zoomableState = rememberZoomableState(
                    initialScale = 1f,
                    minScale = 0.1f,
                    overZoomConfig = overZoomConfig,
                )
                val isHorizontalOrientation = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                Zoomable(
                    state = zoomableState,
                    onTap = {
                        viewModel.onBarsVisibilityChange()
                    },
                ) {
                    val imageNotFoundId = remember { R.drawable.image_not_found }
                    AsyncImage(
                        modifier = Modifier
                            .then(
                                if (imageSize != null) {
                                    Modifier.aspectRatio(
                                        imageSize!!.width / imageSize!!.height,
                                        isHorizontalOrientation
                                    )
                                } else
                                    Modifier.fillMaxSize()
                            ),
                        model = imageUrl,
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = imageNotFoundId),
                        placeholder = if(isSuccess)
                            painterResource(id = R.drawable.placeholder) else {
                            painterResource(imageNotFoundId)
                        },
                        onSuccess = { painterState ->
                            isSuccess = true
                            imageSize = painterState.painter.intrinsicSize
                            isLoading = false
                        },
                        onError = {
                            isSuccess = false
                            isLoading = false
                        },
                        contentDescription = null,
                    )
                }
            }
        }
    }
}