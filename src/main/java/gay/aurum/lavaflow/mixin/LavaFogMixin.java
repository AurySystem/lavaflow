package gay.aurum.lavaflow.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class LavaFogMixin {

	@Inject(method = "applyFog", at = @At("TAIL"))
	private static void lavaflow$lavaFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {

		CameraSubmersionType submersionType = camera.getSubmersionType();

		if(submersionType.equals(CameraSubmersionType.LAVA)) {
			RenderSystem.setShaderFogStart(-16f);
			RenderSystem.setShaderFogEnd(32.5f);
		}
	}
}
