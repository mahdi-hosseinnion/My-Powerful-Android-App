package com.example.mypowerfulandroidapp.di.auth


import com.example.mypowerfulandroidapp.api.auth.OpenApiAuthService
import com.example.mypowerfulandroidapp.persistence.AccountPropertiesDao
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import com.example.mypowerfulandroidapp.repository.auth.AuthRepository
import com.example.mypowerfulandroidapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
@Module
class AuthModule{

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(): OpenApiAuthService{
        return Retrofit.Builder()
            .baseUrl("https://open-api.xyz")
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}