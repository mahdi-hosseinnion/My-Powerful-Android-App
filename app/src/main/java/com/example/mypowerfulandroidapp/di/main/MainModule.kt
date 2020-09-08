package com.example.mypowerfulandroidapp.di.main

import com.example.mypowerfulandroidapp.api.main.OpenApiMainService
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.persistence.AccountPropertiesDao
import com.example.mypowerfulandroidapp.persistence.AppDatabase
import com.example.mypowerfulandroidapp.persistence.BlogPostDao
import com.example.mypowerfulandroidapp.repository.main.AccountRepository
import com.example.mypowerfulandroidapp.repository.main.BlogRepository
import com.example.mypowerfulandroidapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {
    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofit: Retrofit.Builder): OpenApiMainService {
        return retrofit
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(
            openApiMainService,
            accountPropertiesDao,
            sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(
            openApiMainService,
            blogPostDao,
            sessionManager
        )
    }

}