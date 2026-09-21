package namelessju.adaptiveslotcursor.impl;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import namelessju.adaptiveslotcursor.AdaptiveSlotCursor;
import namelessju.adaptiveslotcursor.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class YaclImpl
{
    public static Screen buildScreen(@Nullable Screen parentScreen)
    {
        Config config = AdaptiveSlotCursor.instance().config;

        Component configName = Component.translatable("adaptiveSlotCursor.configScreen.title", AdaptiveSlotCursor.MOD_NAME);

        Function<Option<Config.CursorTypeValue>, ControllerBuilder<Config.CursorTypeValue>> cursorTypeValueControllerBuilder
            = opt -> EnumControllerBuilder.create(opt)
                .enumClass(Config.CursorTypeValue.class)
                .formatValue(value -> value.component);

        return YetAnotherConfigLib.createBuilder()
            .title(configName)
            .category(ConfigCategory.createBuilder()
                .name(configName)
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("adaptiveSlotCursor.configScreen.option.modEnabled"))
                    .description(OptionDescription.of(
                        Component.translatable("adaptiveSlotCursor.configScreen.option.modEnabled.description")
                    ))
                    .binding(config.modEnabled.defaultValue, () -> config.modEnabled.value, newValue -> config.modEnabled.value = newValue)
                    .controller(BooleanControllerBuilder::create)
                    .build())
                .option(Option.<Config.CursorTypeValue>createBuilder()
                    .name(Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeEmptyCursorAndItemHovered"))
                    .description(OptionDescription.of(
                        Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeEmptyCursorAndItemHovered.description")
                    ))
                    .binding(
                        config.cursorTypeEmptyCursorAndItemHovered.defaultValue,
                        () -> config.cursorTypeEmptyCursorAndItemHovered.value,
                        newValue -> config.cursorTypeEmptyCursorAndItemHovered.value = newValue)
                    .controller(cursorTypeValueControllerBuilder)
                    .build())
                .option(Option.<Config.CursorTypeValue>createBuilder()
                    .name(Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItem"))
                    .description(OptionDescription.of(
                        Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItem.description")
                    ))
                    .binding(
                        config.cursorTypeCarryingItem.defaultValue,
                        () -> config.cursorTypeCarryingItem.value,
                        newValue -> config.cursorTypeCarryingItem.value = newValue)
                    .controller(cursorTypeValueControllerBuilder)
                    .build())
                .option(Option.<Config.CursorTypeValue>createBuilder()
                    .name(Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItemAndCanPlaceInSlot"))
                    .description(OptionDescription.of(
                        Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItemAndCanPlaceInSlot.description")
                    ))
                    .binding(
                        config.cursorTypeCarryingItemAndCanPlaceInSlot.defaultValue,
                        () -> config.cursorTypeCarryingItemAndCanPlaceInSlot.value,
                        newValue -> config.cursorTypeCarryingItemAndCanPlaceInSlot.value = newValue)
                    .controller(cursorTypeValueControllerBuilder)
                    .build())
                .option(Option.<Config.CursorTypeValue>createBuilder()
                    .name(Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItemAndCannotPlaceInSlot"))
                    .description(OptionDescription.of(
                        Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItemAndCannotPlaceInSlot.description")
                    ))
                    .binding(
                        config.cursorTypeCarryingItemAndCannotPlaceInSlot.defaultValue,
                        () -> config.cursorTypeCarryingItemAndCannotPlaceInSlot.value,
                        newValue -> config.cursorTypeCarryingItemAndCannotPlaceInSlot.value = newValue)
                    .controller(cursorTypeValueControllerBuilder)
                    .build())
                .option(Option.<Config.CursorTypeValue>createBuilder()
                    .name(Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItemAndSlotCanBePickedUp"))
                    .description(OptionDescription.of(
                        Component.translatable("adaptiveSlotCursor.configScreen.option.cursorTypeCarryingItemAndSlotCanBePickedUp.description")
                    ))
                    .binding(
                        config.cursorTypeCarryingItemAndSlotCanBePickedUp.defaultValue,
                        () -> config.cursorTypeCarryingItemAndSlotCanBePickedUp.value,
                        newValue -> config.cursorTypeCarryingItemAndSlotCanBePickedUp.value = newValue)
                    .controller(cursorTypeValueControllerBuilder)
                    .build())
                .build())
            .save(config::save)
            .build()
            .generateScreen(parentScreen);
    }
}
