package cn.coostack.usefulmagic.extend

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberExtendTest {

    @Test
    fun lerpAsProgressInterpolates() {
        assertEquals(0.0, 0.0.lerpAsProgress(0, 10), 1e-9)
        assertEquals(5.0, 0.5.lerpAsProgress(0, 10), 1e-9)
        assertEquals(10.0, 1.0.lerpAsProgress(0, 10), 1e-9)
        assertEquals(7.0, 0.5.lerpAsProgress(4, 10), 1e-9)
    }

    @Test
    fun numberOperatorsTreatValuesAsDouble() {
        val a: Number = 2
        val b: Number = 3
        assertEquals(5.0, (a + b).toDouble(), 1e-9)
        assertEquals(-1.0, (a - b).toDouble(), 1e-9)
        assertEquals(6.0, (a * b).toDouble(), 1e-9)
    }
}
