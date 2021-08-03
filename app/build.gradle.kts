import com.show.version.getDateTime

plugins{
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("app-version")
    id("app-dependency")
}


android{
    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true

    val kotlinVersion :String by rootProject.extra

    signingConfigs{
        create("release"){
            storeFile = file("release\\key.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }
    }

    defaultConfig{
        applicationId = "com.show.weather"
        resConfigs("zh","zh-rCN")

        splits {
            abi {
                isEnable = true
                reset()
                include("arm64-v8a")
                isUniversalApk = false
            }
        }

    }

    buildTypes{

        findByName("release")?.apply {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),file("proguard-rules.pro"))
            signingConfig = signingConfigs.findByName("release")
        }

        findByName("debug")?.apply {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),file("proguard-rules.pro"))
            signingConfig = signingConfigs.findByName("release")
        }

    }


    applicationVariants.all {
        outputs.forEach { output ->
            val outputImp = output as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            var name = ""
            splits.abiFilters.forEach {
                name = "${name}-${it}"
            }
            outputImp.apply {
                outputFileName = "App-${buildType.name}${name}-${getDateTime()}.apk"
            }
        }
    }



    dependencies {
        implementation(fileTree(baseDir = "libs"){
            include("*.jar","*.aar")
        })
        implementation(project(":core"))
    }

}


