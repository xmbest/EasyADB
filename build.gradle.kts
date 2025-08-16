import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.xmbet"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public/")
    maven("https://maven.aliyun.com/repository/central")
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.android.tools.ddms:ddmlib:31.3.1")
    implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.57")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "EasyADB"
            packageVersion = "1.0.0"
        }
    }
}
