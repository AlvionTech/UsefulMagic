package cn.coostack.usefulmagic.packet.listener.client

import cn.coostack.cooparticlesapi.platform.network.ClientContext
import cn.coostack.usefulmagic.blocks.entity.formation.EnergyCrystalsBlockEntity
import cn.coostack.usefulmagic.blocks.entity.formation.FormationCoreBlockEntity
import cn.coostack.usefulmagic.packet.s2c.PacketS2CEnergyCrystalChange
import cn.coostack.usefulmagic.packet.s2c.PacketS2CFormationBreak
import cn.coostack.usefulmagic.packet.s2c.PacketS2CFormationCreate

object FormationPacketListener {

    fun handleCreate(payload: PacketS2CFormationCreate, context: ClientContext) {
        context.client().execute {
            val pos = payload.pos
            val world = context.client().level ?: return@execute
            val entity = world.getBlockEntity(pos) ?: return@execute
            if (entity !is FormationCoreBlockEntity) return@execute
            entity.formation.tryBuildFormation()
        }
    }

    fun handleBreak(payload: PacketS2CFormationBreak, context: ClientContext) {
        context.client().execute {
            val pos = payload.formationPos
            val world = context.client().level ?: return@execute
            val entity = world.getBlockEntity(pos) ?: return@execute
            if (entity !is FormationCoreBlockEntity) return@execute
            entity.formation.breakFormation(payload.damage, null)
        }
    }

    fun handleEnergyChange(payload: PacketS2CEnergyCrystalChange, context: ClientContext) {
        context.client().execute {
            val crystal = payload.crystal
            val world = context.client().level ?: return@execute
            val entity = world.getBlockEntity(crystal) ?: return@execute
            if (entity !is EnergyCrystalsBlockEntity) return@execute
            entity.currentMana = payload.mana
            entity.maxMana = payload.maxMana
            entity.setChanged()
        }
    }

}