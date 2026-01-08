package com.devhjs.customgalary.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.domain.model.PhotoDetail
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class PhotoDataSourceImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : PhotoDataSource {

    override suspend fun getPhotos(limit: Int, offset: Int): List<Photo> {
        val photos = mutableListOf<Photo>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val cursor = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val bundle = android.os.Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                putStringArray(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    arrayOf(MediaStore.Images.Media.DATE_TAKEN)
                )
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
            }
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                bundle,
                null
            )
        } else {
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC LIMIT $limit OFFSET $offset"
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )
        }
        
        android.util.Log.d("PhotoDataSource", "Querying photos: limit=$limit, offset=$offset")

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            
            android.util.Log.d("PhotoDataSource", "Cursor count: ${it.count}")

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn) ?: ""
                val dateTaken = it.getLong(dateColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                photos.add(Photo(id, contentUri.toString(), dateTaken, name))
            }
        }
        
        android.util.Log.d("PhotoDataSource", "Fetched ${photos.size} photos")
        return photos
    }

    override suspend fun getPhotoDetail(uri: String): PhotoDetail {
        var filename = "Unknown"
        var fileSize = "Unknown"
        var dateTaken = "Unknown"
        var time = "Unknown"
        var resolution = "Unknown"
        var location: String? = null

        val contentUri = Uri.parse(uri)
        
        // 1. Query basic info (Filename, Size) from ContentResolver
        contentResolver.query(contentUri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                
                if (nameIndex != -1) filename = cursor.getString(nameIndex) ?: "Unknown"
                if (sizeIndex != -1) {
                    val sizeBytes = cursor.getLong(sizeIndex)
                    fileSize = formatFileSize(sizeBytes)
                }
            }
        }

        // 2. Extract EXIF data
        try {
            contentResolver.openInputStream(contentUri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                
                // Date & Time
                val dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME) // "YYYY:MM:DD HH:MM:SS"
                if (dateTime != null) {
                    val parts = dateTime.split(" ")
                    if (parts.size >= 2) {
                        dateTaken = parts[0].replace(":", "-")
                        time = parts[1]
                    } else {
                        dateTaken = dateTime
                    }
                }
                
                // Resolution
                val width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
                val height = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
                if (width != null && height != null) {
                    resolution = "${width}x${height}"
                }
                
                // Location
                val latLong = FloatArray(2)
                if (exif.getLatLong(latLong)) {
                    location = "${latLong[0]}, ${latLong[1]}"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 3. Fallback: Query MediaStore if Date/Time is unknown
        if (dateTaken == "Unknown") {
             contentResolver.query(contentUri, arrayOf(MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATE_MODIFIED), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
                    val dateModifiedIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)

                    var timestamp = -1L
                    if (dateTakenIndex != -1 && !cursor.isNull(dateTakenIndex)) {
                        timestamp = cursor.getLong(dateTakenIndex)
                    } else if (dateModifiedIndex != -1 && !cursor.isNull(dateModifiedIndex)) {
                        timestamp = cursor.getLong(dateModifiedIndex) * 1000 // DATE_MODIFIED is in seconds
                    }

                    if (timestamp > 0) {
                        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val date = Date(timestamp)
                        dateTaken = sdfDate.format(date)
                        time = sdfTime.format(date)
                    }
                }
            }
        }

        return PhotoDetail(
            uri = uri,
            filename = filename,
            dateTaken = dateTaken,
            time = time,
            fileSize = fileSize,
            resolution = resolution,
            location = location
        )
    }

    private fun formatFileSize(sizeBytes: Long): String {
        val kb = sizeBytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1.0) {
            String.format(Locale.getDefault(), "%.2f MB", mb)
        } else {
            String.format(Locale.getDefault(), "%.2f KB", kb)
        }
    }
}
