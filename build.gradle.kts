
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

group = "com.xiaoxian.easylan"
version = "1.6"
val archivesBaseName = "EasyLAN_Fix"

tasks.jar {
    manifest {
        attributes(
            "Manifest-Version" to "1.0"
        )
    }

    archiveBaseName.set(archivesBaseName)
    archiveVersion.set(version.toString())
    archiveClassifier.set("")
    archiveExtension.set("jar")
}
