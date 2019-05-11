package com.example.coroutines

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivityku"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        executeChannelCoroutine()
    }

    fun simpleCoroutines() = runBlocking {
        launch {
            delay(1000L)
            Log.d(TAG,"Coroutines")
        }
        Log.d(TAG,"Hello,")
        delay(2000L)
    }

    // it will be executed sequentially from the first then the second line and so on
    fun main() = runBlocking {
        val capital = getCapital()
        val income = getIncome()
        Log.d(TAG, "Your profit is ${income - capital}")
    }

    // it will be executed all of code pararel using async await command
    fun executeAllCodeAtaTime() = runBlocking {
        val capital = async { getCapital() }
        val income = async { getIncome() }
        Log.d(TAG, "Your profit is ${income.await() - capital.await()}}  ---executed at a time")
    }

    //make job with lazy implementation to suspend the execution
    @InternalCoroutinesApi  // use this because the getCancellationException is still experimental
    fun executeJobIndirectly() = runBlocking {
        val job = launch(start = CoroutineStart.LAZY) {
            // do something here
        }

        // to run the job without waiting for
        job.start()
        Log.d(TAG, "Another task")

        // to run the job with waiting for
        job.join()
        Log.d(TAG, "Another Tast")

        // cancel job with additional information
        job.cancel(cause = CancellationException("Time is up"))

        // get cancel information
        val reason = "Job is cancelled because ${job.getCancellationException().message}"
    }

    // make a deferred process with lazy implementation to suspend the execution
    fun executeCoroutineInderictly() = runBlocking {
        val deffer = async(start = CoroutineStart.LAZY){
            // do something
        }

        deffer.start()
        Log.d(TAG, "Another task")

        // to run the job with waiting for
        deffer.join()
        Log.d(TAG, "Another Tast")

        deffer.await()
        Log.d(TAG, "Another Tast")

        val exceptionReason =  "there is exception differed because ${deffer.getCompletionExceptionOrNull()?.message}"
    }

    // coroutines with dispatcher
    fun executeCoroutinesWithDispatcher() = runBlocking {
        launch(Dispatchers.Unconfined){
            Log.d(TAG, "Starting in ${Thread.currentThread().name}")
            delay(1000)
            Log.d(TAG, "Resuming in ${Thread.currentThread().name}")
        }.start()
    }


    fun executeCoroutineUsingSingleDispatcher() = runBlocking {
        val dispatcher = newSingleThreadContext("myThread")
        launch(dispatcher){
            Log.d(TAG, "Starting in ${Thread.currentThread().name}")
            delay(1000)
            Log.d(TAG, "Resumin in ${Thread.currentThread().name}")
        }.start()
    }

    fun executeCoroutineUsingThreadPool() = runBlocking(CoroutineName("Main")) {
        val dispatcher = newFixedThreadPoolContext(3, "myPool")
        launch(dispatcher){
            Log.d(TAG, "Starting in ${Thread.currentThread().name}")
            delay(1000)
            Log.d(TAG, "Resuming in ${Thread.currentThread().name}")
        }.start()

        val differ = async(dispatcher){
            Log.d(TAG, "Starting in ${Thread.currentThread().name}")
            delay(1000)
            Log.d(TAG, "Resumin in ${Thread.currentThread().name}")
        }.await()

    }

    // using channel to share resources
    fun executeChannelCoroutine() = runBlocking(CoroutineName("main")){
        val channel = Channel<Int>()
        launch(CoroutineName("v1coroutines")){
            Log.d(TAG, "Sending from ${Thread.currentThread().name}")
            for (x in 1..5) channel.send(x * x)
        }

        repeat(6){
            Log.d(TAG, channel.receive().toString())
        }
        Log.d(TAG, "received in ${Thread.currentThread().name}")
    }

    suspend fun getCapital(): Int {
        delay(1000L)
        return 50000
    }

    suspend fun getIncome(): Int {
        delay(1000L)
        return 75000
    }
}
