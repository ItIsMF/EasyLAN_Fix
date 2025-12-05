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
    private FontRenderer fontRendererObj;

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

                // 🔧 智能获取字体渲染器
                FontRenderer renderer = getFontRendererSmartly();
                if (renderer != null) {
                    drawString(renderer, textToDraw, xPosition + 4, yPosition + (height - 8) / 2, textColor);
                } else {
                    System.err.println("[EasyLan] Critical: No FontRenderer available - falling back to basic rendering");
                    // 最后的降级方案：什么都不画
                }
            }
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Critical error in drawTextBox: " + e.getMessage());
            // 降级到父类方法
            try {
                super.drawTextBox();
            } catch (Exception ex) {
                // 静默失败
            }
        }
    }

    // 🔧 智能获取字体渲染器的方法
    private FontRenderer getFontRendererSmartly() {
        try {
            // 方法1：直接访问公开字段（如果有）
            if (this.fontRendererObj != null) {
                return this.fontRendererObj;
            }
        } catch (Exception e) {
            // 字段不存在或访问失败
        }

        try {
            // 方法3：通过反射尝试多种可能的字段名
            Class<?> clazz = GuiTextField.class;
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (FontRenderer.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        if (value instanceof FontRenderer) {
                            //System.out.println("[EasyLan] Found FontRenderer in field: " + field.getName());
                            return (FontRenderer) value;
                        }
                    } catch (Exception ex) {
                        // 继续尝试下一个字段
                    }
                }
            }
        } catch (Exception e) {
            // 反射失败
        }

        try {
            // 方法4：最后的降级方案
            if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().fontRenderer != null) {
                return Minecraft.getMinecraft().fontRenderer;
            }
        } catch (Exception e) {
            // Minecraft实例访问失败
        }

        System.err.println("[EasyLan] Failed to get FontRenderer by any method");
        return null;
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

    // 🔧 确保所有必要方法都被正确重写
    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        try {
            return super.textboxKeyTyped(typedChar, keyCode);
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Error in textboxKeyTyped: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Error in mouseClicked: " + e.getMessage());
        }
    }

    @Override
    public void setFocused(boolean focused) {
        try {
            super.setFocused(focused);
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Error setting focus: " + e.getMessage());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        try {
            super.setVisible(visible);
        } catch (Exception e) {
            System.err.println("[EasyLan | TextBoxUtil] Error setting visibility: " + e.getMessage());
        }
    }
}
