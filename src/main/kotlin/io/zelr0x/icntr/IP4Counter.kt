package io.zelr0x.icntr

import java.util.*
import java.util.stream.Stream

/**
 * Counts distinct IPv4 addresses.
 * Not thread-safe.
 */
class IP4Counter: Counter<String> {
    private val bitset = BigBitSet(maxIndex())

    override fun maxIndex(): UInt = UInt.MAX_VALUE

    /**
     * @param item an IPv4 address
     * @return an integer representation of the given IPv4 address string
     * or null if it can't be parsed.
     */
    override fun index(item: String): UInt? {
        val bytes = item.split('.')
        if (bytes.size != 4) return null
        val octets = bytes.map(String::toUByteOrNull)
        if (octets.contains(null)) return null
        return octets
            .map { it!! }
            .fold(0u) { out, x -> out.shl(8) + x }
    }

    override fun count(): UInt {
        return bitset.cardinality()
    }

    override fun count(data: Stream<String>): UInt {
        data.map(this::index)
            .filter(Objects::nonNull)
            .map { it!! }
            .forEach(bitset::set)
        return bitset.cardinality()
    }

    override fun <T : Counter<String>> or(other: T): T {
        if (other is IP4Counter) {
            bitset.or(other.bitset)
        } else {
            error("Merging counters of different types is not supported")
        }
        @Suppress("UNCHECKED_CAST")
        val res = this as T
        return res
    }
}
