package com.nokaori.genshinaibuilder.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R

/**
 * Utility object for accessing string resources in a type-safe manner
 */
object StringResources {
    
    @Composable
    fun getGestureSettingsTitle() = stringResource(R.string.gesture_settings_title)
    
    @Composable
    fun getGestureShakeTitle() = stringResource(R.string.gesture_shake_title)
    
    @Composable
    fun getGestureDoubleTapTitle() = stringResource(R.string.gesture_double_tap_title)
    
    @Composable
    fun getGestureTiltTitle() = stringResource(R.string.gesture_tilt_title)
    
    @Composable
    fun getCalibrationInstructions() = stringResource(R.string.calibration_instructions)
    
    @Composable
    fun getCalibrationGestureDetected() = stringResource(R.string.calibration_gesture_detected)
    
    @Composable
    fun getCalibrationDone() = stringResource(R.string.calibration_done)
    
    @Composable
    fun getEditorBiometricTitle() = stringResource(R.string.editor_biometric_title)
    
    @Composable
    fun getEditorSaveArtifact() = stringResource(R.string.editor_save_artifact)
    
    @Composable
    fun getSettingsTitle() = stringResource(R.string.settings_title)
    
    @Composable
    fun getSettingsGestureControls() = stringResource(R.string.settings_gesture_controls)
    
    @Composable
    fun getSettingsAppData() = stringResource(R.string.settings_app_data)
    
    @Composable
    fun getSettingsUpdateDatabase() = stringResource(R.string.settings_update_database)
    
    @Composable
    fun getSettingsStatusWaiting() = stringResource(R.string.settings_status_waiting)
    
    @Composable
    fun getSettingsErrorPrefix(message: String) = stringResource(R.string.settings_error_prefix, message)
}