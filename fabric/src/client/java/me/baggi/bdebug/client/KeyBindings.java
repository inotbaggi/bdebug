package me.baggi.bdebug.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    private static final KeyBinding nextPage = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.debugmod.next_page", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "category.debugmod")
    );
    private static final KeyBinding prevPage = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.debugmod.prev_page", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "category.debugmod")
    );
    private static final KeyBinding scrollUp = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.debugmod.scroll_up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.debugmod")
    );
    private static final KeyBinding scrollDown = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.debugmod.scroll_down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.debugmod")
    );

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!BDebug.getDebugPages().isEmpty()) {
                while (nextPage.wasPressed()) {
                    var nextPage = BDebug.getCurrentPage() + 1;
                    if (nextPage < BDebug.getDebugPages().size()) {
                        BDebug.setCurrentPage(nextPage);
                        BDebug.setScrollOffset(0);
                    }
                }
                while (prevPage.wasPressed()) {
                    var prevPage = BDebug.getCurrentPage() - 1;
                    if (prevPage <= -1) {
                        BDebug.setCurrentPage(-1);
                        BDebug.setScrollOffset(0);
                    }
                    if (prevPage <= BDebug.getDebugPages().size()) {
                        BDebug.setCurrentPage(prevPage);
                        BDebug.setScrollOffset(0);
                    }
                }
                while (scrollUp.wasPressed()) {
                    BDebug.setScrollOffset(Math.max(0, BDebug.getScrollOffset() - 10));
                }
                while (scrollDown.wasPressed()) {
                    int maxScroll = Math.max(0,
                            BDebug.getDebugPages().get(BDebug.getCurrentPage()).size() * client.textRenderer.fontHeight -
                                    client.getWindow().getScaledHeight()
                    );
                    BDebug.setScrollOffset(Math.min(maxScroll, BDebug.getScrollOffset() + 10));
                }
            }

        });
    }
}