package com.devhjs.customgalary.di

import android.content.Context
import android.content.ContentResolver
import com.devhjs.customgalary.data.repository.GalleryRepositoryImpl
import com.devhjs.customgalary.data.source.PhotoDataSource
import com.devhjs.customgalary.data.source.PhotoDataSourceImpl
import com.devhjs.customgalary.domain.repository.GalleryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindGalleryRepository(
        galleryRepositoryImpl: GalleryRepositoryImpl
    ): GalleryRepository

    @Binds
    @Singleton
    abstract fun bindPhotoDataSource(
        photoDataSourceImpl: PhotoDataSourceImpl
    ): PhotoDataSource

    companion object {
        @Provides
        @Singleton
        fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
            return context.contentResolver
        }
    }
}
