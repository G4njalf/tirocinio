import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val infuraUrl: String = localProperties.getProperty("INFURA_URL") ?: throw IllegalArgumentException("INFURA_URL property is required")
val privatekeyEnsurer: String = localProperties.getProperty("PRIVATE_KEY_ASSICURATORE") ?: throw IllegalArgumentException("PRIVATE_KEY_ASSICURATORE property is required")
val privatekeyEnsured: String = localProperties.getProperty("PRIVATE_KEY_ASSICURATO") ?: throw IllegalArgumentException("PRIVATE_KEY_ASSICURATO property is required")

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    buildFeatures{
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "INFURA_URL", "\"$infuraUrl\"")
        buildConfigField("String", "PRIVATE_KEY_ASSICURATORE", "\"$privatekeyEnsurer\"")
        buildConfigField("String", "PRIVATE_KEY_ASSICURATO", "\"$privatekeyEnsured\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/DISCLAIMER"
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("org.web3j:core:4.9.4")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation(platform("com.reown:android-bom:1.4.5"))
    implementation("com.reown:android-core")
    implementation("com.reown:appkit")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //implementation("com.walletconnect:android-core:1.17.0")
    //implementation("com.walletconnect:sign:1.17.0")

}