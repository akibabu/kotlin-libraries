plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.0"
}

dependencies {
    api("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.0")
    api("org.mongodb:bson-kotlinx:5.1.0") {
        because("Makes @SerialName have effect in DBOs")
    }
    compileOnly("org.apache.logging.log4j:log4j-api:2.23.1")
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
