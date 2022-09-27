package io.zelr0x.icntr

import java.util.*

internal class BigBitSet(nBits: UInt) {
    private val maxFirstIndex = Integer.MAX_VALUE.toLong()

    private val first: BitSet
    private val second: BitSet?

    init {
        @Suppress("NAME_SHADOWING")
        val nBits = nBits.toLong()
        if (nBits >= maxFirstIndex) {
            this.first = BitSet(Integer.MAX_VALUE)
        } else {
            this.first = BitSet(nBits.toInt())
        }
        if (nBits > maxFirstIndex) {
            this.second = BitSet(secondIndex(nBits))
        } else {
            this.second = null
        }
    }

    fun set(bitIndex: UInt) {
        if (bitIndex.toLong() <= maxFirstIndex) {
            first.set(bitIndex.toInt())
        } else {
            second?.set(secondIndex(bitIndex.toLong()))
        }
    }

    fun cardinality(): UInt {
        return first.cardinality().toUInt() + (second?.cardinality() ?: 0).toUInt()
    }

    fun or(other: BigBitSet) {
        first.or(other.first)
        if (second != null && other.second != null) {
            second.or(other.second)
        }
    }

    private fun secondIndex(bitIndex: Long): Int {
        assert(bitIndex > maxFirstIndex)
        return (bitIndex - maxFirstIndex - 1).toInt()
    }
}
