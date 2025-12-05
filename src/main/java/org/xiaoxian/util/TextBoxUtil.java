package org.xiaoxian.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.opengl.GL11;
import java.awt.*;
import java.lang.reflect.Field;
import static org.xiaoxian.EasyLAN.devMode;

public class TextBoxUtil extends GuiTextField {

    String fieldName = devMode ? "lineScrollOffset" : "field_146225_q";
    private Field lineScrollOffsetField;
    private long lastUpdateTick = 20;
    private int componentId;

    public TextBoxUtil(int componentId, FontRenderer fontRendererInstance, int x, int y, int width, int height) {
        super(fontRendererInstance, x, y, width, height);
        this.componentId = componentId;

        try {
            lineScrollOffsetField = GuiTextField.class.getDeclaredField(fieldName);
            lineScrollOffsetField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            System.out.println("[EasyLan | TextBoxUtil] Error accessing field '" + fieldName + "': " + e.getMessage());
        }
    }

    @Override
    public void drawTextBox() {
        try {
            if (this.getVisible()) {
                // 绘制背景
                drawRect(xPosition, yPosition, xPosition + width + 4, yPosition + height, new Color(128, 128, 128, 30).getRGB());

                // 绘制边框
                GL11.glLineWidth(2f);
                drawHorizontalLine(xPosition, xPosition + width + 3, yPosition + height - 1, new Color(135,206,250).getRGB());
                GL11.glLineWidth(1f);

                int textColor = this.getEnableBackgroundDrawing() ? 14737632 : 7368816;

                int lineScrollOffset = 0;
                try {
                    if (lineScrollOffsetField != null) {
                        lineScrollOffset = (int) lineScrollOffsetField.get(this);
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("[EasyLan | drawTextBox] Error getting lineScrollOffset: " + e.getMessage());
                }

                // 🔧 关键修复：添加 null 检查和安全处理
                String fullText = getSafeText();
                String textToDraw = "";

                if (fullText != null && !fullText.isEmpty()) {
                    int startIndex = Math.max(0, lineScrollOffset);
                    if (startIndex < fullText.length()) {
                        textToDraw = fullText.substring(startIndex);
                    }
                }

                // 处理光标闪烁
                if (isFocused()) {
                    long currentTick = System.currentTimeMillis();
                    if (currentTick - lastUpdateTick > 500) {
                        textToDraw += "|";
                        lastUpdateTick = currentTick;
                    }
                }

                // 🔧 使用正确的字体渲染器（从父类获取）
                FontRenderer renderer = null;
                try {
                    // 通过反射安全获取 fontRendererObj
                    Field fontRendererObjField = GuiTextField.class.getDeclaredField("fontRendererObj");
                    fontRendererObjField.setAccessible(true);
                    renderer = (FontRenderer) fontRendererObjField.get(this);
                } catch (Exception e) {
                    System.err.println("[EasyLan] Error accessing fontRendererObj: " + e.getMessage());
                }

                // 安全绘制文本
                if (renderer != null) {
                    drawString(renderer, textToDraw, xPosition + 4, yPosition + (height - 8) / 2, textColor);
                } else {
                    // 最后的降级方案
                    System.err.println("[EasyLan] Warning: No FontRenderer available for drawing");
                }
            }
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Critical error in drawTextBox: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔧 添加安全获取文本的方法
    private String getSafeText() {
        try {
            String text = getText();
            return text != null ? text : "";
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Error getting text: " + e.getMessage());
            return "";
        }
    }

    // 🔧 重写 setText 方法增加保护
    @Override
    public void setText(String text) {
        try {
            super.setText(text != null ? text : "");
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Error setting text: " + e.getMessage());
        }
    }

    // 🔧 重写 getText 方法增加保护
    @Override
    public String getText() {
        try {
            String text = super.getText();
            return text != null ? text : "";
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Error getting text: " + e.getMessage());
            return "";
        }
    }
}
