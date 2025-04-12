package io.zelr0x.icntr

import java.io.File
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.*
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

private const val DEFAULT_WORKERS = 4
private const val DEFAULT_CHUNK_SIZE = 100000

private val TS_FMT = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault())

/**
 * An exact count-distinct problem solver for large lists of non-unique IP addresses.
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printUsage()
        exitProcess(1)
    }
    val fileName = args[0]
    val workers = args.getOrElse(1) { DEFAULT_WORKERS.toString() }.toInt()
    val chunkSize = args.getOrElse(2) { DEFAULT_CHUNK_SIZE.toString() }.toInt()
    println("[${now()}] Count started.")
    System.out.flush()
    val elapsed = measureNanoTime { countIPv4(fileName, workers, chunkSize, 8_000_000_000L) }
    println()
    println("[${now()}] Count finished in ${pprintNanos(elapsed)}.")
}

fun countIPv4(fileName: String,
              workers: Int = 1,
              chunkSize: Int = 1_000,
              totalLines: Long) {
    try {
        File(fileName).useLines { lines ->
            countIPv4(lines, workers, chunkSize, totalLines)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun countIPv4(lines: Sequence<String>,
              workers: Int = 1,
              chunkSize: Int = 1_000,
              totalLines: Long) {
    val perc = totalLines * 0.01
    val counters = LinkedBlockingQueue<IP4Counter>(workers)
    for (i in 0 until workers) {
        counters.add(IP4Counter())
    }
    val executor = Executors.newFixedThreadPool(workers)
    val it = lines.iterator()
    for (chunkIdx in 0 until Long.MAX_VALUE) {
        val chunk = it.asSequence().take(chunkSize).toList()
        if (chunk.isEmpty()) break
        reportProgress(chunkIdx, chunkSize, chunk, perc)
        val c = counters.take()
        val callable = Callable {
            c.count(chunk.stream())
            counters.put(c)
        }
        executor.submit(callable)
    }
    awaitShutdown(executor)
    val merged = counters.reduce { a, b -> a.or(b) }
    println("\n[${now()}] Found ${merged.count()} distinct addresses.")
}

fun printUsage() {
    println("Usage: icntr FILE")
}

fun reportProgress(chunkIdx: Long, chunkSize: Int,
                   chunk: Collection<*>, percent: Double) {
    if (chunkIdx != 0L && (chunkIdx + 1) % 5 == 0L) {
        val processedLines = chunkIdx * chunkSize + chunk.size
        print(
            "\r[${now()}] ${chunkIdx + 1} chunks processed" +
                    " (${processedLines} lines;" +
                    " ${(processedLines / percent).format(2)}%)"
        )
    }
}

fun now(): String {
    return LocalDateTime.now().format(TS_FMT)
}

fun awaitShutdown(executor: ExecutorService) {
    executor.shutdown()
    try {
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            println("[${now()}] Shutting down executor forcefully")
            executor.shutdownNow()
        }
    } catch (e: InterruptedException) {
        println("[${now()}] Shutdown interrupted. Forcing shutdown...")
        executor.shutdownNow()
        Thread.currentThread().interrupt()
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun pprintNanos(nanos: Long): String {
    val d = Duration.ofNanos(nanos)
    return "${d.toHoursPart()}h ${d.toMinutesPart()}m ${d.toSecondsPart()}s ${d.toMillisPart()}ms"
}
