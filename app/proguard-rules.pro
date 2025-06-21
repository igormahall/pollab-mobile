# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

## Manter nomes das classes de modelo de dados para que o Gson (JSON) funcione.
## Garanta que o nome do pacote está correto.
-keep class com.example.app.data.** { *; }

## Regra adicional para garantir a serialização/deserialização do GSON.
-keep public class * extends com.google.gson.TypeAdapter

## Regras comuns para manter o Retrofit e Coroutines funcionando corretamente em release.
-keep class retrofit2.Retrofit { *; }
-keep class * extends retrofit2.Converter { *; }
-keep class * extends retrofit2.CallAdapter { *; }
-keep interface retrofit2.http.** { *; }

-dontwarn org.jetbrains.annotations.**
-dontwarn kotlin.jvm.internal.**

# Manter as assinaturas genéricas de Call e Response do Retrofit
-keep,allowobfuscation,allowshrinking interface retrofit2.Call { *; }
-keep,allowobfuscation,allowshrinking class retrofit2.Response { *; }

# Manter continuations (importante para funções suspend)
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation { *; }

# Manter métodos anotados com HTTP (para refletir e inferir tipos genéricos)
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ==== REGRAS ADICIONAIS PARA COMPOSE E KOTLIN METADATA ====

# Manter metadata Kotlin (necessário para Compose e reflexão)
-keep class kotlin.Metadata { *; }

# Manter todas as classes do runtime do Compose
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.**      { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material.**    { *; }

# Manter métodos anotados com @Composable
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Manter suas data classes usadas pelo Gson (caso estejam em outro pacote, ajuste o path)
-keep class com.example.app.data.** { *; }

# Manter singletons de rede ou builders via reflection (se houver)
-keep class com.example.app.network.** { *; }
