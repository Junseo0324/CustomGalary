package com.devhjs.customgalary.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devhjs.customgalary.domain.model.Photo

class PhotoPagingSource(
    private val dataSource: PhotoDataSource
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val offset = page * pageSize

        val photos = try {
            dataSource.getPhotos(pageSize, offset)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

        val prevKey = if (page > 0) page - 1 else null
        val nextKey = if (photos.size == pageSize) page + 1 else null

        return LoadResult.Page(
            data = photos,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
