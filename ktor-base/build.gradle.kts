val ktorVersion: String = "2.3.10"

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.0"
}

dependencies {
    api("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    api("io.ktor:ktor-server-core-jvm:$ktorVersion")
    api("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    api("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    api("io.ktor:ktor-server-status-pages:$ktorVersion")
    api("io.ktor:ktor-server-caching-headers-jvm:$ktorVersion")
    api("io.ktor:ktor-server-compression-jvm:$ktorVersion")
    api("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
}

kotlin {
    jvmToolchain(22)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/akibabu/kotlin-libraries")
            credentials {
                username = "akibabu"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
