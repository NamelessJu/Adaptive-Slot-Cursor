package namelessju.adaptiveslotcursor.mixin;

import namelessju.adaptiveslotcursor.AdaptiveSlotCursor;
import namelessju.adaptiveslotcursor.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin
{
    @Shadow @Final
    protected AbstractContainerMenu menu;
    @Shadow
    protected @Nullable Slot hoveredSlot;

    @Inject(at = @At("RETURN"),
        //? >= 26.1 {
        method = "extractContents"
        //? } else {
        /*method = "renderContents"
        *///? }
    )
    private void handleSlotCursor(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci)
    {
        Config config = AdaptiveSlotCursor.instance().config;
        if (!config.modEnabled.value) return;

        ItemStack carriedItem = menu.getCarried();
        if (!carriedItem.isEmpty())
        {
            // carrying item (base)
            requestCursor(graphics, config.cursorTypeCarryingItem.value);

            if (hoveredSlot != null)
            {
                if (hoveredSlot.mayPlace(carriedItem))
                {
                    // carrying item, can be placed here
                    requestCursor(graphics, config.cursorTypeCarryingItemAndCanPlaceInSlot.value);
                }
                else
                {
                    ItemStack slotItem = hoveredSlot.getItem();
                    if (!slotItem.isEmpty()
                        && ItemStack.isSameItemSameComponents(carriedItem, slotItem)
                        && carriedItem.getCount() < carriedItem.getMaxStackSize())
                    {
                        // carrying item, can not be placed here but can pick up items
                        requestCursor(graphics, config.cursorTypeCarryingItemAndSlotCanBePickedUp.value);
                    }
                    else
                    {
                        // carrying item, can not be placed here & no items to pick up
                        requestCursor(graphics, config.cursorTypeCarryingItemAndCannotPlaceInSlot.value);
                    }
                }
            }
        }
        else if (hoveredSlot != null && hoveredSlot.hasItem())
        {
            // empty cursor & hovering item in slot
            requestCursor(graphics, config.cursorTypeEmptyCursorAndItemHovered.value);
        }
    }

    @Unique
    private void requestCursor(GuiGraphicsExtractor guiGraphicsExtractor, Config.CursorTypeValue cursorTypeValue)
    {
        if (cursorTypeValue.cursorType() == null) return;
        guiGraphicsExtractor.requestCursor(cursorTypeValue.cursorType());
    }
}
