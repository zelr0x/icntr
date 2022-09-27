package io.zelr0x.icntr

import java.util.stream.Stream

interface Counter<I> {
    /**
     * Returns the maximal value that can be returned by [index].
     */
    fun maxIndex(): UInt

    /**
     * Converts given item to [UInt]. Returns null if the item cannot be indexed
     * (useful when some items should be skipped).
     */
    fun index(item: I): UInt?

    /**
     * Returns the current count.
     */
    fun count(): UInt

    /**
     * Counts distinct items in a given stream and outputs current count after each item.
     */
    fun count(data: Stream<I>): UInt

    /**
     * Performs logical OR between two counters, effectively merging their results.
     *
     * This operation mutates `this` counter instance with the data from `other`,
     * `other` is left untouched.
     */
    fun <T: Counter<I>> or(other: T): T
}