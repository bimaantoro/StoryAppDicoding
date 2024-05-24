package com.example.storyapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.storyapp.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private const val MAXIMAL_SIZE = 1000000
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timestamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())


fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timestamp, ".jpg", filesDir)
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

fun formatDate(currentDate: String, context: Context): String? {
    val currentFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val timeZoneUTC = TimeZone.getTimeZone("UTC")
    val timeZoneIndonesia = TimeZone.getTimeZone("Asia/Jakarta")
    currentFormat.timeZone = timeZoneUTC
    return try {
        val date = currentFormat.parse(currentDate)

        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.timeZone = timeZoneIndonesia
            val dateIndonesia = calendar.time
            val now = Date()

            val diffInMilliSeconds = now.time - dateIndonesia.time
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliSeconds)

            if (diffInMinutes < 1) {
                return context.getString(R.string.just_now)
            } else if (diffInMinutes < 60) {
                return context.getString(R.string.minutes_ago, diffInMinutes)
            } else {
                val diffInHours = TimeUnit.MINUTES.toHours(diffInMinutes)
                if (diffInHours < 24) {
                    return context.getString(R.string.hours_ago, diffInHours)
                } else {
                    val outputDate = SimpleDateFormat("d MMMM yyyy, HH:mm:ss", Locale.getDefault())
                    return outputDate.format(dateIndonesia)
                }
            }
        } else {
            context.getString(R.string.invalid_date)
        }

    } catch (exc: Exception) {
        exc.printStackTrace()
        context.getString(R.string.invalid_date)
    }
}


fun formatDateISO8601(currentDate: String, targetTimeZone: String): String {
    val instant = Instant.parse(currentDate)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
        .withZone(ZoneId.of(targetTimeZone))
    return formatter.format(instant)

}