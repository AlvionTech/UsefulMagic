package cn.coostack.usefulmagic.beans

import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import net.minecraft.world.item.Items
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreferMagicDataTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun bootstrapMinecraft() {
            // Needed so the vanilla Items.* registry entries are populated.
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

    @Test
    fun factorsScaleWithPreferLevel() {
        val data = PreferMagicData(2.0, 3.0, 4.0, 5.0)
        data.putPrefer(Items.DIAMOND, 3)
        assertEquals(6.0, data.getDamageFactor(Items.DIAMOND), 1e-9)
        assertEquals(9.0, data.getUsageFactor(Items.DIAMOND), 1e-9)
        assertEquals(12.0, data.getCdReductionFactor(Items.DIAMOND), 1e-9)
        assertEquals(15.0, data.getManaReductionFactor(Items.DIAMOND), 1e-9)
    }

    @Test
    fun unknownItemHasZeroLevelAndFactors() {
        val data = PreferMagicData(2.0, 2.0, 2.0, 2.0)
        assertEquals(0, data.getPreferLevel(Items.STONE))
        assertFalse(data.isPreferItem(Items.STONE))
        assertEquals(0.0, data.getDamageFactor(Items.STONE), 1e-9)
    }

    @Test
    fun putPreferRegistersItemAndNegativeLevels() {
        val data = PreferMagicData(1.0, 1.0, 1.0, 1.0)
        data.putPrefer(Items.STICK, -2)
        assertTrue(data.isPreferItem(Items.STICK))
        assertEquals(-2, data.getPreferLevel(Items.STICK))
        assertEquals(-2.0, data.getDamageFactor(Items.STICK), 1e-9)
    }
}
