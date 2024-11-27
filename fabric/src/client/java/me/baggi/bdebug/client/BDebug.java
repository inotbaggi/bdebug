package me.baggi.bdebug.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BDebug implements ClientModInitializer {
    public final Identifier DEBUG_CHANNEL = new Identifier("debugmod", "info");
    private final Gson GSON = new Gson();
    private final Type LIST_TYPE = new TypeToken<List<List<String>>>() {}.getType();

    private List<List<String>> debugPages = new ArrayList<>();

    private boolean isEnabled = false;

    private int currentPage = -1;
    private int scrollOffset = 0;

    private static BDebug instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        KeyBindings keyBindings = new KeyBindings(this);
        keyBindings.register();

        registerChannelReceiver();
    }

    private void registerChannelReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(DEBUG_CHANNEL, (client, handler, buf, responseSender) -> {
            byte[] jsonBytes = new byte[buf.readInt()];
            buf.readBytes(jsonBytes);

            String jsonData = new String(jsonBytes, StandardCharsets.UTF_8);
            List<List<String>> receivedPages = GSON.fromJson(jsonData, LIST_TYPE);

            if (receivedPages != null) {
                debugPages = receivedPages;
            }
        });
    }

    public List<List<String>> getDebugPages() {
        return debugPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int offset) {
        scrollOffset = offset;
    }

    public static BDebug getInstance() {
        return instance;
    }

    public void toggleDebugMenu() {
        if (!isEnabled) {
            currentPage = -1;
            scrollOffset = 0;
        }
        isEnabled = !isEnabled;
    }
}
