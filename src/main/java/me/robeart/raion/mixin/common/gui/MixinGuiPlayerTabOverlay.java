package me.robeart.raion.mixin.common.gui;

import com.google.common.collect.Ordering;
import me.robeart.raion.client.module.misc.BetterTabModule;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author cookiedragon234 03/Mar/2020
 */
@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {
	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	private void getPlayerNameInject(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
		try {
			cir.setReturnValue(BetterTabModule.INSTANCE.getPlayerName(networkPlayerInfoIn));
			return;
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private List redirectSortedCopy(Ordering ordering, Iterable elements) {
		List out = null;
		try {
			if (BetterTabModule.INSTANCE.getState()) {
				if (BetterTabModule.INSTANCE.getPerformance()) {
					out = BetterTabModule.INSTANCE.getCachedList();
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		
		if (out == null) {
			out = ordering.sortedCopy(elements);
			try {
				if (BetterTabModule.INSTANCE.getState()) {
					out = BetterTabModule.INSTANCE.sort(out);
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		return out;
	}
	
	@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"))
	private List redirectMin(List list, int fromIndex, int toIndex) {
		try {
			if (BetterTabModule.INSTANCE.getState() && BetterTabModule.INSTANCE.getExtraTab()) {
				return list;
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		return list.subList(fromIndex, toIndex);
	}
	
	@Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
	private void injectDrawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn, CallbackInfo ci) {
		try {
			if (BetterTabModule.INSTANCE.drawPing(p_175245_1_, p_175245_2_, p_175245_3_, networkPlayerInfoIn)) {
				ci.cancel();
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
