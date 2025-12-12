plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.sugarspoon.sentinel"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    task<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        from(tasks.create("emptyJavadoc", Javadoc::class))
    }

    task<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val pomName = "Sentinel"
val pomDescription = "Uma biblioteca Android para detecção de anomalias e possíveis fraudes em tempo real."
val pomUrl = "https://github.com/seu-usuario/sentinel" // <-- SUBSTITUA
val pomScmUrl = "https://github.com/seu-usuario/sentinel.git" // <-- SUBSTITUA
val pomLicenseName = "The Apache License, Version 2.0"
val pomLicenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
val pomDeveloperId = "seu-usuario" // <-- SUBSTITUA
val pomDeveloperName = "Seu Nome" // <-- SUBSTITUA
val pomDeveloperEmail = "seu-email@example.com" // <-- SUBSTITUA

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.core:core-ktx:1.10.1") // Adicionada a dependência que faltava
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.sugarspoon"
                artifactId = "sentinel"
                version = "1.0.0"

                from(components["release"])

                artifact(tasks["sourcesJar"])
                artifact(tasks["javadocJar"])

                pom {
                    name.set(pomName)
                    description.set(pomDescription)
                    url.set(pomUrl)
                    licenses {
                        license {
                            name.set(pomLicenseName)
                            url.set(pomLicenseUrl)
                        }
                    }
                    developers {
                        developer {
                            id.set(pomDeveloperId)
                            name.set(pomDeveloperName)
                            email.set(pomDeveloperEmail)
                        }
                    }
                    scm {
                        connection.set(pomScmUrl)
                        developerConnection.set(pomScmUrl)
                        url.set(pomUrl)
                    }
                }
            }
        }
        repositories {
            maven {
                name = "sonatype"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = project.findProperty("sonatypeUsername") as? String
                    password = project.findProperty("sonatypePassword") as? String
                }
            }
        }
    }

    signing {
        sign(publishing.publications["release"])
    }
}
