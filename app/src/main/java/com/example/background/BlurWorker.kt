package com.example.background

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.workers.blurBitmap
import com.example.background.workers.makeStatusNotification
import com.example.background.workers.sleep
import com.example.background.workers.writeBitmapToFile

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("blurImage", appContext)

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)
        sleep()

        return try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e("TAG", "Gak ada bro ")
                throw IllegalArgumentException("gak ada bro")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)
        }catch (throwable: Throwable) {
            Log.e("mytag", "Error applying blur")
            Result.failure()
        }
    }
}