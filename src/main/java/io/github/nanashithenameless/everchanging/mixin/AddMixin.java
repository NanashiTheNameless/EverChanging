package io.github.nanashithenameless.everchanging.mixin;

import io.github.nanashithenameless.everchanging.EverChanging;
import io.github.nanashithenameless.everchanging.GistServerConnector;
import net.minecraft.client.network.MultiplayerServerListPinger;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListPinger.class)
public abstract class AddMixin {
	@ModifyArg(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerAddress;parse(Ljava/lang/String;)Lnet/minecraft/client/network/ServerAddress;"))
	private String modifyServerAddressParse(String address) {
		// Modify the address parameter as needed
		if (address.startsWith("gist://")) {
			String gistLink = address.substring(7);
			String serverIp = fetchServerIpFromGist(gistLink);
			return serverIp;
		} else {
			return address; // If the address doesn't start with "gist://", return it unchanged
		}
	}

	private String fetchServerIpFromGist(String gistLink)
	{
		return GistServerConnector.fetchServerIpFromGist(gistLink);
	}
}