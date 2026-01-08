package com.devhjs.customgalary.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.devhjs.customgalary.domain.model.Photo
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

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC LIMIT $limit OFFSET $offset"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

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
        return photos
    }
}
