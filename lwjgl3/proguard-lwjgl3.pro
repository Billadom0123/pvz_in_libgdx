# ProGuard rules for LibGDX desktop (LWJGL3)
# Input/output are provided by the Gradle task. This file focuses on keep/dontwarn and attributes.

-verbose
# DO NOT disable preverify on Java 7+; we need proper StackMap frames
#-dontpreverify
# Keep optimizations off to be safe
-dontoptimize

# Keep useful debug info in stack traces and verification frames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,StackMap,StackMapTable
-renamesourcefileattribute SourceFile

# Entry point and launcher package: keep all to avoid verifier issues
-keep class com.example.plantsvszombies.lwjgl3.** { *; }

# Keep the whole LibGDX and LWJGL stacks intact (do not shrink/obfuscate)
-keep class com.badlogic.** { *; }
-keep class org.lwjgl.** { *; }
-keep class org.jcraft.** { *; }
-keep class org.slf4j.** { *; }

# Some projects use reflection on these; keep to be safe
-keep class com.badlogic.gdx.backends.lwjgl3.** { *; }
-keep class com.badlogic.gdx.graphics.g2d.** { *; }
-keep class com.badlogic.gdx.scenes.scene2d.** { *; }

# Ignore warnings coming from 3rd-party libs (module-info, service providers, etc.)
-dontwarn module-info
-dontwarn com.badlogic.**
-dontwarn org.lwjgl.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# If you dynamically load by class name from assets, consider keeping your own packages as names (uncomment):
# -keepnames class com.example.plantsvszombies.**

# Output reports (Gradle task overrides these to build/reports/proguard)
-printmapping proguard-mapping.txt
-printusage proguard-usage.txt
-printseeds proguard-seeds.txt

