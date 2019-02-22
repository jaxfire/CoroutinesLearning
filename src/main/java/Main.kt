import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun main(args: Array<String>) {
//    exampleBlocking()
//    exampleBlockingDispatcher()
//    exampleLaunchGlobal()
//    exampleLaunchGlobalWaiting()
//    exampleLaunchCoroutineScope()
//    exampleCustomDispatcher()
    exampleAsyncAwait()
}


suspend fun printlnDelayed(message: String) {
    delay(2000)
    println(message)
}

suspend fun complexCalculation(startNum: Int): Int {
    delay(3000)
    return startNum * 10
}


fun exampleBlocking() = runBlocking {
    println("one")
    printlnDelayed("two")
    println("three")
}

// Running on another thread but still blocking the main thread
fun exampleBlockingDispatcher() {

    // Dictionary definition for Dispatcher - "Send off to a destination". Allows you to choose which thread category
    // you want the operation to run on.
    // MAIN - Use to update UI. For Android you will need the dependency kotlinx-coroutines-android in order for
    // your coroutine to access the main UI looper.
    // IO
    runBlocking(Dispatchers.Default) {
        println("one - from thread ${Thread.currentThread().name}")
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    // Outside of runBlocking to show that it's running the the blocked main thread
    println("three - from thread ${Thread.currentThread().name}")
}

fun exampleLaunchGlobal() = runBlocking {

    println("one - from thread ${Thread.currentThread().name}")

    GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")

    // Without this delay the main thread would complete and the program would exit
    // and so we would never see out delayed print.
    // However using a delay like this is error prone and not good practice. Instead use...
    // Job and join(). This will delay the thread until the job is complete. See example below.
    delay(3000)
}

fun exampleLaunchGlobalWaiting() = runBlocking {

    println("one - from thread ${Thread.currentThread().name}")

    val job = GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    job.join() // Suspends coroutine until this job is complete.
}

// RunBlocking provides us with 'this' of type CoroutineScope
fun exampleLaunchCoroutineScope() = runBlocking {

    println("one - from thread ${Thread.currentThread().name}")

    // Launches in the local coroutine scope. Calling "this.launch" would be synonymous. Where 'this' is the
    // is the CoroutineScope return by runBlocking.
    launch() {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")

    // Note: We can remove job.join() as the launch is now running in the local coroutine scope
    // the block of code will wait for the launch job to complete.
}

fun exampleCustomDispatcher() = runBlocking {

    println("one - from thread ${Thread.currentThread().name}")

    // Takes Executor that is used for normal threading and converts to a coroutine Dispatcher.
    // The 2 here is the number of threads we'd like to pool.
    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    launch(customDispatcher) {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")

    // With custom dispatchers we need to manually shutdown the diapatcher's Executor to stop the thread.
    // If we did not include the line below then the program would never terminate.
    (customDispatcher.executor as ExecutorService).shutdown()
}

// launch returns a Job but it doesn't return a value. This is why we'd use async, await and withContext {...}
fun exampleAsyncAwait() = runBlocking {

    val startTime = System.currentTimeMillis()

    val deferred1 = async { complexCalculation(10) }.await()
    val deferred2 = async { complexCalculation(20) }.await()
    val deferred3 = async { complexCalculation(30) }.await()

    val sum = deferred1 + deferred2 + deferred3
    val endTime = System.currentTimeMillis()

    println("async/await result $sum")
    println("Time take: ${endTime - startTime}")
}