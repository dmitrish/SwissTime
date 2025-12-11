package com.coroutines.swisstime.update

import android.app.Activity
import android.content.IntentSender
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages in-app updates using the Play Core library. Supports both immediate and flexible update
 * flows.
 */
class AppUpdateManager(private val activity: Activity, private val coroutineScope: CoroutineScope) :
  DefaultLifecycleObserver {

  companion object {
    const val UPDATE_REQUEST_CODE = 500
    const val DAYS_FOR_FLEXIBLE_UPDATE = 2 // Days before showing flexible update
    const val DAYS_FOR_IMMEDIATE_UPDATE = 7 // Days before forcing immediate update
  }

  // Update manager from Play Core
  private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

  // State flows to observe update status
  private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
  val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

  // Install state listener
  private val installStateUpdatedListener: InstallStateUpdatedListener =
    InstallStateUpdatedListener { state ->
      when (state.installStatus()) {
        InstallStatus.DOWNLOADING -> {
          val bytesDownloaded = state.bytesDownloaded()
          val totalBytesToDownload = state.totalBytesToDownload()
          if (totalBytesToDownload > 0) {
            val progress = (bytesDownloaded * 100 / totalBytesToDownload).toInt()
            _updateStatus.value = UpdateStatus.Downloading(progress)
          }
        }
        InstallStatus.DOWNLOADED -> {
          _updateStatus.value = UpdateStatus.Downloaded
        }
        InstallStatus.INSTALLED -> {
          _updateStatus.value = UpdateStatus.Installed
          unregisterInstallStateListener()
        }
        InstallStatus.FAILED -> {
          _updateStatus.value = UpdateStatus.Failed("Update installation failed")
          unregisterInstallStateListener()
        }
        InstallStatus.CANCELED -> {
          _updateStatus.value = UpdateStatus.Canceled
          unregisterInstallStateListener()
        }
        else -> {
          // Handle other states if needed
        }
      }
    }

  private fun unregisterInstallStateListener() {
    appUpdateManager.unregisterListener(installStateUpdatedListener)
  }

  init {
    // Register the install state listener
    appUpdateManager.registerListener(installStateUpdatedListener)
  }

  /**
   * Checks if an update is available and starts the appropriate update flow.
   *
   * @param forceImmediateUpdate If true, will use immediate update flow if available
   */
  fun checkForUpdate(forceImmediateUpdate: Boolean = false) {
    _updateStatus.value = UpdateStatus.Checking

    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    appUpdateInfoTask
      .addOnSuccessListener { appUpdateInfo ->
        when (appUpdateInfo.updateAvailability()) {
          // Check if update is available
          UpdateAvailability.UPDATE_AVAILABLE -> {
            val updatePriority = appUpdateInfo.updatePriority()
            val clientVersionStalenessDays = appUpdateInfo.clientVersionStalenessDays() ?: 0

            // Determine update type based on staleness and priority
            val updateType =
              when {
                forceImmediateUpdate ||
                  clientVersionStalenessDays >= DAYS_FOR_IMMEDIATE_UPDATE ||
                  updatePriority >= 4 -> AppUpdateType.IMMEDIATE
                clientVersionStalenessDays >= DAYS_FOR_FLEXIBLE_UPDATE || updatePriority >= 2 ->
                  AppUpdateType.FLEXIBLE
                else -> null
              }

            if (updateType != null && appUpdateInfo.isUpdateTypeAllowed(updateType)) {
              startUpdate(appUpdateInfo, updateType)
            } else {
              _updateStatus.value = UpdateStatus.NotRequired
            }
          }
          // Update is already in progress
          UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
            // Continue the update that was already started
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
              startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
          }
          // No update available
          else -> {
            _updateStatus.value = UpdateStatus.NotRequired
          }
        }
      }
      .addOnFailureListener { exception ->
        _updateStatus.value = UpdateStatus.Failed(exception.message ?: "Unknown error")
      }
  }

  /** Starts the update process with the specified update type. */
  private fun startUpdate(appUpdateInfo: AppUpdateInfo, updateType: Int) {
    try {
      // Start the update
      appUpdateManager.startUpdateFlowForResult(
        appUpdateInfo,
        activity,
        AppUpdateOptions.defaultOptions(updateType),
        UPDATE_REQUEST_CODE
      )
      _updateStatus.value = UpdateStatus.Started(updateType)
    } catch (e: IntentSender.SendIntentException) {
      _updateStatus.value = UpdateStatus.Failed("Failed to launch update: ${e.message}")
    }
  }

  /** Completes a flexible update by installing the downloaded update. */
  fun completeUpdate() {
    appUpdateManager.completeUpdate()
    _updateStatus.value = UpdateStatus.Installing
  }

  /** Resumes any in-progress updates when the activity is resumed. */
  override fun onResume(owner: LifecycleOwner) {
    super.onResume(owner)

    // Check if an update has been downloaded
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
      // If an immediate update is in progress, resume it
      if (
        appUpdateInfo.updateAvailability() ==
          UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
          appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
      ) {
        startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
      }

      // If a flexible update has been downloaded, notify the user
      if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
        _updateStatus.value = UpdateStatus.Downloaded
      }
    }
  }

  /** Cleans up resources when the lifecycle owner is destroyed. */
  override fun onDestroy(owner: LifecycleOwner) {
    super.onDestroy(owner)
    appUpdateManager.unregisterListener(installStateUpdatedListener)
  }

  /**
   * Handles the result of the update flow. Should be called from the activity's onActivityResult
   * method.
   */
  fun onActivityResult(requestCode: Int, resultCode: Int) {
    if (requestCode == UPDATE_REQUEST_CODE) {
      if (resultCode != Activity.RESULT_OK) {
        _updateStatus.value = UpdateStatus.Canceled
      }
    }
  }

  /** Represents the current status of the update process. */
  sealed class UpdateStatus {
    object Idle : UpdateStatus()

    object Checking : UpdateStatus()

    object NotRequired : UpdateStatus()

    object InProgress : UpdateStatus()

    data class Started(val updateType: Int) : UpdateStatus()

    data class Downloading(val progress: Int) : UpdateStatus()

    object Downloaded : UpdateStatus()

    object Installing : UpdateStatus()

    object Installed : UpdateStatus()

    object Canceled : UpdateStatus()

    data class Failed(val reason: String) : UpdateStatus()
  }
}
