package org.xiaoxian.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class CheckBoxButtonUtil extends GuiButton {

    private boolean checked;
    private boolean hovered;

    public CheckBoxButtonUtil(int id, int x, int y, boolean initialState, int width, int height) {
        super(id, x, y, width, height, "");
        this.checked = initialState;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggle() {
        this.checked = !this.checked;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // 检查鼠标悬停状态
            this.field_146123_n = (mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height);
            this.hovered = this.field_146123_n;

            // 保存 OpenGL 状态
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            try {
                // 确保正确的渲染状态
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);

                // 绘制边框
                int borderColor = this.hovered ? 0xFFA0A0A0 : 0xFF808080;
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, borderColor);
                drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, borderColor);
                drawRect(
                    this.xPosition + this.width - 1,
                    this.yPosition,
                    this.xPosition + this.width,
                    this.yPosition + this.height,
                    borderColor);
                drawRect(
                    this.xPosition,
                    this.yPosition + this.height - 1,
                    this.xPosition + this.width,
                    this.yPosition + this.height,
                    borderColor);

                // 绘制内部背景
                int backgroundColor = this.hovered ? 0xFFE0E0E0 : 0xFFFFFFFF;
                drawRect(
                    this.xPosition + 1,
                    this.yPosition + 1,
                    this.xPosition + this.width - 1,
                    this.yPosition + this.height - 1,
                    backgroundColor);

                // 如果选中，绘制勾选标记
                if (this.checked) {
                    // 绘制绿色勾号
                    drawRect(
                        this.xPosition + 4,
                        this.yPosition + this.height / 2 - 1,
                        this.xPosition + this.width / 2 - 1,
                        this.yPosition + this.height / 2 + 1,
                        0xFF008000); // 水平线
                    drawRect(
                        this.xPosition + this.width / 2 - 1,
                        this.yPosition + this.height / 2 - 1,
                        this.xPosition + this.width - 4,
                        this.yPosition + this.height / 2 + 1,
                        0xFF008000); // 斜线
                    drawRect(
                        this.xPosition + 4,
                        this.yPosition + this.height / 2 - 1,
                        this.xPosition + this.width / 2 - 1,
                        this.yPosition + this.height - 4,
                        0xFF008000); // 垂直线
                }

            } finally {
                // 恢复 OpenGL 状态
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (this.enabled && this.visible) {
            if (mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height) {

                this.toggle();
                return true;
            }
        }
        return false;
    }
}
