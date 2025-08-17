import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.xmbet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.aliyun.com/repository/public/")
    maven("https://maven.aliyun.com/repository/central")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.alibaba.fastjson2:fastjson2-kotlin:${properties["fastjson2.version"]}")
    implementation("com.android.tools.ddms:ddmlib:${properties["ddmlib.version"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${properties["kotlin.version"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlin.version"]}")
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation(project(":ddmlib"))
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
