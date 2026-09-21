package namelessju.adaptiveslotcursor.mixin;

import namelessju.adaptiveslotcursor.AdaptiveSlotCursor;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(
        method = "onGameLoadFinished",
        at = @At("RETURN")
    )
    private void onGameLoadFinished(CallbackInfo ci)
    {
        AdaptiveSlotCursor.instance().onGameLoadFinished();
    }
}
