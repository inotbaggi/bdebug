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
    private static final Gson GSON = new Gson();
    private static final Type listType = new TypeToken<List<List<String>>>() {}.getType();
    private static List<List<String>> debugPages = new ArrayList<>();
    private static int currentPage = -1;
    private static int scrollOffset = 0;

    public static List<List<String>> getDebugPages() {
        return debugPages;
    }

    public static int getCurrentPage() {
        return currentPage;
    }

    public static void setCurrentPage(int page) {
        currentPage = page;
    }

    public static int getScrollOffset() {
        return scrollOffset;
    }

    public static void setScrollOffset(int offset) {
        scrollOffset = offset;
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DEBUG_CHANNEL, (client, handler, buf, responseSender) -> {
            int length = buf.readInt();
            byte[] jsonBytes = new byte[length];
            buf.readBytes(jsonBytes);

            String jsonData = new String(jsonBytes, StandardCharsets.UTF_8);
            List<List<String>> receivedPages = GSON.fromJson(jsonData, listType);

            if (receivedPages != null) {
                debugPages = receivedPages;
            }
        });

        new KeyBindings().register();
    }
}
