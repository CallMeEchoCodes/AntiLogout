package org.samo_lego.antilogout.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.samo_lego.antilogout.datatracker.ILogoutRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class MServerCommonPacketListenerImpl {
    @Shadow @Final protected Connection connection;

    @Shadow protected abstract GameProfile playerProfile();

    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("TAIL"))
    private void al$disconnect(DisconnectionDetails disconnectionDetails, CallbackInfo ci) {
        if (!((Object)this instanceof ServerGamePacketListenerImpl packetListener)) return;

        if (((ILogoutRules) packetListener.player).al_isFake()) {
            packetListener.onDisconnect(disconnectionDetails);
        }
    }
}
