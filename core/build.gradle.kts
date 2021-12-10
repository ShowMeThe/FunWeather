import com.show.version.VersionPlugin

val kotlin_version: String by extra

plugins{
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("app-version")
    id("core-dependency")
}
apply {
    plugin("kotlin-android")
}

android{


    buildTypes {
        findByName("release")?.apply {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),file("proguard-rules.pro"))
        }
    }

    dependencies {
        api(fileTree(baseDir = "libs"){
            include("*.jar","*.aar")
        })
    }
}


repositories {
    mavenCentral()
}