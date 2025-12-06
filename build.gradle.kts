
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

group = "com.xiaoxian.easylan"
version = "1.5fix"
val archivesBaseName = "EasyLAN_Fix"

tasks.jar {
    archiveBaseName.set("EasyLAN_Fix")           // 基础名称
    archiveVersion.set("1.5fix")            // 版本号
    archiveClassifier.set("")       // 分类器（可选）
    archiveExtension.set("jar")            // 扩展名（默认）
}
