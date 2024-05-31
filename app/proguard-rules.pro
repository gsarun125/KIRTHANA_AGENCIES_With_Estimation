-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Keep application class
-keep class com.ka.billingsystem.crash.MyApplication { *; }

-keep class com.ka.billingsystem.services.MyBroadcastReceiver { *; }

# Keep service
-keep class com.ka.billingsystem.services.LogoutService { *; }

# Keep FileProvider configuration
-keep class androidx.core.content.FileProvider { *; }

# Keep permissions
-keep class android.** { *; }
-keepclassmembers class android.** { *; }

# Keep generated XML files
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep resource names
-keepclassmembers class **.R$* {
    public static final int *;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.shockwave.**
# Keep attributes of classes and methods accessed via reflection
-keepattributes Signature, InnerClasses, EnclosingMethod

# Preserve annotations
-keepattributes *Annotation*

# Keep all classes in the given package and its subpackages
#-keep class com.ka.billingsystem.** { *; }

# Keep all classes in third-party libraries (replace 'library_package' with the actual package name)
-keep class library_package.** { *; }
-keep class org.apache.commons.compress.archivers.** { *; }
-keep class org.apache.commons.compress.utils.IOUtils { *; }
# Keep enum constants
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# Add any additional keep rules specific to your project and third-party libraries here
-dontwarn org.bouncycastle.asn1.ASN1Encodable
-dontwarn org.bouncycastle.asn1.ASN1InputStream
-dontwarn org.bouncycastle.asn1.ASN1Integer
-dontwarn org.bouncycastle.asn1.ASN1ObjectIdentifier
-dontwarn org.bouncycastle.asn1.ASN1OctetString
-dontwarn org.bouncycastle.asn1.ASN1OutputStream
-dontwarn org.bouncycastle.asn1.ASN1Primitive
-dontwarn org.bouncycastle.asn1.ASN1Set
-dontwarn org.bouncycastle.asn1.DEROctetString
-dontwarn org.bouncycastle.asn1.DERSet
-dontwarn org.bouncycastle.asn1.cms.ContentInfo
-dontwarn org.bouncycastle.asn1.cms.EncryptedContentInfo
-dontwarn org.bouncycastle.asn1.cms.EnvelopedData
-dontwarn org.bouncycastle.asn1.cms.IssuerAndSerialNumber
-dontwarn org.bouncycastle.asn1.cms.KeyTransRecipientInfo
-dontwarn org.bouncycastle.asn1.cms.OriginatorInfo
-dontwarn org.bouncycastle.asn1.cms.RecipientIdentifier
-dontwarn org.bouncycastle.asn1.cms.RecipientInfo
-dontwarn org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
-dontwarn org.bouncycastle.asn1.x500.X500Name
-dontwarn org.bouncycastle.asn1.x509.AlgorithmIdentifier
-dontwarn org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
-dontwarn org.bouncycastle.asn1.x509.TBSCertificateStructure
-dontwarn org.bouncycastle.cert.X509CertificateHolder
-dontwarn org.bouncycastle.cms.CMSEnvelopedData
-dontwarn org.bouncycastle.cms.Recipient
-dontwarn org.bouncycastle.cms.RecipientId
-dontwarn org.bouncycastle.cms.RecipientInformation
-dontwarn org.bouncycastle.cms.RecipientInformationStore
-dontwarn org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient
-dontwarn org.bouncycastle.cms.jcajce.JceKeyTransRecipient
-dontwarn org.bouncycastle.crypto.BlockCipher
-dontwarn org.bouncycastle.crypto.CipherParameters
-dontwarn org.bouncycastle.crypto.engines.AESFastEngine
-dontwarn org.bouncycastle.crypto.modes.CBCBlockCipher
-dontwarn org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
-dontwarn org.bouncycastle.crypto.params.KeyParameter
-dontwarn org.bouncycastle.crypto.params.ParametersWithIV
-dontwarn org.tukaani.xz.ARMOptions
-dontwarn org.tukaani.xz.ARMThumbOptions
-dontwarn org.tukaani.xz.DeltaOptions
-dontwarn org.tukaani.xz.FilterOptions
-dontwarn org.tukaani.xz.FinishableOutputStream
-dontwarn org.tukaani.xz.FinishableWrapperOutputStream
-dontwarn org.tukaani.xz.IA64Options
-dontwarn org.tukaani.xz.LZMA2InputStream
-dontwarn org.tukaani.xz.LZMA2Options
-dontwarn org.tukaani.xz.LZMAInputStream
-dontwarn org.tukaani.xz.LZMAOutputStream
-dontwarn org.tukaani.xz.PowerPCOptions
-dontwarn org.tukaani.xz.SPARCOptions
-dontwarn org.tukaani.xz.UnsupportedOptionsException
-dontwarn org.tukaani.xz.X86Options