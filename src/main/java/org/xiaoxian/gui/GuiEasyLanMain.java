package org.xiaoxian.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.xiaoxian.util.ButtonUtil;
import org.xiaoxian.util.CheckBoxButtonUtil;
import org.xiaoxian.util.ConfigUtil;
import org.xiaoxian.util.TextBoxUtil;
import java.awt.*;
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

        // 游戏规则设置
        buttonList.add(new CheckBoxButtonUtil(10, this.width / 2 - 145, 60, allowPVP, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(11, this.width / 2 - 145, 85, onlineMode, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(12, this.width / 2 - 145, 110, spawnAnimals, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(13, this.width / 2 - 145, 135, spawnNPCs, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(14, this.width / 2 - 145, 160, allowFlight, 20, 20));

        // 指令支持
        buttonList.add(new CheckBoxButtonUtil(20, this.width / 2 - 25, 60, whiteList, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(21, this.width / 2 - 25, 85, BanCommand, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(22, this.width / 2 - 25, 110, OpCommand, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(23, this.width / 2 - 25, 135, SaveCommand, 20, 20));

        // 其他设置
        buttonList.add(new CheckBoxButtonUtil(30, this.width / 2 + 95, 60, HttpAPI, 20, 20));
        buttonList.add(new CheckBoxButtonUtil(31, this.width / 2 + 95, 85, LanOutput, 20, 20));

        // MOTD 文本框 - 添加安全初始化
        try {
            MotdTextBox = new TextBoxUtil(100, mc.fontRenderer, this.width / 2 - 70, 185, 230, 20);
            if (MotdTextBox != null) {
                MotdTextBox.setMaxStringLength(100);
                safeSetMotdText();  // 使用安全方法设置文本
            }
        } catch (Exception e) {
            System.err.println("[EasyLAN] Error creating MOTD text box: " + e.getMessage());
        }

        updateGuiConfig();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            this.drawDefaultBackground();

            // 安全绘制 MOTD 文本框
            if (MotdTextBox != null) {
                try {
                    MotdTextBox.drawTextBox();
                } catch (Exception e) {
                    System.err.println("[EasyLAN] Error drawing MOTD text box: " + e.getMessage());
                }
            }

            // 绘制标签文本 - 添加 null 保护
            safeDrawStrings();

            super.drawScreen(mouseX, mouseY, partialTicks);

        } catch (Exception e) {
            System.err.println("[EasyLAN] Critical error in drawScreen: " + e.getMessage());
            // 降级处理：至少绘制基本界面
            drawCenteredString(fontRendererObj, "GUI Error - Please restart", width/2, height/2, 0xFF0000);
        }
    }

    // 安全绘制字符串的方法
    private void safeDrawStrings() {
        try {
            drawString(mc.fontRenderer, safeGetString("easylan.title"), this.width / 2, 10, 0xFFFFFF);

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
            System.err.println("[EasyLAN] Error drawing strings: " + e.getMessage());
        }
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
                switch (button.id) {
                    case 10: allowPVP = checkBox.isChecked(); break;
                    case 11: onlineMode = checkBox.isChecked(); break;
                    case 12: spawnAnimals = checkBox.isChecked(); break;
                    case 13: spawnNPCs = checkBox.isChecked(); break;
                    case 14: allowFlight = checkBox.isChecked(); break;
                    case 20: whiteList = checkBox.isChecked(); break;
                    case 21: BanCommand = checkBox.isChecked(); break;
                    case 22: OpCommand = checkBox.isChecked(); break;
                    case 23: SaveCommand = checkBox.isChecked(); break;
                    case 30: HttpAPI = checkBox.isChecked(); break;
                    case 31: LanOutput = checkBox.isChecked(); break;
                }
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            if (MotdTextBox != null) {
                MotdTextBox.mouseClicked(mouseX, mouseY, mouseButton);
                MotdText = MotdTextBox.getText();
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception e) {
            System.err.println("[EasyLAN] Error in mouseClicked: " + e.getMessage());
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
