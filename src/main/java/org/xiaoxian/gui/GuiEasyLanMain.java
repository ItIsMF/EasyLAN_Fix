package org.xiaoxian.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.xiaoxian.util.ButtonUtil;
import org.xiaoxian.util.CheckBoxButtonUtil;
import org.xiaoxian.util.ConfigUtil;
import org.xiaoxian.util.TextBoxUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import java.util.ArrayList;
import java.util.List;
import static org.xiaoxian.EasyLAN.*;

public class GuiEasyLanMain extends GuiScreen {
    private GuiTextField MotdTextBox;
    private String MotdText = motd != null ? motd : "";  // 添加 null 检查
    private final GuiScreen parentScreen;
    private final List<GuiButton> buttonList = new ArrayList<>();

    public GuiEasyLanMain(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        // 设置按钮
        buttonList.add(new ButtonUtil(0, this.width / 2 + 70, this.height - 25, 100, 20, I18n.format("easylan.back")));
        buttonList.add(new ButtonUtil(1, this.width / 2 - 50, this.height - 25, 100, 20, I18n.format("easylan.save")));

        // 游戏规则设置 - 使用绝对坐标，避免计算错误
        int baseX = this.width / 2 - 145;
        int baseY = 60;
        int spacing = 25;

        buttonList.add(new CheckBoxButtonUtil(10, baseX, baseY, allowPVP, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(11, baseX, baseY + spacing, onlineMode, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(12, baseX, baseY + spacing * 2, spawnAnimals, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(13, baseX, baseY + spacing * 3, spawnNPCs, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(14, baseX, baseY + spacing * 4, allowFlight, 20, 20));

        // 指令支持
        baseX = this.width / 2 - 25;
        baseY = 60;
        buttonList.add(new CheckBoxButtonUtil(20, baseX, baseY, whiteList, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(21, baseX, baseY + spacing, BanCommand, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(22, baseX, baseY + spacing * 2, OpCommand, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(23, baseX, baseY + spacing * 3, SaveCommand, 20, 20));

        // 其他设置
        baseX = this.width / 2 + 95;
        baseY = 60;
        buttonList.add(new CheckBoxButtonUtil(30, baseX, baseY, HttpAPI, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(31, baseX, baseY + spacing, LanOutput, 20, 20));

        // MOTD
        MotdTextBox = new TextBoxUtil(100, mc.fontRenderer, this.width / 2 - 70, 185, 230, 20);
        MotdTextBox.setMaxStringLength(100);

        updateGuiConfig();
    }

    private void drawLabelStrings() {
        try {
            if (mc.fontRenderer == null) return;

            // 游戏规则
            drawString(mc.fontRenderer, safeGetString("easylan.text.setting1"), this.width / 2 - 165, 35, 0x33CCFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.pvp"), this.width / 2 - 165, 60, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.onlineMode"), this.width / 2 - 165, 85, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.spawnAnimals"), this.width / 2 - 165, 110, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.spawnNPCs"), this.width / 2 - 165, 135, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.allowFlight"), this.width / 2 - 165, 160, 0xFFFFFF);

            // 指令支持
            drawString(mc.fontRenderer, safeGetString("easylan.text.setting2"), this.width / 2 - 25, 35, 0x33CCFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.whitelist"), this.width / 2 - 25, 60, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.ban"), this.width / 2 - 25, 85, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.op"), this.width / 2 - 25, 110, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.save"), this.width / 2 - 25, 135, 0xFFFFFF);

            // 其他设置
            drawString(mc.fontRenderer, safeGetString("easylan.text.setting3"), this.width / 2 + 95, 35, 0x33CCFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.httpApi"), this.width / 2 + 95, 60, 0xFFFFFF);
            drawString(mc.fontRenderer, safeGetString("easylan.text.lanInfo"), this.width / 2 + 95, 85, 0xFFFFFF);

            // MOTD
            drawString(mc.fontRenderer, safeGetString("easylan.text.motd"), this.width / 2 - 70, 170, 0xFFFFFF);

        } catch (Exception e) {
            System.err.println("[EasyLAN] Error drawing label strings: " + e.getMessage());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // 首先绘制背景
        this.drawDefaultBackground();

        try {
            // 绘制所有按钮（包括复选框）
            if (buttonList != null) {
                for (GuiButton button : buttonList) {
                    if (button != null) {
                        button.drawButton(mc, mouseX, mouseY);
                    }
                }
            }

            // 绘制文本框
            if (MotdTextBox != null) {
                MotdTextBox.drawTextBox();
            }

            // 绘制标签文本
            drawLabelStrings();

        } catch (Exception e) {
            System.err.println("[EasyLAN] Error in drawScreen: " + e.getMessage());
        }

        // 最后调用父类方法
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // 安全获取字符串的方法
    private String safeGetString(String key) {
        try {
            String result = I18n.format(key);
            return result != null ? result : key;
        } catch (Exception e) {
            return key;
        }
    }

    // 安全设置 MOTD 文本的方法
    private void safeSetMotdText() {
        try {
            if (MotdTextBox != null) {
                MotdTextBox.setText(MotdText != null ? MotdText : "");
            }
        } catch (Exception e) {
            System.err.println("[EasyLAN] Error setting MOTD text: " + e.getMessage());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            if (button instanceof ButtonUtil) {
                switch (button.id) {
                    case 0: // 返回
                        mc.displayGuiScreen(parentScreen);
                        break;
                    case 1: // 保存
                        saveConfig();
                        break;
                }
            } else if (button instanceof CheckBoxButtonUtil) {
                CheckBoxButtonUtil checkBox = (CheckBoxButtonUtil) button;

                // 只需要更新变量和配置，toggle() 已在 mousePressed 中调用
                boolean newState = checkBox.isChecked();

                switch (button.id) {
                    case 10: allowPVP = newState; break;
                    case 11: onlineMode = newState; break;
                    case 12: spawnAnimals = newState; break;
                    case 13: spawnNPCs = newState; break;
                    case 14: allowFlight = newState; break;
                    case 20: whiteList = newState; break;
                    case 21: BanCommand = newState; break;
                    case 22: OpCommand = newState; break;
                    case 23: SaveCommand = newState; break;
                    case 30: HttpAPI = newState; break;
                    case 31: LanOutput = newState; break;
                }

                // 立即保存配置
                saveConfig();
            }
        } catch (Exception e) {
            System.err.println("[EasyLAN] Error in actionPerformed: " + e.getMessage());
        }
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        try {
            if (MotdTextBox != null) {
                MotdTextBox.textboxKeyTyped(typedChar, keyCode);
                MotdText = MotdTextBox.getText();
            }
            super.keyTyped(typedChar, keyCode);
        } catch (Exception e) {
            System.err.println("[EasyLAN] Error in keyTyped: " + e.getMessage());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (GuiButton button : buttonList) {
            if (button.mousePressed(mc, mouseX, mouseY)) {
                ISound sound = new PositionedSoundRecord(
                    new ResourceLocation("random", "click"),  // 声音资源
                    1.0F,  // 音量
                    1.0F,  // 音调
                    0.0F,  // x坐标
                    0.0F,  // y坐标
                    0.0F   // z坐标
                );
                actionPerformed(button);
                return;
            }
        }
    }

    private void updateGuiConfig() {
        try {
            safeSetMotdText();

            for (GuiButton button : buttonList) {
                if (button instanceof CheckBoxButtonUtil) {
                    CheckBoxButtonUtil checkBox = (CheckBoxButtonUtil) button;
                    switch (button.id) {
                        case 10: checkBox.setChecked(allowPVP); break;
                        case 11: checkBox.setChecked(onlineMode); break;
                        case 12: checkBox.setChecked(spawnAnimals); break;
                        case 13: checkBox.setChecked(spawnNPCs); break;
                        case 14: checkBox.setChecked(allowFlight); break;
                        case 20: checkBox.setChecked(whiteList); break;
                        case 21: checkBox.setChecked(BanCommand); break;
                        case 22: checkBox.setChecked(OpCommand); break;
                        case 23: checkBox.setChecked(SaveCommand); break;
                        case 30: checkBox.setChecked(HttpAPI); break;
                        case 31: checkBox.setChecked(LanOutput); break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[EasyLAN] Error updating GUI config: " + e.getMessage());
        }
    }

    private void saveConfig() {
        try {
            // 保存 MOTD
            motd = MotdText != null ? MotdText : "";
            ConfigUtil.set("motd", motd);

            // 保存其他设置
            ConfigUtil.set("allowPVP", String.valueOf(allowPVP));
            ConfigUtil.set("onlineMode", String.valueOf(onlineMode));
            ConfigUtil.set("spawnAnimals", String.valueOf(spawnAnimals));
            ConfigUtil.set("spawnNPCs", String.valueOf(spawnNPCs));
            ConfigUtil.set("allowFlight", String.valueOf(allowFlight));
            ConfigUtil.set("whiteList", String.valueOf(whiteList));
            ConfigUtil.set("BanCommand", String.valueOf(BanCommand));
            ConfigUtil.set("OpCommand", String.valueOf(OpCommand));
            ConfigUtil.set("SaveCommand", String.valueOf(SaveCommand));
            ConfigUtil.set("HttpAPI", String.valueOf(HttpAPI));
            ConfigUtil.set("LanOutput", String.valueOf(LanOutput));

            ConfigUtil.save();
        } catch (Exception e) {
            System.err.println("[EasyLAN] Error saving config: " + e.getMessage());
        }
    }
}
