package com.github.jvsena42.mandacaru.presentation.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Stable
class NotificationPermissionState internal constructor(
    private val context: Context,
    private val activity: Activity?,
) {
    private val usesRuntimePermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    var isGranted by mutableStateOf(NotificationUtils.areNotificationsEnabled(context))
        private set

    /**
     * True when the system dialog is not on the table and the settings page is the only
     * route left: either a request was denied with no rationale remaining (Android's
     * automatic "don't ask again"), or the platform predates the runtime permission and
     * notifications are only ever toggled in settings.
     */
    var needsSettings by mutableStateOf(!usesRuntimePermission)
        private set

    /** True once the user has actively turned the request down, which warrants a blunter
     * message than the opening invitation. */
    var wasDenied by mutableStateOf(false)
        private set

    internal var launcher: ActivityResultLauncher<String>? = null

    fun request() {
        if (needsSettings) {
            NotificationPermissionHelper.openAppSettings(context)
        } else {
            launcher?.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    internal fun onRequestResult(granted: Boolean) {
        isGranted = granted
        wasDenied = !granted
        // Checked only after an actual request: before one, a missing rationale is
        // indistinguishable from "never asked", which would strand the user on a settings
        // link when the dialog would have worked.
        needsSettings = !granted &&
            activity?.let { !NotificationPermissionHelper.shouldShowRationale(it) } == true
    }

    internal fun refresh() {
        val current = NotificationUtils.areNotificationsEnabled(context)
        if (current != isGranted) {
            isGranted = current
            if (current) {
                needsSettings = !usesRuntimePermission
                wasDenied = false
            }
        }
    }
}

/**
 * Observes the POST_NOTIFICATIONS permission as Compose state. Nothing is requested until
 * [NotificationPermissionState.request] is called, so the caller decides when to prompt.
 */
@Composable
fun rememberNotificationPermissionState(): NotificationPermissionState {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state = remember(context, activity) {
        NotificationPermissionState(context, activity)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        state.onRequestResult(granted)
    }

    SideEffect { state.launcher = launcher }

    // Catches a grant made outside the dialog — from the settings page, or from the
    // notification drawer — which produces no launcher callback.
    DisposableEffect(lifecycleOwner, state) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) state.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return state
}
