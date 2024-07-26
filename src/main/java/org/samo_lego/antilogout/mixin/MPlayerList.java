package org.samo_lego.antilogout.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.samo_lego.antilogout.datatracker.ILogoutRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

/**
 * Kicks same players that are in {@link ILogoutRules#DISCONNECTED_PLAYERS} list
 * when player with same UUID joins.
 */
@Mixin(PlayerList.class)
public class MPlayerList {

    @Shadow
    @Final
    private MinecraftServer server;

    /**
     * When a player wants to connect but is still online,
     * we allow players with same uuid to be disconnected.
     *
     * @param gameProfile
     * @param cir
     */
    @Inject(method = "canPlayerLogin",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile, CallbackInfoReturnable<Component> cir) {
        ServerPlayer player = server.getPlayerList().getPlayer(gameProfile.getId());
        if (player == null)
            return;

        // Allows disconnect
        ((ILogoutRules) player).al_setAllowDisconnect(true);

        // Removes player so that the internal finite state machine in ServerLoginPacketListenerImpl can continue
        this.server.getPlayerList().remove(player);
        ILogoutRules.DISCONNECTED_PLAYERS.remove(player);
    }
}
