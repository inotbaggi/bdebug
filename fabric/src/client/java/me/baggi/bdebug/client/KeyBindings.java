package me.baggi.bdebug.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    private final BDebug modInstance;

    private final KeyBinding nextPage = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.debugmod.next_page",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT,
                    "category.debugmod"
            )
    );
    private final KeyBinding prevPage = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.debugmod.prev_page",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT,
                    "category.debugmod"
            )
    );
    private final KeyBinding scrollUp = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.debugmod.scroll_up",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UP,
                    "category.debugmod"
            )
    );
    private final KeyBinding scrollDown = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.debugmod.scroll_down",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_DOWN,
                    "category.debugmod"
            )
    );

    public KeyBindings(BDebug modInstance) {
        this.modInstance = modInstance;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (modInstance.getDebugPages().isEmpty()) return;

            handlePageNavigation();
            handleScrolling(client);
        });
    }

    private void handlePageNavigation() {
        int currentPage = modInstance.getCurrentPage();
        int totalPages = modInstance.getDebugPages().size();

        if (nextPage.wasPressed()) {
            int next = currentPage + 1;
            if (next < totalPages) {
                setPage(next);
            }
        }

        if (prevPage.wasPressed() && currentPage != -1) {
            if (currentPage == 0) {
                modInstance.setCurrentPage(-1);
                modInstance.setScrollOffset(0);
            } else {
                int prev = currentPage - 1;
                setPage(Math.max(prev, 0));
            }
        }
    }

    private void handleScrolling(MinecraftClient client) {
        if (scrollUp.wasPressed()) {
            modInstance.setScrollOffset(Math.max(0, modInstance.getScrollOffset() - 10));
        }

        if (scrollDown.wasPressed()) {
            int maxScroll = calculateMaxScroll(client);
            modInstance.setScrollOffset(Math.min(maxScroll, modInstance.getScrollOffset() + 10));
        }
    }

    private void setPage(int page) {
        modInstance.setCurrentPage(page);
        modInstance.setScrollOffset(0);
    }

    private int calculateMaxScroll(MinecraftClient client) {
        int pageHeight = modInstance.getDebugPages()
                .get(modInstance.getCurrentPage()).getLines().size() * client.textRenderer.fontHeight;
        return Math.max(0, pageHeight - client.getWindow().getScaledHeight());
    }
}