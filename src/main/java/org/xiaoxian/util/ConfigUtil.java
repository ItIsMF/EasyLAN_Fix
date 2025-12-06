package org.xiaoxian.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import static org.xiaoxian.EasyLAN.*;

public class ConfigUtil {
    private static final String CONFIG_FILE = "config/easylan.cfg";
    private static final Properties properties = new Properties();

    public static void load() {
        File file = new File(CONFIG_FILE);

        if (file.exists()) {
            try (InputStream input = Files.newInputStream(file.toPath())) {
                properties.load(input);

                // 从配置文件加载值到全局变量
                loadPropertiesToGlobals();

                System.out.println("[EasyLAN] Configuration loaded successfully from: " + file.getAbsolutePath());

            } catch (IOException ex) {
                System.err.println("[EasyLAN] Error loading config file: " + ex.getMessage());
                ex.printStackTrace();
                setDefaultValues();
            }
        } else {
            System.out.println("[EasyLAN] Config file not found, creating default configuration");
            setDefaultValues();
            save();
        }
    }

    public static void save() {
        try {
            // 确保目录存在
            File configFile = new File(CONFIG_FILE);
            configFile.getParentFile().mkdirs();

            // 将全局变量的当前值保存到配置文件
            saveGlobalsToProperties();

            try (OutputStream output = Files.newOutputStream(Paths.get(CONFIG_FILE))) {
                properties.store(output, "EasyLAN configuration");
                System.out.println("[EasyLAN] Configuration saved successfully to: " + configFile.getAbsolutePath());
            }

        } catch (IOException ex) {
            System.err.println("[EasyLAN] Error saving config file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static void set(String key, String value) {
        properties.setProperty(key, value);
        System.out.println("[EasyLAN] Config set: " + key + " = " + value);
    }

    // 从全局变量保存到配置文件
    private static void saveGlobalsToProperties() {
        set("Http-Api", String.valueOf(HttpAPI));
        set("Lan-output", String.valueOf(LanOutput));
        set("pvp", String.valueOf(allowPVP));
        set("online-mode", String.valueOf(onlineMode));
        set("spawn-Animals", String.valueOf(spawnAnimals));
        set("spawn-NPCs", String.valueOf(spawnNPCs));
        set("allow-Flight", String.valueOf(allowFlight));
        set("whiteList", String.valueOf(whiteList));
        set("BanCommand", String.valueOf(BanCommand));
        set("OpCommand", String.valueOf(OpCommand));
        set("SaveCommand", String.valueOf(SaveCommand));
        set("Motd", motd);
        set("Port", CustomPort);
        set("MaxPlayer", CustomMaxPlayer);
    }

    // 从配置文件加载到全局变量
    private static void loadPropertiesToGlobals() {
        allowPVP = Boolean.parseBoolean(get("pvp"));
        onlineMode = Boolean.parseBoolean(get("online-mode"));
        spawnAnimals = Boolean.parseBoolean(get("spawn-Animals"));
        spawnNPCs = Boolean.parseBoolean(get("spawn-NPCs"));
        allowFlight = Boolean.parseBoolean(get("allow-Flight"));
        whiteList = Boolean.parseBoolean(get("whiteList"));
        BanCommand = Boolean.parseBoolean(get("BanCommand"));
        OpCommand = Boolean.parseBoolean(get("OpCommand"));
        SaveCommand = Boolean.parseBoolean(get("SaveCommand"));
        HttpAPI = Boolean.parseBoolean(get("Http-Api"));
        LanOutput = Boolean.parseBoolean(get("Lan-output"));
        CustomPort = get("Port");
        CustomMaxPlayer = get("MaxPlayer");
        motd = get("Motd");

        // 确保字符串值不为 null
        if (CustomPort == null) CustomPort = "25565";
        if (CustomMaxPlayer == null) CustomMaxPlayer = "20";
        if (motd == null) motd = "This is a Default EasyLAN Motd!";
    }

    private static void setDefaultValues() {
        HttpAPI = true;
        LanOutput = true;
        allowPVP = true;
        onlineMode = true;
        spawnAnimals = true;
        spawnNPCs = true;
        allowFlight = true;
        whiteList = false;
        BanCommand = false;
        OpCommand = false;
        SaveCommand = false;
        motd = "This is a Default EasyLAN Motd!";
        CustomPort = "25565";
        CustomMaxPlayer = "20";

        // 设置默认属性
        set("Http-Api", "true");
        set("Lan-output", "true");
        set("pvp", "true");
        set("online-mode", "true");
        set("spawn-Animals", "true");
        set("spawn-NPCs", "true");
        set("allow-Flight", "true");
        set("whiteList", "false");
        set("BanCommand", "false");
        set("OpCommand", "false");
        set("SaveCommand", "false");
        set("Motd", "This is a Default EasyLAN Motd!");
        set("Port", "25565");
        set("MaxPlayer", "20");
    }
}
