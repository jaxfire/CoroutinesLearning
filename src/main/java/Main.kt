import kotlinx.coroutines.*

fun main(args: Array<String>) {
//    exampleBlocking()
//    exampleBlockingDispatcher()
//    exampleLaunchGlobal()
    exampleLaunchGlobalWaiting()
}


suspend fun printlnDelayed(message: String) {
    // Fake complex calculation
    delay(2000)
    println(message)
}

fun exampleBlocking() = runBlocking {
    println("one")
    printlnDelayed("two")
    println("three")
}

// Running on another thread but still blocking the main thread
fun exampleBlockingDispatcher() {

    // Dictionary definition for Dispatcher - "Send off to a destination"
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