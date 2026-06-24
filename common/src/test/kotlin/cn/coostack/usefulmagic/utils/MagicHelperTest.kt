package cn.coostack.usefulmagic.utils

import cn.coostack.usefulmagic.beans.PreferMagicData
import cn.coostack.usefulmagic.items.UsefulMagicDataComponentTypes
import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Exercises [MagicHelper.getManaCost] against real [ItemStack]s with real data components.
 * The mod's component types are `@JvmField` statics (not mockable via mockkStatic), so we
 * build them through the deferred suppliers and rely on in-memory component storage.
 */
class MagicHelperTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

    @Test
    fun getManaCostAppliesWandEffectAndPreferReduction() {
        val ball = ItemStack(Items.STICK)
        ball.set(UsefulMagicDataComponentTypes.MAGIC_BASE_MANA_COST.get(), 5000)

        // manaReductionFactor 0.2 * prefer level 1 for the ball's item = 0.2 reduction
        val prefer = PreferMagicData(0.0, 0.0, 0.0, 0.2).putPrefer(Items.STICK, 1)

        val wand = ItemStack(Items.STICK)
        wand.set(UsefulMagicDataComponentTypes.WAND_MAGIC.get(), ball)
        wand.set(UsefulMagicDataComponentTypes.WAND_REDUCTION.get(), 0.5)
        wand.set(UsefulMagicDataComponentTypes.WAND_PREFER.get(), prefer)

        // wandAddition = 5000 * (1 - 0.5) = 2500
        // final        = (1 - 0.2) * 2500 = 2000
        assertEquals(2000, MagicHelper.getManaCost(wand))
    }

    @Test
    fun getManaCostReturnsZeroWithoutMagicBall() {
        assertEquals(0, MagicHelper.getManaCost(ItemStack(Items.STICK)))
    }
}
