package me.baggi.bdebug.client.mixin;

import me.baggi.bdebug.client.BDebug;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injectRender(DrawContext context, CallbackInfo ci) {
        BDebug modInstance = BDebug.getInstance();

        if (modInstance.getCurrentPage() != -1) {
            renderCustomDebugScreen(context, modInstance);
            ci.cancel();
        }
    }

    @Inject(method = "toggleDebugHud", at = @At("HEAD"))
    private void injectToggleDebugHud(CallbackInfo ci) {
        BDebug.getInstance().toggleDebugMenu();
    }

    @Unique
    private void renderCustomDebugScreen(DrawContext context, BDebug modInstance) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int currentPageIndex = modInstance.getCurrentPage();
        int scrollOffset = modInstance.getScrollOffset();

        if (isPageValid(modInstance, currentPageIndex)) {
            var currentPage = modInstance.getDebugPages().get(currentPageIndex);

            int startY = 10 - scrollOffset;
            renderPageContent(context, textRenderer, currentPage, startY);

            renderFooter(context, textRenderer, currentPageIndex, modInstance.getDebugPages().size(), client);
        }
    }

    @Unique
    private boolean isPageValid(BDebug modInstance, int currentPageIndex) {
        return currentPageIndex >= 0 && currentPageIndex < modInstance.getDebugPages().size();
    }

    @Unique
    private void renderPageContent(DrawContext context, TextRenderer textRenderer, Iterable<String> currentPage, int startY) {
        int currentY = startY;

        for (String line : currentPage) {
            context.drawTextWithShadow(textRenderer, line, 10, currentY, 0xFFFFFF);
            currentY += textRenderer.fontHeight;
        }
    }

    @Unique
    private void renderFooter(DrawContext context, TextRenderer textRenderer, int currentPageIndex, int totalPages, MinecraftClient client) {
        String footer = (currentPageIndex + 1) + "/" + totalPages;
        context.drawTextWithShadow(
                textRenderer,
                footer,
                10,
                client.getWindow().getScaledHeight() - 20,
                0xAAAAAA
        );
    }
}
