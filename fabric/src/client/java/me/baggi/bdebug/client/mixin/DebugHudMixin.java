package me.baggi.bdebug.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.math.MatrixStack;
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
        if (!BDebug.getDebugPages().isEmpty()) {
            renderCustomDebugScreen(context);
            ci.cancel();
        }
    }

    @Unique
    private void renderCustomDebugScreen(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int currentPageIndex = BDebug.getCurrentPage();
        int scrollOffset = BDebug.getScrollOffset();

        // Получаем текущую страницу
        if (currentPageIndex >= 0 && currentPageIndex < BDebug.getDebugPages().size()) {
            var currentPage = BDebug.getDebugPages().get(currentPageIndex);

            int startY = 10 - scrollOffset;
            int currentY = startY;

            // Рендер строк на странице
            for (String line : currentPage) {
                context.drawTextWithShadow(textRenderer, line, 10, currentY, 0xFFFFFF);
                currentY += textRenderer.fontHeight;
            }

            // Рендер номера страницы внизу экрана
            String footer = "Page " + (currentPageIndex + 1) + "/" + BDebug.getDebugPages().size();
            context.drawTextWithShadow(
                    textRenderer,
                    footer,
                    10,
                    client.getWindow().getScaledHeight() - 20,
                    0xAAAAAA
            );
        }
    }
}
