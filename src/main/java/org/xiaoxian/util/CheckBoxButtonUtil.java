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

    // 🔧 获取选中状态
    public boolean isChecked() {
        return checked;
    }

    // 🔧 设置选中状态
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    // 🔧 关键方法：切换选中状态
    public void toggle() {
        this.checked = !this.checked;
    }

    // 🔧 绘制复选框
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // 检查鼠标是否悬停
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;

            this.hovered = this.field_146123_n;

            // 绘制复选框背景
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            // 绘制外框
            drawRect(this.xPosition, this.yPosition,
                this.xPosition + this.width, this.yPosition + this.height,
                this.hovered ? 0xFFAAAAAA : 0xFF888888);

            // 绘制内框（背景）
            drawRect(this.xPosition + 1, this.yPosition + 1,
                this.xPosition + this.width - 1, this.yPosition + this.height - 1,
                0xFF000000);

            // 如果选中，绘制勾选标记
            if (this.checked) {
                drawCheckMark();
            }

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    // 🔧 可选：绘制勾号
    private void drawCheckMark() {
        // 绘制勾号的线条
        GL11.glPushMatrix();
        GL11.glTranslatef(this.xPosition + 2, this.yPosition + 2, 0);
        GL11.glLineWidth(2.0f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(2, 6);
        GL11.glVertex2f(6, 10);
        GL11.glVertex2f(6, 10);
        GL11.glVertex2f(12, 2);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    // 🔧 处理鼠标点击
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (this.enabled && this.visible) {
            if (mouseX >= this.xPosition && mouseY >= this.yPosition &&
                mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {

                // 点击时切换状态
                this.toggle();
                return true;
            }
        }
        return false;
    }

    // 🔧 处理鼠标释放（可选）
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        // 可以在这里添加释放时的逻辑
    }
}
