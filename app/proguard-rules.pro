# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\androidStudio\sdk/tools/proguard/proguard-android.txt
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

#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#忽略警告
-ignorewarning

#如果引用了v4或者v7包
-dontwarn android.support.**

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
#-keepclasseswithmembers class * {
#    public (android.content.Context, android.util.AttributeSet);
#}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#调试信息（源码的行号、源文件信息等）
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#不混淆资源类
#-keepclassmembers class **.R$* {
#    public static ;
#}

#GreenDao DB
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

#保留@Bind注解  ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# 保留eventbus中的类,以及我们的类中的onevent方法:
#eventbus
-dontwarn de.greenrobot.event.**
-keep class de.greenrobot.event.** { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}


# 记录生成的日志数据,gradle build时在本项目根目录输出 apk 包内所有 class 的内部结构
-dump class_files.txt
# 未混淆的类和成员
#-printseeds seeds.txt
# 列出从 apk 中删除的代码
#-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt


#- - - - - - - - -  百度地图Api  - - - - - - - - - - - #
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

#- - - - - - - - - 微信分享 Api  - - - - - - - - - - - - #
-keep class com.tencent.** { *;}
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** { *;}
-keep class com.tencent.mm.sdk.** { *;}

#- - - - - - - - - 百度统计 Api  - - - - - - - - - - - - #
-keep class com.baidu.bottom.** { *; }
-keep class com.baidu.kirin.** { *; }
-keep class com.baidu.mobstat.** { *; }

-keepattributes EnclosingMethod

#- - - - - - - - - 微信支付 Api  - - - - - - - - - - - - #
-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.tencent.wxop.** { *; }
-keep class com.tencent.mm.sdk.** { *; }
