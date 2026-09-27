package bungus.sunlesssea.mixin;
import dev.customhitboxlib.api.CustomEntityPart;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    
    //for some reason some multihitbox entity parts do not forward attack events to the main entity properly. this fixes that
    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;createAttackPacket(Lnet/minecraft/world/entity/Entity;Z)Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;"
            )
    )
    private ServerboundInteractPacket sunlesssea$redirectPartAttack(Entity target, boolean usingSecondaryAction){
        if (target instanceof CustomEntityPart part) {
            Entity parent = part.getParent();
            System.out.println("parent = "+parent);
            if (parent != null) {
                return ServerboundInteractPacket.createAttackPacket(
                        parent,
                        usingSecondaryAction
                );
            }
        }
        return ServerboundInteractPacket.createAttackPacket(target, usingSecondaryAction);
    }
}
