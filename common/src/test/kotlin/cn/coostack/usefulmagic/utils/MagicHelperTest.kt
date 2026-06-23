package cn.coostack.usefulmagic.utils

import cn.coostack.usefulmagic.beans.PreferMagicData
import cn.coostack.usefulmagic.items.UsefulMagicDataComponentTypes
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MagicHelperTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            net.minecraft.SharedConstants.tryDetectVersion()
            net.minecraft.server.Bootstrap.bootStrap()
        }
    }

    @Test
    fun testGetManaCost() {
        // Mock dependencies
        val wand = mockk<ItemStack>()
        val ball = mockk<ItemStack>()
        val baseCost = 5000
        val effect = 0.5 // 50% reduction from wand effect
        val preferData = mockk<PreferMagicData>()

        mockkStatic(UsefulMagicDataComponentTypes::class)
        val wandMagicType = mockk<cn.coostack.cooparticlesapi.platform.registry.CommonDeferredComponentType<ItemStack>>()
        val baseManaType = mockk<cn.coostack.cooparticlesapi.platform.registry.CommonDeferredComponentType<Int>>()
        val wandReductionType = mockk<cn.coostack.cooparticlesapi.platform.registry.CommonDeferredComponentType<Double>>()
        val wandPreferType = mockk<cn.coostack.cooparticlesapi.platform.registry.CommonDeferredComponentType<PreferMagicData>>()
        
        every { UsefulMagicDataComponentTypes.WAND_MAGIC } returns wandMagicType
        every { UsefulMagicDataComponentTypes.MAGIC_BASE_MANA_COST } returns baseManaType
        every { UsefulMagicDataComponentTypes.WAND_REDUCTION } returns wandReductionType
        every { UsefulMagicDataComponentTypes.WAND_PREFER } returns wandPreferType
        
        every { wandMagicType.get() } returns mockk()
        every { baseManaType.get() } returns mockk()
        every { wandReductionType.get() } returns mockk()
        every { wandPreferType.get() } returns mockk()
        // but for now let's just mock what the wand.get returns
        // We need to bypass the actual get() calls to return our values
        every { wand.get(any<DataComponentType<ItemStack>>()) } returns ball
        every { ball.get(any<DataComponentType<Int>>()) } returns baseCost
        every { wand.get(any<DataComponentType<Double>>()) } returns effect
        every { wand.get(any<DataComponentType<PreferMagicData>>()) } returns preferData
        
        every { preferData.getManaReductionFactor(ball) } returns 0.2 // 20% reduction

        // Calculate expected:
        // wandAddition = baseCost * (1 - effect) = 5000 * 0.5 = 2500
        // final = (1 - wandReduction) * wandAddition = 0.8 * 2500 = 2000
        val finalCost = MagicHelper.getManaCost(wand)
        
        // Assert
        assertEquals(2000, finalCost)
    }
}
