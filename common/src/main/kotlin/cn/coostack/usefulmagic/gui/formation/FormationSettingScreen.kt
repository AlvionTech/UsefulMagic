package cn.coostack.usefulmagic.gui.formation

import cn.coostack.usefulmagic.UsefulMagic
import cn.coostack.usefulmagic.formation.api.FormationSettings
import cn.coostack.usefulmagic.managers.client.ClientRequestManager
import cn.coostack.usefulmagic.packet.c2s.PacketC2SFormationSettingChangeRequest
import cn.coostack.usefulmagic.packet.c2s.PacketC2SFormationSettingRequest
import cn.coostack.usefulmagic.packet.s2c.PacketS2CFormationSettingsResponse
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.realms.RealmsLabel

class FormationSettingScreen(val clickPos: BlockPos, var settings: FormationSettings) :
    Screen(Component.literal("formation-setting")) {
    lateinit var textField: EditBox
    val client: Minecraft
        get() = Minecraft.getInstance()

    override fun init() {
        UsefulMagic.logger.debug("try flushing buttons")
        flush(settings)
        super.init()
        UsefulMagic.logger.debug("init finished")
    }


    private fun request() {
        ClientRequestManager.sendRequest(
            PacketC2SFormationSettingRequest(clickPos), PacketS2CFormationSettingsResponse.payloadID
        ).recall {
            it as PacketS2CFormationSettingsResponse
            flush(it.settings)
        }
    }

    private fun flush(data: FormationSettings) {
        this.settings = data
        clearWidgets()
        var v = client.options.guiScale().get()
        v = if (v == 0) {
            3
        } else {
            v
        }
        val font = Minecraft.getInstance().font
        // 基本位置然后转移到居中的Y轴偏移
        // 16 是按钮的高
        val alignCenterOffset = (16 + font.lineHeight) / 2
        val scaled = 3.0 / v
        val originX = width / 2
        addRenderableOnly(
            RealmsLabel(
                Component.literal("Formation Settings"),
//                originX - (64 * scaled).toInt(),
                originX,
                (alignCenterOffset * scaled).toInt(),
                0xFFFFFFFFU.toInt()
            )
        )
        addRenderableWidget(
            genToggleButton(
                { settings.hostileEntityAttack = it },
                { settings.hostileEntityAttack },
                Component.literal(if (settings.hostileEntityAttack) "§aEnabled" else "§cDisabled")
            ).bounds(
                originX + (64 * scaled).toInt(),
                (38 * scaled).toInt(),
                (32 * scaled).toInt(), (16 * scaled).toInt()
            ).tooltip(
                Tooltip.create(
                    Component.literal(
                        """
                            When enabled, the formation blocks all hostile mobs
                        """.trimIndent()
                    )
                )
            )
                .build()
        )

        addRenderableOnly(
            RealmsLabel(
                Component.literal("Attack Hostile Mobs"),
//                originX - (128 * scaled).toInt(),
                originX - (64 * scaled).toInt(),
                ((30 + alignCenterOffset) * scaled).toInt(),
                0xFFFFFFFFU.toInt()
//                    (128 * scaled).toInt(), (32 * scaled).toInt(),
//                    textRenderer
            )
        )

        addRenderableWidget(
            genToggleButton(
                { settings.playerEntityAttack = it },
                { settings.playerEntityAttack },
                Component.literal(if (settings.playerEntityAttack) "§aEnabled" else "§cDisabled")
            ).bounds(
                originX + (64 * scaled).toInt(),
//                originX,
                (60 * scaled).toInt(),
                (32 * scaled).toInt(), (16 * scaled).toInt()
            ).tooltip(
                Tooltip.create(
                    Component.literal(
                        """
                            When enabled, the formation attacks all non-friend players
                        """.trimIndent()
                    )
                )
            )
                .build()
        )

        addRenderableOnly(
            RealmsLabel(
                Component.literal("Attack Players"),
//                originX - (128 * scaled).toInt(),
                originX - (64 * scaled).toInt(),
                ((54 + alignCenterOffset) * scaled).toInt(),
                0xFFFFFFFFU.toInt()
//                    (128 * scaled).toInt(), (32 * scaled).toInt(),
//                    textRenderer
            )
        )

        addRenderableWidget(
            genToggleButton(
                { settings.animalEntityAttack = it },
                { settings.animalEntityAttack },
                Component.literal(if (settings.animalEntityAttack) "§aEnabled" else "§cDisabled")
            ).bounds(
                originX + (64 * scaled).toInt(),
//                originX,
                (82 * scaled).toInt(),
                (32 * scaled).toInt(), (16 * scaled).toInt()
            ).tooltip(
                Tooltip.create(
                    Component.literal(
                        """
                            When enabled, the formation blocks all animal entities
                        """.trimIndent()
                    )
                )
            )
                .build()
        )

        addRenderableOnly(
            RealmsLabel(
                Component.literal("Attack Animals"),
                originX - (64 * scaled).toInt(),
                ((76 + alignCenterOffset) * scaled).toInt(),
                0xFFFFFFFFU.toInt()
//                    (128 * scaled).toInt(), (32 * scaled).toInt(),
//                    textRenderer
            )
        )
        addRenderableWidget(
            genToggleButton(
                { settings.anotherEntityAttack = it },
                { settings.anotherEntityAttack },
                Component.literal(if (settings.anotherEntityAttack) "§aEnabled" else "§cDisabled")
            ).bounds(
                originX + (64 * scaled).toInt(),
                ((104) * scaled).toInt(),
                (32 * scaled).toInt(), (16 * scaled).toInt()
            ).tooltip(
                Tooltip.create(
                    Component.literal(
                        """
                            When enabled, the formation attacks entities whose type it cannot identify (the three categories above)
                        """.trimIndent()
                    )
                )
            )
                .build()
        )

        addRenderableOnly(
            RealmsLabel(
                Component.literal("Attack Other Entities"),
                originX - (64 * scaled).toInt(),
                ((98 + alignCenterOffset) * scaled).toInt(),
                0xFFFFFFFFu.toInt()
//                    (128 * scaled).toInt(), (32 * scaled).toInt(),
//                            textRenderer
            )
        )

        addRenderableWidget(
            genToggleButton(
                { settings.displayParticleOnlyTrigger = it },
                { settings.displayParticleOnlyTrigger },
                Component.literal(if (settings.displayParticleOnlyTrigger) "§cDisabled" else "§aEnabled")
            ).bounds(
                originX + (64 * scaled).toInt(),
                (126 * scaled).toInt(),
                (32 * scaled).toInt(), (16 * scaled).toInt()
            ).tooltip(
                Tooltip.create(
                    Component.literal(
                        """
                            When enabled, formation particles are only shown while the formation is active
                            They disappear 6 seconds after the formation deactivates
                        """.trimIndent()
                    )
                )
            )
                .build()
        )

        addRenderableOnly(
            RealmsLabel(
                Component.literal("Always Show Formation Particles"),
                originX - (64 * scaled).toInt(),
                ((120 + alignCenterOffset) * scaled).toInt(),
                0xFFFFFFFFU.toInt()
//                    (128 * scaled).toInt(), (32 * scaled).toInt(),
            )
        )

        addRenderableOnly(
            RealmsLabel(
                Component.literal("Always Show Defense Barrier"),
                originX - (64 * scaled).toInt(),
                ((142 + alignCenterOffset) * scaled).toInt(),
                0xFFFFFFFFU.toInt()
//                    (128 * scaled).toInt(), (32 * scaled).toInt(),
            )
        )


        addRenderableWidget(
            genToggleButton(
                { settings.displayDefendBallOnlyTrigger = it },
                { settings.displayDefendBallOnlyTrigger },
                Component.literal(if (settings.displayDefendBallOnlyTrigger) "§cDisabled" else "§aEnabled")
            ).bounds(
                originX + (64 * scaled).toInt(),
                (148 * scaled).toInt(),
                (32 * scaled).toInt(), (16 * scaled).toInt()
            ).tooltip(
                Tooltip.create(
                    Component.literal(
                        """
                            When enabled, the orb with the defense crystal is only shown while the formation is active
                            It disappears 6 seconds after the formation deactivates
                        """.trimIndent()
                    )
                )
            )
                .build()
        )

        textField = EditBox(
            font, originX + (64 * scaled).toInt(),
            (170 * scaled).toInt(),
            (32 * scaled).toInt(), (16 * scaled).toInt(), Component.literal("${settings.triggerRange}")
        )
        textField.tooltip = Tooltip.create(
            Component.literal(
                """
                            Formation trigger range
                            Within the formation's effective range, this controls when it triggers
                            The formation activates when an entity enters the set trigger range
                            and stays active until the entity leaves the effective range
                            Set to -1 to make the trigger range equal the effective range
                        """.trimIndent()
            )
        )
        textField.value = "${settings.triggerRange}"
        textField.setFilter { it.toDoubleOrNull() != null || it == "-" }
        textField.setResponder { text ->
            var num = text.toDoubleOrNull() ?: -1.0
            if (text == "-") {
                textField.value = "-1.0"      // NeoForge 用 .value 替代 .text
                settings.triggerRange = -1.0
            } else {
                if (num < 0) num = -1.0
                settings.triggerRange = num
            }
        }

        addRenderableWidget(
            textField
        )

        addRenderableOnly(
            RealmsLabel(
                Component.literal("Effective Range"),
                originX - (64 * scaled).toInt(),
                ((164 + alignCenterOffset) * scaled).toInt(),
                0xFFFFFFFFU.toInt()
            )
        )
        UsefulMagic.logger.debug("FLUSH FINISHED")
    }

    private fun genToggleButton(
        toggleMethod: (Boolean) -> Unit,
        getMethod: () -> Boolean,
        text: Component
    ): Button.Builder {
        return Button.builder(text) {
            val now = getMethod()
            toggleMethod(!now)
            ClientRequestManager.sendRequest(
                PacketC2SFormationSettingChangeRequest(clickPos, settings), PacketS2CFormationSettingsResponse.payloadID
            ).recall {
                Minecraft.getInstance().execute {
                    flush(settings)
                }
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun onClose() {
        ClientRequestManager.sendRequest(
            PacketC2SFormationSettingChangeRequest(clickPos, settings), PacketS2CFormationSettingsResponse.payloadID
        ).recall {
            Minecraft.getInstance().execute {
                flush(settings)
            }
        }
        super.onClose()
    }


}