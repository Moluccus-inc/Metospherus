buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}
val psw by extra("12345678")
val key by extra("metospherus")