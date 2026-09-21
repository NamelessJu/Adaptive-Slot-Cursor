package namelessju.adaptiveslotcursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Config
{
    private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("adaptiveSlotCursorConfig.json").toFile();
    private static final Gson GSON;

    static
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.disableHtmlEscaping();
        GSON = gsonBuilder.create();
    }


    private final List<Option<?>> options = new ArrayList<>();


    public Option<Boolean> modEnabled = createOption("modEnabled", true, BOOLEAN_SERIALIZER);

    public Option<CursorTypeValue> cursorTypeEmptyCursorAndItemHovered
        = createOption("cursorTypeEmptyCursorAndItemHovered", CursorTypeValue.POINTING_HAND, CURSOR_TYPE_VALUE_SERIALIZER);
    public Option<CursorTypeValue> cursorTypeCarryingItem
        = createOption("cursorTypeCarryingItem", CursorTypeValue.DEFAULT, CURSOR_TYPE_VALUE_SERIALIZER);
    public Option<CursorTypeValue> cursorTypeCarryingItemAndCanPlaceInSlot
        = createOption("cursorTypeCarryingItemAndCanPlaceInSlot", CursorTypeValue.POINTING_HAND, CURSOR_TYPE_VALUE_SERIALIZER);
    public Option<CursorTypeValue> cursorTypeCarryingItemAndCannotPlaceInSlot
        = createOption("cursorTypeCarryingItemAndCannotPlaceInSlot", CursorTypeValue.NOT_ALLOWED, CURSOR_TYPE_VALUE_SERIALIZER);
    public Option<CursorTypeValue> cursorTypeCarryingItemAndSlotCanBePickedUp
        = createOption("cursorTypeCarryingItemAndSlotCanBePickedUp", CursorTypeValue.POINTING_HAND, CURSOR_TYPE_VALUE_SERIALIZER);


    private <T> Option<T> createOption(String id, T defaultValue, Config.Serializer<T> serializer)
    {
        Option<T> option = new Option<>(id, defaultValue, serializer);
        options.add(option);
        return option;
    }

    public void load()
    {
        String jsonString = null;

        if (FILE.exists())
        {
            BufferedReader bufferedReader = null;
            try
            {
                FileInputStream inputStream = new FileInputStream(FILE);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    if (!stringBuilder.isEmpty()) stringBuilder.append("\n");
                    stringBuilder.append(line);
                }
                bufferedReader.close();

                jsonString = stringBuilder.toString();
            }
            catch (IOException e)
            {
                AdaptiveSlotCursor.LOGGER.error("Failed to read config file", e);
            }
            finally
            {
                IOUtils.closeQuietly(bufferedReader);
            }
        }

        JsonElement json = null;
        if (jsonString != null)
        {
            try
            {
                json = JsonParser.parseString(jsonString);
            }
            catch (JsonSyntaxException e)
            {
                AdaptiveSlotCursor.LOGGER.error("Invalid config file JSON", e);
            }
        }

        JsonObject jsonObject = json instanceof JsonObject object ? object : new JsonObject();

        for (Option<?> option : options)
        {
            option.loadFrom(jsonObject);
        }
    }

    public void save()
    {
        JsonObject json = new JsonObject();
        for (Option<?> option : options)
        {
            option.saveTo(json);
        }

        String jsonString;
        try
        {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = GSON.newJsonWriter(stringWriter);
            jsonWriter.setIndent("    ");
            GSON.toJson(json, jsonWriter);
            jsonString = stringWriter.toString();
        }
        catch (Exception e)
        {
            AdaptiveSlotCursor.LOGGER.warn("Exception while serializing config JSON, used basic toString() instead", e);
            jsonString = json.toString();
        }

        File directory = FILE.getParentFile();
        if (directory != null)
        {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdirs();
        }

        BufferedWriter bufferedWriter = null;
        try
        {
            FileOutputStream outputStream = new FileOutputStream(FILE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(jsonString);
        }
        catch (IOException e)
        {
            AdaptiveSlotCursor.LOGGER.error("Failed to write config file", e);
        }
        finally
        {
            IOUtils.closeQuietly(bufferedWriter);
        }
    }

    public enum CursorTypeValue
    {
        DEFAULT(Component.translatable("adaptiveSlotCursor.cursorType.default"), () -> null),
        POINTING_HAND(Component.translatable("adaptiveSlotCursor.cursorType.pointingHand"), () -> CursorTypes.POINTING_HAND),
        CROSSHAIR(Component.translatable("adaptiveSlotCursor.cursorType.crosshair"), () -> CursorTypes.CROSSHAIR),
        RESIZE_ALL(Component.translatable("adaptiveSlotCursor.cursorType.resizeAll"), () -> CursorTypes.RESIZE_ALL),
        NOT_ALLOWED(Component.translatable("adaptiveSlotCursor.cursorType.notAllowed"), () -> CursorTypes.NOT_ALLOWED);

        // Class loading of CursorType is deferred until Minecraft is loaded,
        // as otherwise it's constants fail to create the underlying SDL cursor handles
        // during their initialization, breaking dynamic cursors for the entire game!
        public static void loadCursorTypes()
        {
            for (CursorTypeValue value : values())
            {
                value.cursorType = value.cursorTypeSupplier.get();
            }
        }

        public final Component component;
        private final Supplier<@Nullable CursorType> cursorTypeSupplier;
        private @Nullable CursorType cursorType;

        CursorTypeValue(Component component, Supplier<@Nullable CursorType> cursorTypeSupplier)
        {
            this.component = component;
            this.cursorTypeSupplier = cursorTypeSupplier;
        }

        public @Nullable CursorType cursorType()
        {
            return cursorType;
        }
    }

    public static class Option<T>
    {
        private final String id;
        private final Serializer<T> serializer;

        public T value;
        public final T defaultValue;

        protected Option(String id, T defaultValue, Serializer<T> serializer)
        {
            this.id = id;
            this.serializer = serializer;

            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        protected void loadFrom(JsonObject jsonObject)
        {
            value = defaultValue;

            JsonElement jsonElement = jsonObject.get(id);
            if (jsonElement != null)
            {
                T savedValue = serializer.deserialize(jsonElement);
                if (savedValue != null) value = savedValue;
            }
        }

        protected void saveTo(JsonObject jsonObject)
        {
            jsonObject.add(id, serializer.serialize(value));
        }
    }

    protected interface Serializer<T>
    {
        @Nullable T deserialize(JsonElement json);
        JsonElement serialize(T value);
    }

    protected static class EnumSerializer<T extends Enum<T>> implements Serializer<T>
    {
        private final Class<T> enumClass;

        public EnumSerializer(Class<T> enumClass)
        {
            this.enumClass = enumClass;
        }

        @Override
        public @Nullable T deserialize(JsonElement json)
        {
            if (json instanceof JsonPrimitive jsonPrimitive)
            {
                if (jsonPrimitive.isString())
                {
                    try
                    {
                        return Enum.valueOf(enumClass, jsonPrimitive.getAsString());
                    }
                    catch (Exception ignored) {}
                }
            }

            return null;
        }

        @Override
        public JsonElement serialize(T value)
        {
            return new JsonPrimitive(value.name());
        }
    }

    private static final Serializer<Boolean> BOOLEAN_SERIALIZER = new Serializer<>()
    {
        @Override
        public @Nullable Boolean deserialize(JsonElement json)
        {
            if (json instanceof JsonPrimitive jsonPrimitive)
            {
                if (jsonPrimitive.isBoolean()) return jsonPrimitive.getAsBoolean();
            }

            return null;
        }

        @Override
        public JsonElement serialize(Boolean value)
        {
            return new JsonPrimitive(value);
        }
    };
    private static final Serializer<CursorTypeValue> CURSOR_TYPE_VALUE_SERIALIZER = new EnumSerializer<>(CursorTypeValue.class);
}
