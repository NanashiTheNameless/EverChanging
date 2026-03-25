package io.github.nanashithenameless.everchanging.mixin;

import io.github.nanashithenameless.everchanging.GistServerConnector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(value = EnvType.CLIENT)
@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @ModifyVariable(method = "connect*", at = @At(value = "HEAD"), argsOnly = true)
    private static ServerAddress modifyConnectVariable(ServerAddress originalAddress) {
        if(originalAddress.getAddress().startsWith("gist://"))
        {
            String gistLink = originalAddress.getAddress().substring(7);
            return ServerAddress.parse(GistServerConnector.fetchServerIpFromGist(gistLink));
        }
        return originalAddress;
    }
}
