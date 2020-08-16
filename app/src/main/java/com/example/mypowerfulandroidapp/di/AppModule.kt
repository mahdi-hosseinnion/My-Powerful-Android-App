package com.example.mypowerfulandroidapp.di

import android.app.Application
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.persistence.AccountPropertiesDao
import com.example.mypowerfulandroidapp.persistence.AppDatabase
import com.example.mypowerfulandroidapp.persistence.AppDatabase.Companion.DATABASE_NAME
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() // get correct db version if schema changed
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthTokenDao(db: AppDatabase): AuthTokenDao {
        return db.getAuthTokenDao()
    }

    @Singleton
    @Provides
    fun provideAccountPropertiesDao(db: AppDatabase): AccountPropertiesDao {
        return db.getAccountPropertiesDao()
    }


    @Singleton
    @Provides
    fun provideRequestOption(): RequestOptions {
        return RequestOptions.placeholderOf(R.drawable.default_image)
            .error(R.drawable.default_image)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(app: Application,requestOptions: RequestOptions):RequestManager{
        return Glide.with(app)
            .setDefaultRequestOptions(requestOptions)
    }
}