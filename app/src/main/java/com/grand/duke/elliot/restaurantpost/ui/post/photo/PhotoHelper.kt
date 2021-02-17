package com.grand.duke.elliot.restaurantpost.ui.post.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.grand.duke.elliot.restaurantpost.R
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object PhotoHelper {

    private var photoUri: Uri? = null

    fun getPhotoUri() = photoUri

    object RequestCode {
        const val ImageCapture = 1915
        const val ImagePicker = 1916
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun dispatchImageCaptureIntent(activity: Activity) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(activity.packageManager)?.also {
                val imageFile: File? = try {
                    createImageFile(activity)
                } catch (e: IOException) {
                    Timber.e(e)
                    null
                }

                imageFile?.also {
                    val imageUri = FileProvider.getUriForFile(
                        activity,
                        activity.getString(R.string.file_provider_authorities),
                        it
                    )

                    photoUri = imageUri

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    activity.startActivityForResult(
                        intent,
                        RequestCode.ImageCapture
                    )
                }
            }
        }
    }

    fun dispatchImagePickerIntent(activity: Activity) {
        Intent(Intent.ACTION_PICK).also { imagePickerIntent ->
            imagePickerIntent.type = "image/*"
            activity.startActivityForResult(
                imagePickerIntent,
                RequestCode.ImagePicker
            )
        }
    }

    private fun createImageFile(context: Context): File? {
        return try {
            val timestamp: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val path = File(context.filesDir, "images")

            if (path.exists().not())
                path.mkdirs()

            File(path, "${timestamp}.jpg")
        } catch (e: FileNotFoundException) {
            Timber.e(e)
            null
        } catch (e: IOException) {
            Timber.e(e)
            null
        }
    }
}