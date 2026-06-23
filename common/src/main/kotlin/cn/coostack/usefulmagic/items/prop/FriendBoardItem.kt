package cn.coostack.usefulmagic.items.prop

import cn.coostack.cooparticlesapi.CooParticlesAPI
import cn.coostack.cooparticlesapi.platform.CooParticlesServices
import cn.coostack.usefulmagic.UsefulMagicClient
import cn.coostack.usefulmagic.managers.client.ClientRequestManager
import cn.coostack.usefulmagic.packet.c2s.PacketC2SFriendAddRequest
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext

class FriendBoardItem : Item(
    Properties()
        .stacksTo(1)
) {
    // friend_board
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag)
        tooltip.add(Component.literal("§7Right-click a player to add them to your friend list"))
        tooltip.add(Component.literal("§7Press §f${UsefulMagicClient.friendUIBinding.translatedKeyMessage.string}§7 to open the friend list UI"))
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        user: Player,
        entity: LivingEntity,
        hand: InteractionHand
    ): InteractionResult {
        if (!user.level().isClientSide) {
            return super.interactLivingEntity(stack, user, entity, hand)
        }
        if (entity !is Player) {
            return super.interactLivingEntity(stack, user, entity, hand)
        }
        CooParticlesServices.CLIENT_NETWORK.send((PacketC2SFriendAddRequest(user.uuid, entity.uuid)))
        user.sendSystemMessage(Component.literal("Added player ${entity.name.string} to your friend list"))

        return super.interactLivingEntity(stack, user, entity, hand)
    }


}