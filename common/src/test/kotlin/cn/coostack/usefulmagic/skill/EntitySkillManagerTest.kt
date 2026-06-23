package cn.coostack.usefulmagic.skill

import cn.coostack.usefulmagic.skill.api.EntitySkillManager
import cn.coostack.usefulmagic.skill.api.Skill
import io.mockk.mockk
import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import net.minecraft.world.entity.LivingEntity
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Verifies the crash-hardening added to [EntitySkillManager.tick]: a skill that throws
 * during ticking must be caught and interrupted (entity tick exceptions are fatal to a
 * dedicated server in vanilla 1.21), and normal skill/cooldown flow must be unaffected.
 *
 * The owner [LivingEntity] is only ever passed through to skill methods, so a mock that is
 * never invoked is sufficient.
 */
class EntitySkillManagerTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun bootstrapMinecraft() {
            // Mocking LivingEntity triggers its static initializer, which needs the
            // vanilla registries available.
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

    private open class FakeSkill(
        private val id: String,
        override var chance: Double = 1.0,
        private val maxHolding: Int = 100,
        private val countDown: Int = 40,
    ) : Skill<LivingEntity> {
        var stopped = false
        override fun getSkillCountDown(source: LivingEntity): Int = countDown
        override fun onActive(source: LivingEntity) {}
        override fun onRelease(source: LivingEntity, holdingTick: Int) {}
        override fun getMaxHoldingTick(holdingEntity: LivingEntity): Int = maxHolding
        override fun holdingTick(holdingEntity: LivingEntity, holdTicks: Int) {}
        override fun stopHolding(entity: LivingEntity, holdTicks: Int) { stopped = true }
        override fun getSkillID(): String = id
    }

    private class ThrowingSkill(id: String) : FakeSkill(id) {
        override fun holdingTick(holdingEntity: LivingEntity, holdTicks: Int) {
            throw RuntimeException("boom from skill")
        }
    }

    private fun manager() = EntitySkillManager(mockk<LivingEntity>(relaxed = true))

    @Test
    fun `throwing skill is caught and interrupted instead of crashing`() {
        val m = manager()
        val skill = ThrowingSkill("boom")
        m.addSkill(skill)
        m.setActiveSkill(skill)
        assertTrue(m.hasActiveSkill())

        // Must not propagate the exception (would crash the server otherwise).
        m.tick()

        assertFalse(m.hasActiveSkill(), "active skill should be cleared after it threw")
        assertTrue(m.hasCD("boom"), "interrupted skill should be put on cooldown")
    }

    @Test
    fun `normal active skill keeps ticking without error`() {
        val m = manager()
        val skill = FakeSkill("ok", maxHolding = 3)
        m.addSkill(skill)
        m.setActiveSkill(skill)
        repeat(2) { m.tick() }
        assertTrue(m.hasActiveSkill(), "skill should still be holding before reaching max hold tick")
    }

    @Test
    fun `cooldown decrements each tick and expires`() {
        val m = manager()
        val skill = FakeSkill("cd", countDown = 2)
        m.addSkill(skill)
        m.setActiveSkill(skill)
        m.interruptActiveSkill(true)
        assertTrue(m.hasCD("cd"))
        m.tick() // 2 -> 1
        m.tick() // 1 -> 0 -> removed
        assertFalse(m.hasCD("cd"), "cooldown should expire and be removed")
    }
}
