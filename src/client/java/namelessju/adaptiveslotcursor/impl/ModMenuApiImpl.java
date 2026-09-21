package namelessju.adaptiveslotcursor.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.util.NullScreenFactory;
import namelessju.adaptiveslotcursor.AdaptiveSlotCursor;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuApiImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3"))
        {
            AdaptiveSlotCursor.LOGGER.info("YACL is installed, using YaclImpl config screen");
            return YaclImpl::buildScreen;
        }

        return new NullScreenFactory<>();
    }
}
