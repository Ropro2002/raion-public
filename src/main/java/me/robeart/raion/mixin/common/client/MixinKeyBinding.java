package me.robeart.raion.mixin.common.client;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.client.GetKeyStateEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cookiedragon234 12/Nov/2019
 */
@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding {
	
	@Accessor
	public abstract boolean getPressed();
	
	// Causes bugs, lets disable this for now
	/*@Inject(method = "unPressAllKeys", at = @At("HEAD"), cancellable = true)
	private static void unPressAllKeysWrapper(CallbackInfo ci)
	{
		UnpressAllKeysEvent event = new UnpressAllKeysEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if(event.shouldUnpress)
		{
			ci.cancel();
		}
	}*/
	
	@Redirect(method = "isKeyDown", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/settings/IKeyConflictContext;isActive()Z"))
	private boolean isKeyConflictActive(IKeyConflictContext iKeyConflictContext) {
		return true;
	}
	
	@Redirect(method = "isKeyDown", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/settings/KeyModifier;isActive(Lnet/minecraftforge/client/settings/IKeyConflictContext;)Z"))
	private boolean isKeyConflictActive(KeyModifier keyModifier, IKeyConflictContext conflictContext) {
		return true;
	}
	
	@Inject(method = "isKeyDown", at = @At("RETURN"), cancellable = true)
	private void isKeyDownWrapper(final CallbackInfoReturnable<Boolean> ci) {
		GetKeyStateEvent event = new GetKeyStateEvent((KeyBinding) (Object) this, ci.getReturnValue());
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		ci.setReturnValue(event.value);
	}
}
