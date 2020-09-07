package com.example.mypowerfulandroidapp.repository

import android.util.Log
import kotlinx.coroutines.Job

open class JobManager(
    private val className: String
) {
    private  val TAG = "JobManager"
    private val jobs: HashMap<String, Job> = HashMap()

    fun addJob(methodName: String, job: Job) {
        cancelJob(methodName)
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String) {
        getJob(methodName)?.cancel()
    }

    fun getJob(methodName: String): Job? {
        if (jobs.containsKey(methodName)) {
            return jobs[methodName]
        }
        return null
    }

     fun cancelActiveJobs() {
        for ((methodName, job) in jobs) {
            if (job.isActive){
                Log.e(TAG, "cancelActiveJobs: $className canceled $methodName")
                job.cancel()
            }
        }
    }
}