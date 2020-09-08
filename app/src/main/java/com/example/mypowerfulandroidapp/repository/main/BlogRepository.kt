package com.example.mypowerfulandroidapp.repository.main

import com.example.mypowerfulandroidapp.api.main.OpenApiMainService
import com.example.mypowerfulandroidapp.persistence.BlogPostDao
import com.example.mypowerfulandroidapp.repository.JobManager
import com.example.mypowerfulandroidapp.session.SessionManager
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {

}