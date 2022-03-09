package net.shadow.client;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.shadow.client.feature.command.CommandRegistry;
import net.shadow.client.feature.gui.FastTickable;
import net.shadow.client.feature.gui.notifications.NotificationRenderer;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.helper.Rotations;
import net.shadow.client.helper.Texture;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PostInitEvent;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.font.adapter.impl.ClientFontRenderer;
import net.shadow.client.helper.font.render.GlyphPageFontRenderer;
import net.shadow.client.helper.manager.ConfigManager;
import net.shadow.client.helper.util.Utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CoffeeClientMain implements ModInitializer {

    public static final String MOD_ID = "sipoverprivate";
    public static final String MOD_NAME = "SipoverPrivate";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final File BASE = new File(MinecraftClient.getInstance().runDirectory, "sip");
    public static final List<ResourceEntry> resources = new ArrayList<>();
    public static long lastScreenChange = System.currentTimeMillis();
    public static CoffeeClientMain INSTANCE;
    public static Thread MODULE_FTTICKER;
    public static Thread FAST_TICKER;
    public static String sessionKey = null;
    public boolean initialized = false;

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static void registerTexture(ResourceEntry entry) {
        resources.add(entry);
    }

    public static String generateOrGetSessionToken() {
        if (sessionKey != null) {
            return sessionKey;
        }
        sessionKey = RandomStringUtils.randomAlphabetic(32);
        return sessionKey;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        log(Level.INFO, "Initializing");

        registerTexture(new ResourceEntry(new Texture("background.jpg"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/background.jpg"));
        registerTexture(new ResourceEntry(new Texture("notif/error.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/error.png"));
        registerTexture(new ResourceEntry(new Texture("notif/info.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/info.png"));
        registerTexture(new ResourceEntry(new Texture("notif/success.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/success.png"));
        registerTexture(new ResourceEntry(new Texture("notif/warning.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/warning.png"));

        registerTexture(new ResourceEntry(new Texture("icons/render"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/render.png"));
        registerTexture(new ResourceEntry(new Texture("icons/move"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/movement.png"));
        registerTexture(new ResourceEntry(new Texture("icons/misc"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/misc.png"));
        registerTexture(new ResourceEntry(new Texture("icons/world"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/world.png"));
        registerTexture(new ResourceEntry(new Texture("icons/exploit"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/exploit.png"));
        registerTexture(new ResourceEntry(new Texture("icons/fun"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/fun.png"));
        registerTexture(new ResourceEntry(new Texture("icons/combat"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/combat.png"));

        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::saveState));
        if (BASE.exists() && !BASE.isDirectory()) {
            BASE.delete();
        }
        if (!BASE.exists()) {
            BASE.mkdir();
        }
        //        KeybindingManager.init();
        ConfigManager.loadState();


        log(Level.INFO, "Done initializing");
        //TODO: Initializer
    }

    void initFonts() {
        FontRenderers.setRenderer(new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", 17, false, false, false)));
    }

    void tickModulesNWC() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) {
                    module.onFastTick_NWC();
                }
            } catch (Exception ignored) {
            }
        }
    }

    void tickModules() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) {
                    module.onFastTick();
                }
            } catch (Exception ignored) {
            }
        }
    }

    void tickGuiSystem() {
        NotificationRenderer.onFastTick();
        try {
            if (client.currentScreen != null) {
                if (client.currentScreen instanceof FastTickable tickable) {
                    tickable.onFastTick();
                }
                for (Element child : new ArrayList<>(client.currentScreen.children())) { // wow, I hate this
                    if (child instanceof FastTickable t) {
                        t.onFastTick();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    public void postWindowInit() {
        initialized = true;
        initFonts();
        ConfigManager.loadState();
        MODULE_FTTICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickModulesNWC(); // always ticks even when we're not in a world
                if (client.player == null || client.world == null) {
                    continue;
                }
                tickModules(); // only ticks when we're in a world
            }
        }, "100_tps_ticker:modules");
        FAST_TICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickGuiSystem(); // ticks gui elements
                //                Themes.tickThemes(); // Tick themes
                if (client.player == null || client.world == null) {
                    continue;
                }
                Rotations.update(); // updates rotations, again only if we are in a world
            }
        }, "100_tps_ticker:gui");
        MODULE_FTTICKER.start();
        FAST_TICKER.start();
        //        ModuleRegistry.sortModulesPostInit();
        CommandRegistry.init();
        System.out.println("sending post init");
        Events.fireEvent(EventType.POST_INIT, new PostInitEvent());
    }

    public record ResourceEntry(Texture tex, String url) {
    }

}