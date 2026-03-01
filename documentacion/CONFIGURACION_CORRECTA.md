# Configuración Mínima para Proyecto Android Funcional

Basado en la experiencia y errores corregidos, esta es la configuración mínima y correcta para crear un proyecto Android que compile y genere APK sin errores.

## Estructura de Archivos Esencial

```
HTFadm/
├── app/
│   └── src/main/
│       ├── java/com/htf/admin/
│       │   ├── MainActivity.kt
│       │   └── MyApplication.kt
│       ├── res/
│       │   ├── layout/activity_main.xml
│       │   ├── values/strings.xml
│       │   └── mipmap/
│       └── AndroidManifest.xml
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle
├── app/build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── .gitignore
```

## Archivos de Configuración Clave

### 1. gradle.properties
```properties
android.useAndroidX=true
android.enableJetifier=true
```

### 2. settings.gradle
```gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = 'HTFAdmin'
include ':app'
```

### 3. build.gradle (proyecto)
```gradle
plugins {
    id 'com.android.application' version '8.2.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.8.10' apply false
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.8.10' apply false
}
```

### 4. app/build.gradle
```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.htf.admin'
    compileSdk 34

    defaultConfig {
        applicationId "com.htf.admin"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.13.1'
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

### 5. gradle-wrapper.properties
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.6-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

### 6. AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.HTFAdmin"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### 7. MainActivity.kt
```kotlin
package com.htf.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.htf.admin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.helloWorld.text = getString(R.string.hello_world)
    }
}
```

### 8. MyApplication.kt
```kotlin
package com.htf.admin

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
```

### 9. .gitignore
```
.gradle/
app/build/
build/
*.apk
*.jar
*.class
*.dex
*.aar
*.so
*.obj
*.lib
*.pdb
*.exe
*.dll
*.dylib
*.dSYM
*.DS_Store
*.swp
*.swo
*~
.idea/
*.iml
local.properties
*.log
*.hprof
```

## Comandos para Generar APK

```bash
# 1. Crear Gradle wrapper con versión correcta
gradle wrapper --gradle-version 8.6

# 2. Generar APK
./gradlew.bat assembleDebug

# 3. Instalar en dispositivo
adb install app/build/outputs/apk/debug/app-debug.apk

# 4. Ejecutar app
adb shell am start -n com.htf.admin/.MainActivity
```

## Versiones Compatibles Clave

- **Gradle:** 8.6 (NO usar 9.x)
- **Android Gradle Plugin:** 8.2.2
- **Kotlin:** 1.8.10
- **CompileSdk:** 34
- **TargetSdk:** 34
- **MinSdk:** 21

## Errores a Evitar (Basado en Experiencia)

1. **NO mezclar DSL** - Usar solo Groovy (.gradle), no Kotlin DSL (.kts)
2. **NO usar Gradle 9.x** - Incompatible con AGP 8.2.2
3. **SIEMPRE agregar android.useAndroidX=true** - Obligatorio para dependencias modernas
4. **NO incluir dataExtractionRules en manifest** - Causa error si archivos no existen
5. **NO especificar versiones de plugins en app/build.gradle** - Dejar que project build.gradle las gestione

## Resultado Esperado

Con esta configuración:
- Build exitoso en primer intento
- APK generado en `app/build/outputs/apk/debug/app-debug.apk`
- App funcional con "Hello World"
- Sin errores de compatibilidad
