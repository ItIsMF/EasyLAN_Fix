package org.xiaoxian.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.lwjgl.input.Keyboard;
import org.xiaoxian.util.ConfigUtil;
import org.xiaoxian.util.TextBoxUtil;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

public class GuiShareToLanEdit {

    // 使用 final 修饰确保不会被重新赋值为 null
    public static volatile String PortText = "";
    public static volatile String PortWarningText = "";
    public static volatile String MaxPlayerText = "20";  // 给定默认值
    public static volatile String MaxPlayerWarningText = "";

    // 使用 volatile 确保线程安全
    public static volatile GuiTextField PortTextBox;
    public static volatile GuiTextField MaxPlayerBox;

    @SubscribeEvent
    public void onGuiOpenEvent(GuiOpenEvent event) {
        if (event.gui instanceof GuiShareToLan) {
            event.gui = new GuiShareToLanModified(new GuiIngameMenu());
        }
    }

    public static class GuiShareToLanModified extends GuiShareToLan {

        public GuiShareToLanModified(GuiScreen parentScreen) {
            super(parentScreen);
            // 初始化配置值，确保安全
            initializeConfigValues();
        }

        // 添加配置初始化方法
        private void initializeConfigValues() {
            try {
                String portFromConfig = ConfigUtil.get("Port");
                PortText = getSafeString(portFromConfig, "");

                String maxPlayerFromConfig = ConfigUtil.get("MaxPlayer");
                MaxPlayerText = getSafeString(maxPlayerFromConfig, "20");

            } catch (Exception e) {
                System.err.println("[EasyLAN] Error loading config: " + e.getMessage());
                PortText = "";
                MaxPlayerText = "20";
            }
        }

        // 安全字符串获取方法
        private String getSafeString(String value, String defaultValue) {
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return value;
        }

        // 安全设置文本方法
        private void safeSetText(GuiTextField textField, String text) {
            if (textField != null) {
                textField.setText(text != null ? text : "");
            }
        }

        @Override
        public void initGui() {
            super.initGui();

            try {
                // 创建端口文本框
                PortTextBox = new TextBoxUtil(4, mc.fontRenderer, this.width / 2 - 155, this.height - 70, 145, 20);
                PortTextBox.setMaxStringLength(5);
                safeSetText(PortTextBox, PortText);  // 使用安全方法

                // 创建最大玩家数文本框
                MaxPlayerBox = new TextBoxUtil(5, mc.fontRenderer, this.width / 2 + 5, this.height - 70, 145, 20);
                MaxPlayerBox.setMaxStringLength(6);
                safeSetText(MaxPlayerBox, MaxPlayerText);  // 使用安全方法

                // 更新按钮状态
                updateButtonState();

            } catch (Exception e) {
                System.err.println("[EasyLAN] Error in initGui: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void updateButtonState() {
            try {
                GuiButton button101 = findButton();
                if (button101 != null) {
                    String portText = PortTextBox != null ? PortTextBox.getText() : "";
                    String maxPlayerText = MaxPlayerBox != null ? MaxPlayerBox.getText() : "20";

                    button101.enabled = checkPortAndEnableButton(portText) &&
                        checkMaxPlayerAndEnableButton(maxPlayerText);
                }
            } catch (Exception e) {
                System.err.println("[EasyLAN] Error updating button state: " + e.getMessage());
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);

            try {
                if (PortTextBox != null) PortTextBox.drawTextBox();
                if (MaxPlayerBox != null) MaxPlayerBox.drawTextBox();

                drawString(mc.fontRenderer, I18n.format("easylan.text.port"), this.width / 2 - 155, this.height - 85, 0xFFFFFF);
                drawString(mc.fontRenderer, PortWarningText != null ? PortWarningText : "", this.width / 2 - 155, this.height - 45, 0xFF0000);
                drawString(mc.fontRenderer, I18n.format("easylan.text.maxplayer"), this.width / 2 + 5, this.height - 85, 0xFFFFFF);
                drawString(mc.fontRenderer, MaxPlayerWarningText != null ? MaxPlayerWarningText : "", this.width / 2 + 5, this.height - 45, 0xFF0000);
            } catch (Exception e) {
                System.err.println("[EasyLAN] Error in drawScreen: " + e.getMessage());
            }
        }

        @Override
        protected void actionPerformed(@Nonnull GuiButton button) {
            super.actionPerformed(button);

            try {
                if (button.id == 101) {
                    // 安全保存配置
                    ConfigUtil.set("Port", PortText != null ? PortText : "");
                    ConfigUtil.set("MaxPlayer", MaxPlayerText != null ? MaxPlayerText : "20");
                    ConfigUtil.save();
                }

                updateButtonState();
            } catch (Exception e) {
                System.err.println("[EasyLAN] Error in actionPerformed: " + e.getMessage());
            }
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) {
            try {
                // 安全处理键盘输入
                if (PortTextBox != null) PortTextBox.textboxKeyTyped(typedChar, keyCode);
                if (MaxPlayerBox != null) MaxPlayerBox.textboxKeyTyped(typedChar, keyCode);

                String previousText = PortTextBox != null ? PortTextBox.getText() : "";
                String previousMaxPlayerText = MaxPlayerBox != null ? MaxPlayerBox.getText() : "20";

                if (Character.isDigit(typedChar) || keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_ESCAPE) {
                    if (Character.isDigit(typedChar)) {
                        try {
                            String newPortText = PortTextBox != null ? PortTextBox.getText() : "";
                            String newMaxPlayerText = MaxPlayerBox != null ? MaxPlayerBox.getText() : "20";

                            // 端口验证
                            if (!newPortText.isEmpty()) {
                                int newPort = Integer.parseInt(newPortText);
                                if (!(newPort >= 100 && newPort <= 65535)) {
                                    if (PortTextBox != null) PortTextBox.setText(previousText);
                                }
                            }

                            // 最大玩家数验证
                            if (!newMaxPlayerText.isEmpty()) {
                                int newMaxPlayer = Integer.parseInt(newMaxPlayerText);
                                if (!(newMaxPlayer >= 2 && newMaxPlayer <= 500000)) {
                                    if (MaxPlayerBox != null) MaxPlayerBox.setText(previousMaxPlayerText);
                                }
                            }

                        } catch (NumberFormatException e) {
                            if (PortTextBox != null) PortTextBox.setText(previousText);
                            if (MaxPlayerBox != null) MaxPlayerBox.setText(previousMaxPlayerText);
                        }
                    } else if (keyCode == Keyboard.KEY_ESCAPE) {
                        mc.displayGuiScreen(null);
                        if (mc.currentScreen == null) mc.setIngameFocus();
                    }
                }

                // 更新按钮状态
                updateButtonState();

                // 安全更新全局变量
                PortText = PortTextBox != null ? PortTextBox.getText() : "";
                MaxPlayerText = MaxPlayerBox != null ? MaxPlayerBox.getText() : "20";

            } catch (Exception e) {
                System.err.println("[EasyLAN] Error in keyTyped: " + e.getMessage());
            }
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            try {
                if (PortTextBox != null) {
                    PortTextBox.mouseClicked(mouseX, mouseY, mouseButton);
                    PortText = PortTextBox.getText();
                }

                if (MaxPlayerBox != null) {
                    MaxPlayerBox.mouseClicked(mouseX, mouseY, mouseButton);
                    MaxPlayerText = MaxPlayerBox.getText();
                }

                super.mouseClicked(mouseX, mouseY, mouseButton);
            } catch (Exception e) {
                System.err.println("[EasyLAN] Error in mouseClicked: " + e.getMessage());
            }
        }

        private GuiButton findButton() {
            try {
                if (buttonList != null) {
                    for (GuiButton button : (List<GuiButton>) buttonList) {
                        if (button != null && button.id == 101) {
                            return button;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("[EasyLAN] Error finding button: " + e.getMessage());
            }
            return null;
        }

        private boolean checkPortAndEnableButton(String portText) {
            try {
                if (portText == null || portText.isEmpty()) {
                    PortWarningText = "";
                    return true;
                } else {
                    try {
                        int port = Integer.parseInt(portText);
                        boolean isPortAvailable = port >= 100 && port <= 65535 && isPortAvailable(port);
                        PortWarningText = isPortAvailable ? "" : I18n.format("easylan.text.port.used");

                        if (!(port >= 100 && port <= 65535)) {
                            PortWarningText = I18n.format("easylan.text.port.range");
                            return false;
                        }

                        return isPortAvailable;
                    } catch (NumberFormatException e) {
                        PortWarningText = I18n.format("easylan.text.port.invalid");
                        return false;
                    }
                }
            } catch (Exception e) {
                System.err.println("[EasyLAN] Error checking port: " + e.getMessage());
                return false;
            }
        }

        private boolean checkMaxPlayerAndEnableButton(String maxPlayerText) {
            try {
                if (maxPlayerText == null || maxPlayerText.isEmpty()) {
                    MaxPlayerWarningText = "";
                    return true;
                } else {
                    try {
                        int maxPlayer = Integer.parseInt(maxPlayerText);
                        boolean isValid = maxPlayer >= 2 && maxPlayer <= 500000;
                        MaxPlayerWarningText = isValid ? "" : I18n.format("easylan.text.maxplayer.range");
                        return isValid;
                    } catch (NumberFormatException e) {
                        MaxPlayerWarningText = I18n.format("easylan.text.maxplayer.invalid");
                        return false;
                    }
                }
            } catch (Exception e) {
                System.err.println("[EasyLAN] Error checking max player: " + e.getMessage());
                return false;
            }
        }

        private boolean isPortAvailable(int port) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}
