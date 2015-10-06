# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

#GreenDao
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

#Youmi
-dontwarn net.youmi.android.**
-keep class net.youmi.android.** {
    *;
}

#Fresco
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
# Keep native methods
-keepclassmembers class * {
    native <methods>;
}
-dontwarn okio.**
-dontwarn javax.annotation.**

#Jackson
-keep class com.fasterxml.jackson.databind.**{*;}
-keep class com.fasterxml.jackson.databind.annotation.**{*;}
-keep class com.fasterxml.jackson.databind.cfg.**{*;}
-keep class com.fasterxml.jackson.databind.deser.**{*;}
-keep class com.fasterxml.jackson.databind.deser.impl.**{*;}
-keep class com.fasterxml.jackson.databind.deser.std.**{*;}
-keep class com.fasterxml.jackson.databind.exc.**{*;}
-keep class com.fasterxml.jackson.databind.ext.**{*;}
-dontwarn com.fasterxml.jackson.databind.ext.**
-keep class com.fasterxml.jackson.databind.introspect.**{*;}
-keep class com.fasterxml.jackson.databind.jsonFormatVisitors.**{*;}
-keep class com.fasterxml.jackson.databind.jsonschema.**{*;}
-keep class com.fasterxml.jackson.databind.jsontype.**{*;}
-keep class com.fasterxml.jackson.databind.jsontype.impl.**{*;}
-keep class com.fasterxml.jackson.databind.module.**{*;}
-keep class com.fasterxml.jackson.databind.node.**{*;}
-keep class com.fasterxml.jackson.databind.ser.**{*;}
-keep class com.fasterxml.jackson.databind.ser.impl.**{*;}
-keep class com.fasterxml.jackson.databind.ser.std.**{*;}
-keep class com.fasterxml.jackson.databind.type.**{*;}
-keep class com.fasterxml.jackson.databind.util.**{*;}

-keep class com.fasterxml.jackson.core.**{*;}
-keep class com.fasterxml.jackson.core.base.**{*;}
-keep class com.fasterxml.jackson.core.format.**{*;}
-keep class com.fasterxml.jackson.core.io.**{*;}
-keep class com.fasterxml.jackson.core.json.**{*;}
-keep class com.fasterxml.jackson.core.sym.**{*;}
-keep class com.fasterxml.jackson.core.type.**{*;}
-keep class com.fasterxml.jackson.core.util.**{*;}

-keep class com.fasterxml.jackson.annotation.**{*;}

#jsoup
-keep class org.jsoup.** {*;}
-keep class org.jsoup.examples.** {*;}
-keep class org.jsoup.helper.** {*;}
-keep class org.jsoup.nodes.** {*;}
-keep class org.jsoup.parser.** {*;}
-keep class org.jsoup.safety.** {*;}
-keep class org.jsoup.select.** {*;}

