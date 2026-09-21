package namelessju.adaptiveslotcursor;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptiveSlotCursor implements ClientModInitializer
{
    public static final String MOD_NAME = "Adaptive Slot Cursor";

    public static final Logger LOGGER = LoggerFactory.getLogger("adaptiveslotcursor");

    @SuppressWarnings("NotNullFieldNotInitialized")
    private static AdaptiveSlotCursor instance;
    public static AdaptiveSlotCursor instance()
    {
        return instance;
    }


    public final Config config = new Config();

    public AdaptiveSlotCursor()
    {
        instance = this;
    }

    @Override
    public void onInitializeClient()
    {
        config.load();
    }

    public void onGameLoadFinished()
    {
        Config.CursorTypeValue.loadCursorTypes();
    }
}
