package bungus.sunlesssea.mixin;

import dev.customhitboxlib.api.ICustomMultipart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    
    //this fixes the serverside hard range check to also check against entity part hitboxes.
    @Redirect(
            method = "handleInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;canReachRaw(Lnet/minecraft/world/entity/Entity;D)Z",
                    remap = false
            )
    )
    private boolean sunlesssea$redirectCanReachRaw(ServerPlayer player, Entity entity, double padding){
        if(entity instanceof ICustomMultipart && ((ICustomMultipart)entity).hasCustomParts()){
            return Arrays.stream(((ICustomMultipart)entity).getCustomParts()).anyMatch(p -> player.canReachRaw(p, padding));
        }
        return player.canReachRaw(entity,padding);
    }
}
