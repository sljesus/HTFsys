# 📋 Pasos Exactos para Crear Proyecto Android HTFsystem - Versión Sincronizada

## 🎯 Objetivo
Crear una aplicación Android nativa funcional llamada **HTFsystem** siguiendo la documentación sincronizada y evitando errores conocidos.

## 🛠️ Requisitos Previos
- Windows 10/11
- Scoop instalado (gestor de paquetes para Windows)
- Android Studio (opcional, para desarrollo posterior)
- Conexión a internet

---

## 📝 NOTA IMPORTANTE
Este documento está sincronizado con:
- **Nombre del proyecto:** HTFsystem
- **Namespace:** com.htf.system
- **Configuración Gradle:** 8.6 + AGP 8.2.2 + Kotlin 1.8.10

---

## 📝 PASO 1: Crear Estructura de Carpetas

### 1.1 Crear carpeta principal del proyecto
```powershell
# En PowerShell, navegar al escritorio
cd C:\Users\ASUS-B\Desktop

# Crear carpeta del proyecto
mkdir HTFsystem
```

### 1.2 Crear estructura Android completa
```powershell
# Entrar a la carpeta del proyecto
cd HTFsystem

# Crear estructura de carpetas Android
mkdir app\src\main\java\com\htf\system
mkdir app\src\main\res\layout
mkdir app\src\main\res\values
mkdir app\src\main\res\mipmap
mkdir gradle\wrapper
mkdir app\src\main\res\mipmap-anydpi-v26
```

---

## 📝 PASO 2: Configurar Archivos Gradle

### 2.1 Crear gradle.properties
```powershell
# Contenido del archivo:
android.useAndroidX=true
android.enableJetifier=true
```

### 2.2 Crear settings.gradle
```powershell
# Contenido del archivo:
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

rootProject.name = 'HTFsystem'
include ':app'
```

### 2.3 Crear build.gradle (raíz)
```powershell
# Contenido del archivo:
plugins {
    id 'com.android.application' version '8.2.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.8.10' apply false
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.8.10' apply false
}
```

### 2.4 Crear app/build.gradle
```powershell
# Contenido del archivo:
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.htf.system'
    compileSdk 34

    defaultConfig {
        applicationId "com.htf.system"
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

### 2.5 Crear gradle-wrapper.properties
```powershell
# Contenido del archivo:
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.6-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

---

## 📝 PASO 3: Crear AndroidManifest.xml

### 3.1 Crear archivo de manifiesto
```powershell
# Ruta: app/src/main/AndroidManifest.xml
# Contenido:
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.HTFsystem"
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

---

## 📝 PASO 4: Crear Código Kotlin

### 4.1 Crear MainActivity.kt
```powershell
# Ruta: app/src/main/java/com/htf/system/MainActivity.kt
# Contenido con comentarios para programadores jr
package com.htf.system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.htf.system.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    
    fun onButtonClick(view: android.view.View) {
        // TODO: Implementar acción del botón
    }
}
```

### 4.2 Crear MyApplication.kt
```powershell
# Ruta: app/src/main/java/com/htf/system/MyApplication.kt
# Contenido con comentarios explicativos
package com.htf.system

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // TODO: Inicializar componentes globales aquí
    }
}
```

---

## 📝 PASO 5: Crear Layout y Recursos

### 5.1 Crear activity_main.xml
```powershell
# Ruta: app/src/main/res/layout/activity_main.xml
# Contenido con ConstraintLayout y comentarios
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/helloWorld"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_world"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginTop="32dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <Button
        android:id="@+id/actionButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="@string/action_button"
        android:onClick="onButtonClick"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="32dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 5.2 Crear strings.xml
```powershell
# Ruta: app/src/main/res/values/strings.xml
# Contenido con textos centralizados
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">HTFsystem</string>
    <string name="hello_world">Bienvenido a HTFsystem</string>
    <string name="app_description">Sistema de gestión para administración de membresías y asignaciones HTF</string>
    <string name="action_button">Iniciar Sistema</string>
    <string name="initial_status">Sistema listo para operar</string>
</resources>
```

### 5.3 Crear colors.xml
```powershell
# Ruta: app/src/main/res/values/colors.xml
# Contenido con paleta de colores HTF
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary_color">#1976D2</color>
    <color name="primary_dark_color">#0D47A1</color>
    <color name="primary_light_color">#BBDEFB</color>
    <color name="accent_color">#FF5722</color>
    <color name="primary_text_color">#212121</color>
    <color name="secondary_text_color">#757575</color>
    <color name="background_color">#FAFAFA</color>
    <color name="surface_color">#FFFFFF</color>
</resources>
```

### 5.4 Crear themes.xml
```powershell
# Ruta: app/src/main/res/values/themes.xml
# Contenido con Material Design 3
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.HTFsystem" parent="Theme.Material3.DayNight">
        <item name="colorPrimary">@color/primary_color</item>
        <item name="colorOnPrimary">@color/surface_color</item>
        <item name="colorPrimaryContainer">@color/primary_light_color</item>
        <item name="colorOnPrimaryContainer">@color/primary_dark_color</item>
        <item name="colorSecondary">@color/accent_color</item>
        <item name="colorOnSecondary">@color/surface_color</item>
        <item name="android:colorBackground">@color/background_color</item>
        <item name="colorOnBackground">@color/primary_text_color</item>
        <item name="colorSurface">@color/surface_color</item>
        <item name="colorOnSurface">@color/primary_text_color</item>
        <item name="android:statusBarColor">@color/primary_dark_color</item>
    </style>
</resources>
```

---

## 📝 PASO 6: Crear Íconos de la Aplicación

### 6.1 Crear íconos adaptativos
```powershell
# Crear ic_launcher_foreground.xml
# Ruta: app/src/main/res/drawable/ic_launcher_foreground.xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="@color/primary_color"
        android:pathData="M0,0h108v108h-108z" />
    <path
        android:fillColor="@android:color/white"
        android:pathData="M20,40h10v28h-10z" />
    <path
        android:fillColor="@android:color/white"
        android:pathData="M40,40h8l12,28h-8l-2,-6h-10l-2,6h-8z M47,58h6l-3,-8z" />
    <path
        android:fillColor="@android:color/white"
        android:pathData="M65,40h10v20h10v8h-20z" />
</vector>
```

```powershell
# Crear ic_launcher_background.xml
# Ruta: app/src/main/res/drawable/ic_launcher_background.xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="@color/primary_color"
        android:pathData="M54,0 A54,54 0 1,1 54,108 A54,54 0 1,1 54,0z" />
</vector>
```

### 6.2 Crear referencias de íconos
```powershell
# Crear ic_launcher.xml
# Ruta: app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>

# Crear ic_launcher_round.xml
# Ruta: app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
```

---

## 📝 PASO 7: Crear Scripts Gradle

### 7.1 Crear gradlew (Linux/Mac)
```powershell
# Descargar desde: https://github.com/gradle/gradle/blob/v8.6.0/gradlew
# O copiar el contenido estándar del script Gradle wrapper
```

### 7.2 Crear gradlew.bat (Windows)
```powershell
# Descargar desde: https://github.com/gradle/gradle/blob/v8.6.0/gradlew.bat
# O copiar el contenido estándar del script Gradle wrapper para Windows
```

---

## 📝 PASO 8: Instalar Gradle con Scoop (CRÍTICO)

### 8.1 Instalar Gradle 8.6 específico
```powershell
# IMPORTANTE: No usar gradle 9.x - causa errores de compatibilidad
scoop install gradle@8.6

# Verificar instalación
gradle --version
# Debe mostrar: Gradle 8.6
```

### 8.2 Crear Gradle Wrapper
```powershell
# Dentro de la carpeta del proyecto
cd C:\Users\ASUS-B\Desktop\HTFsystem

# Crear wrapper con la versión correcta
gradle wrapper --gradle-version 8.6

# Esto descargará automáticamente el gradle-wrapper.jar
```

---

## 📝 PASO 9: Generar APK

### 9.1 Compilar y generar APK
```powershell
# En la carpeta del proyecto
cd C:\Users\ASUS-B\Desktop\HTFsystem

# Generar APK debug
./gradlew.bat assembleDebug

# O alternativamente:
gradlew.bat assembleDebug
```

### 9.2 Verificar APK generado
```powershell
# El APK se genera en:
app\build\outputs\apk\debug\app-debug.apk

# Verificar que existe:
dir app\build\outputs\apk\debug\
```

---

## 📝 PASO 10: Archivos Adicionales

### 10.1 Crear .gitignore
```powershell
# Contenido completo para Android
.gradle/
app/build/
build/
*.apk
*.aar
*.class
*.dex
.idea/
*.iml
local.properties
*.log
.DS_Store
Thumbs.db
*.keystore
*.jks
```

### 10.2 Crear README.md
```powershell
# Documentación del proyecto con instrucciones de uso
```

---

## ✅ Verificación Final

### Verificar estructura completa:
```powershell
# Estructura esperada:
HTFsystem/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/htf/system/
│       │   ├── MainActivity.kt
│       │   └── MyApplication.kt
│       └── res/
│           ├── layout/activity_main.xml
│           ├── values/strings.xml
│           ├── values/colors.xml
│           ├── values/themes.xml
│           ├── drawable/ic_launcher_*.xml
│           └── mipmap-anydpi-v26/ic_launcher*.xml
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── .gitignore
```

### Verificar APK:
```powershell
# El APK debe existir y tener ~5-6 MB
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🚀 Para Instalar y Probar

### Instalar en dispositivo:
```powershell
# Conectar dispositivo Android con USB debugging activado
adb install app/build/outputs/apk/debug/app-debug.apk

# Ejecutar la app
adb shell am start -n com.htf.system/.MainActivity
```

### Abrir en Android Studio:
1. File → Open
2. Seleccionar carpeta `C:\Users\ASUS-B\Desktop\HTFsystem`
3. Esperar sincronización Gradle
4. Build → Build APK(s)

---

## ⚠️ Errores Comunes y Soluciones

### Error: "gradle no se reconoce"
**Solución:** Usar `scoop install gradle@8.6` (no versión 9.x)

### Error: "resource drawable/ic_launcher_foreground not found"
**Solución:** Crear los archivos de íconos como se muestra en el PASO 6

### Error: "style Theme.SplashScreen not found"
**Solución:** Eliminar referencias a Splash Screen en themes.xml

### Error: "BUILD FAILED con SelfResolvingDependency"
**Solución:** Usar Gradle 8.6 específico, no 9.x

---

## 🎯 Resultado Esperado

- **APK funcional:** `app-debug.apk` (~5.9 MB)
- **App ejecutable:** Se abre y muestra "Bienvenido a HTFsystem"
- **Código comentado:** Explicaciones para programadores jr
- **Estructura completa:** Lista para desarrollo de funcionalidades HTF
- **Configuración estable:** Gradle 8.6 + AGP 8.2.2 + Kotlin 1.8.10

¡Proyecto listo para continuar desarrollo de funcionalidades de gestión HTF!
