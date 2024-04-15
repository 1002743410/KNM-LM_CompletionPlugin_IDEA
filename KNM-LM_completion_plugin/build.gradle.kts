plugins {
    id("java")
    id("org.jetbrains.intellij") version "0.4.18"
}

group = "com.knmlm"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://maven.aliyun.com/nexus/content/groups/public/")
    }
    mavenCentral()
}

dependencies {
    implementation(group = "com.alibaba", name = "fastjson", version = "1.2.62")
    implementation(group = "org.apache.httpcomponents", name = "httpclient", version = "4.5.6")
    implementation(fileTree("lib") { include("*.jar") })
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

intellij {
    version = "2020.2"
    plugins = listOf("java")
}

patchPluginXml {
    changeNotes """
      Add change notes here.<br>
      <em>most HTML tags may be used</em>"""
}
