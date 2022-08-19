//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package weblogic.security.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.AccessController;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Properties;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import weblogic.kernel.Kernel;
import weblogic.logging.Loggable;
import weblogic.management.configuration.ServerMBean;
import weblogic.management.provider.CommandLine;
import weblogic.management.provider.ManagementService;
import weblogic.security.SecurityLogger;
import weblogic.security.SSL.HostnameVerifier;
import weblogic.security.SSL.SSLClientInfo;
import weblogic.security.SSL.TrustManager;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.security.service.PrivilegedActions;
import weblogic.security.service.SecurityServiceManager;

public final class SSLSetup extends SSLSetupLogging {
    public static final int STANDARD_IO = 0;
    public static final int MUXING_IO = 1;
    public static final int LICENSE_NOT_CHECKED = -1;
    public static final int LICENSE_NONE = 0;
    public static final int LICENSE_DOMESTIC = 1;
    public static final int LICENSE_EXPORT = 2;
    private static final AuthenticatedSubject kernelId = (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
    public static final String FAILURE_DETAILS = "weblogic.security.ssl.failureDetails";
    private static boolean ioModelAccessed = false;
    private static int ioModel = 0;
    private static int licenseLevel = -1;
    private static int debugLevel = 0;
    private static boolean protocolVersionChecked = false;
    private static int protocolVersion = 3;
    private static boolean enforceConstraintsChecked = false;
    private static int enforceConstraints = 1;
    private static final String CERTICOM_DELEGATE = "com.bea.sslplus.CerticomSSLContext";
    private static final String RSA_DELEGATE = "com.rsa.ssl.WeblogicContextWrapper";
    private static Class sslDelegateClass = null;

    public SSLSetup() {
    }

    public static synchronized int getLicenseLevel() {
        if(licenseLevel > -1) {
            return licenseLevel;
        } else {
            licenseLevel = 1;
            String var0 = "com.bea.sslplus.CerticomSSLContext";
            info("Use Certicom SSL with Domestic strength");
            setSSLDelegate(var0);
            return licenseLevel;
        }
    }

    public static synchronized void initForServer() {
        setIOModel(1);
        info("Enabled muxing IO for SSL in server");
    }

    private static void setSSLDelegate(String var0) {
        try {
            sslDelegateClass = Class.forName(var0);
            if(!SSLContextDelegate.class.isAssignableFrom(sslDelegateClass)) {
                String var1 = "Cannot initialize SSL implementation. " + var0 + " does not implement " + SSLContextDelegate.class.getName();
                throw new IllegalArgumentException(var1);
            }
        } catch (ClassNotFoundException var3) {
            String var2 = SecurityLogger.getClassNotFound(var0);
            throw new IllegalArgumentException(var2, var3);
        }
    }

    static SSLContextDelegate getSSLDelegateInstance() {
        if(licenseLevel == -1) {
            getLicenseLevel();
        }

        String var1;
//        return new SSLContextDelegateImpl();

        try {
            return (SSLContextDelegate)sslDelegateClass.newInstance();
        } catch (IllegalAccessException var2) {
            var1 = SecurityLogger.getIllegalAccessOnContextWrapper(sslDelegateClass.getName());
            throw new RuntimeException(var1, var2);
        } catch (InstantiationException var3) {
            var1 = SecurityLogger.getInstantiationExcOnContextWrapper(sslDelegateClass.getName());
            throw new RuntimeException(var1, var3);
        }
    }

    public static int getIOModel() {
        ioModelAccessed = true;
        return ioModel;
    }

    public static boolean logSSLRejections() {
        if(Kernel.isApplet()) {
            return false;
        } else if(!Kernel.isServer()) {
            return true;
        } else {
            try {
                return ManagementService.getRuntimeAccess(kernelId).getServer().getSSL().isSSLRejectionLoggingEnabled();
            } catch (Exception var1) {
                info(var1, "Caught exception in SSLSetup.logSSLRejections");
                return false;
            }
        }
    }

    public static void setIOModel(int var0) {
        if(var0 != 0 && var0 != 1) {
            debug(2, "Attempt to change SSL IO model to invalid setting");
        } else if(ioModelAccessed) {
            debug(2, "Attempt to change SSL IO model after access");
        } else {
            ioModel = var0;
        }
    }

    public static int getProtocolVersion() {
        if(!protocolVersionChecked) {
            try {
                String var0 = CommandLine.getCommandLine().getSSLVersion();
                if(var0 != null) {
                    if(var0.equalsIgnoreCase("SSL3")) {
                        protocolVersion = 1;
                    } else if(var0.equalsIgnoreCase("TLS1")) {
                        protocolVersion = 0;
                    } else if(var0.equalsIgnoreCase("ALL")) {
                        protocolVersion = 3;
                    }
                }
            } catch (SecurityException var1) {
                ;
            }

            protocolVersionChecked = true;
        }

        return protocolVersion;
    }

    public static int getEnforceConstraints() {
        if(!enforceConstraintsChecked) {
            try {
                String var0 = CommandLine.getCommandLine().getSSLEnforcementConstraint();
                if(var0 != null) {
                    if(!var0.equalsIgnoreCase("off") && !var0.equalsIgnoreCase("false")) {
                        if(!var0.equalsIgnoreCase("strong") && !var0.equalsIgnoreCase("true")) {
                            if(var0.equalsIgnoreCase("strict")) {
                                enforceConstraints = 2;
                            }
                        } else {
                            enforceConstraints = 1;
                        }
                    } else {
                        enforceConstraints = 0;
                    }
                }
            } catch (SecurityException var1) {
                ;
            }

            enforceConstraintsChecked = true;
        }

        return enforceConstraints;
    }

    public static SSLContextWrapper getSSLContext() throws SocketException {
        return getSSLContext((SSLClientInfo)null);
    }

    public static SSLContextWrapper getSSLContext(SSLClientInfo var0) throws SocketException {
        SSLContextWrapper var1 = SSLContextWrapper.getInstance();
        if(!Kernel.isApplet()) {
            X509Certificate[] var2 = getTrustedCAs(var1);
            if(var2 != null) {
                try {
                    var1.addTrustedCA(var2);
                } catch (Exception var4) {
                    debug(2, var4, "Failure loading trusted CA list");
                }
            }
        }

//        if(var0 != null) {
//            applyInfo(var1, var0);
//        }

        return var1;
    }

    private static void applyInfo(SSLContextWrapper var0, SSLClientInfo var1) throws SocketException {
//        InputStream[] var2 = var1.getSSLClientCertificate();
//        if(var2 != null && var2.length >= 2) {
//            info("clientInfo has old style certificate and key");
//
//            try {
//                String var3 = var1.getSSLClientKeyPassword();
//                char[] var4 = null;
//                if(var3 != null) {
//                    var4 = var3.toCharArray();
//                }
//
//                PrivateKey var5 = var0.inputPrivateKey(var2[0], var4);
//                X509Certificate[] var6 = new X509Certificate[var2.length - 1];
//                CertificateFactory var7 = CertificateFactory.getInstance("X.509");
//
//                for(int var8 = 1; var8 < var2.length; ++var8) {
//                    var6[var8 - 1] = (X509Certificate)var7.generateCertificate(var2[var8]);
//                }
//
//                var0.addIdentity(var6, var5);
//                info("client identity added");
//            } catch (KeyManagementException var9) {
//                info(var9, "Problem accessing private key");
//                throw new SocketException(SecurityLogger.getProblemAccessingPrivateKey());
//            } catch (CertificateException var10) {
//                info(var10, "Problem with certificate chain");
//                throw new SocketException(SecurityLogger.getProblemWithCertificateChain(var10.getMessage()));
//            }
//        }
//
//        X509Certificate[] var11 = (X509Certificate[])var1.getClientLocalIdentityCert();
//        PrivateKey var12 = var1.getClientLocalIdentityKey();
//        if(var11 != null && var12 != null) {
//            info("clientInfo has new style certificate and key");
//            var0.addIdentity(var11, var12);
//        }
//
//        TrustManager var13 = var1.getTrustManager();
//        if(var13 != null) {
//            info("clientInfo has programmatic TrustManager");
//            var0.getTrustManager().setTrustManager(var13);
//        }
//
//        byte[][] var14 = var1.getRootCAfingerprints();
//        if(var14 != null) {
//            info("Adding legacy rootCA fingerprints");
//            var0.getTrustManager().setRootCAFingerPrints(var14);
//        }
//
//        HostnameVerifier var15 = var1.getHostnameVerifier();
//        if(var15 != null) {
//            info("clientInfo has HostnameVerifier");
//            var0.getHostnameVerifier().setHostnameVerifier(var15);
//        }
//
//        String var16 = var1.getExpectedName();
//        if(var16 != null) {
//            info("clientInfo has expectedName");
//            var0.getHostnameVerifier().setExpectedName(var16);
//        }

    }

    private static X509Certificate[] getTrustedCAs(SSLContextWrapper var0) {
//        X509Certificate[] var1 = null;
//        String var2;
//        KeyStoreInfo[] var3;
//        if(!Kernel.isServer()) {
//            var2 = CommandLine.getCommandLine().getSSLTrustCA();
//            var3 = var2 != null?new KeyStoreInfo[]{new KeyStoreInfo(var2, "jks", (String)null)}:(new KeyStoreConfigurationHelper(ClientKeyStoreConfiguration.getInstance())).getTrustKeyStores();
//            ArrayList var4 = new ArrayList();
//
//            for(int var5 = 0; var3 != null && var5 < var3.length; ++var5) {
//                info("Trusted CA keystore: " + var3[var5].getFileName());
//
//                try {
//                    KeyStore var6 = KeyStore.getInstance(var3[var5].getType());
//                    FileInputStream var7 = new FileInputStream(var3[var5].getFileName());
//                    var6.load(var7, (char[])null);
//                    var4.addAll(SSLCertUtility.getX509Certificates(var6));
//                    var7.close();
//                } catch (Exception var9) {
//                    debug(2, var9, "Failure loading trusted CA list from: " + var3[var5].getFileName());
//                }
//            }
//
//            var1 = (X509Certificate[])((X509Certificate[])var4.toArray(new X509Certificate[var4.size()]));
//        } else {
//            info("SSLSetup: loading trusted CA certificates");
//            if(SecurityServiceManager.isSecurityServiceInitialized()) {
//                try {
//                    var1 = SSLContextManager.getServerTrustedCAs();
//                } catch (Exception var8) {
//                    debug("Failed to load server trusted CAs", var8);
//                }
//            } else {
//                debug(2, "SSLSetup: using pre-mbean command line configuration for SSL trust");
//                var2 = CommandLine.getCommandLine().getSSLTrustCA();
//                var3 = var2 != null?new KeyStoreInfo[]{new KeyStoreInfo(var2, "jks", (String)null)}:(new KeyStoreConfigurationHelper(PreMBeanKeyStoreConfiguration.getInstance())).getTrustKeyStores();
//                var1 = SSLContextManager.getTrustedCAs(var3);
//            }
//        }
//
//        return var1 != null && var1.length != 0?var1:null;
        return null;
    }

    public static void setFailureDetails(SSLSession var0, String var1) {
        var0.putValue("weblogic.security.ssl.failureDetails", var1);
    }

    public static String getFailureDetails(SSLSession var0) {
        return (String)var0.getValue("weblogic.security.ssl.failureDetails");
    }

    public static void logPlaintextProtocolClientError(SSLSocket var0, String var1) {
        String var2 = getPeerName(var0);
        debug(2, "Connection to SSL port was made from " + var2 + " using plaintext protocol: " + var1);
        if(logSSLRejections()) {
            Loggable var3 = SecurityLogger.logPlaintextProtocolClientErrorLoggable(var1, var2);
            var3.log();
            setFailureDetails(var0.getSession(), var3.getMessage());
        }

    }

    public static void logProtocolVersionError(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "Connection to SSL port from " + var1 + " appears to be either unknown SSL version or maybe is plaintext");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logProtocolVersionErrorLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainConstraintsStrictNonCriticalFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var1 + " contained a V3 CA certificate which had basic constraints which were not marked critical, " + "this is being rejected due to the strict enforcement of basic constraints.");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainConstraintsStrictNonCriticalFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainMissingConstraintsFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var1 + " contained a V3 CA certificate which was missing the basic constraints extension");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainMissingConstraintsFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainNotACaConstraintsFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var1 + " contained a V3 CA certificate which didn't indicate it really is a CA");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainNotACaConstraintsFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainPathLenExceededConstraintsFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var1 + " contained a V3 CA certificate which indicated a certificate chain path length in the basic constraints that was exceeded");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainPathLenExceededConstraintsFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainConstraintsConversionFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var1 + " contained a V3 CA certificate which couldn't be converted to be checked for basic constraints.");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainConstraintsConversionFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainUnrecognizedExtensionFailure(SSLSocket var0, String var1) {
        String var2 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var2 + " contained a V3 certificate with unrecognized critical extension: " + var1);
        if(logSSLRejections()) {
            Loggable var3 = SecurityLogger.logCertificateChainUnrecognizedExtensionFailureLoggable(var2, var1);
            var3.log();
            setFailureDetails(var0.getSession(), var3.getMessage());
        }

    }

    public static void logCertificateChainAlgKeyUsageFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var1 + " contained a V3 certificate which key usage constraints indicate" + " its key cannot be used in quality required by the key agreement algorithm");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainAlgKeyUsageFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainCheckKeyUsageFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "Cannot check key usage constraints of certificate recieved from " + var1 + " because of the failure to determine the key agreement algorithm");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainCheckKeyUsageFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificateChainCertSignKeyUsageFailure(SSLSocket var0) {
        String var1 = getPeerName(var0);
        debug(2, "The certificate chain received from " + var1 + " contained a V3 CA certificate which key usage constraints indicate" + " its key cannot be used to sign certificates");
        if(logSSLRejections()) {
            Loggable var2 = SecurityLogger.logCertificateChainCertSignKeyUsageFailureLoggable(var1);
            var2.log();
            setFailureDetails(var0.getSession(), var2.getMessage());
        }

    }

    public static void logCertificatePolicyIdDoesntExistIntheList(SSLSocket var0, String var1) {
        String var2 = getPeerName(var0);
        debug(2, "Certificate Policies Extension Processing Failed,PolicyId: " + var1 + " doesn't Exist in the allowed list");
        if(logSSLRejections()) {
            Loggable var3 = SecurityLogger.logCertificatePolicyIdDoesntExistIntheListLoggable(var1);
            var3.log();
            setFailureDetails(var0.getSession(), var3.getMessage());
        }

    }

    public static void logPolicyQualifierIdNotCPS(SSLSocket var0, String var1) {
        String var2 = getPeerName(var0);
        debug(2, "PolicyQualifier Id Found in the Certificate" + var1 + " doesn't match with CPS Qualifier Id");
        if(logSSLRejections()) {
            Loggable var3 = SecurityLogger.logPolicyQualifierIdNotCPSLoggable(var1);
            var3.log();
            setFailureDetails(var0.getSession(), var3.getMessage());
        }

    }

    public static String getPeerName(SSLSocket var0) {
        String var1 = "unknown";
        if(var0 != null) {
            InetAddress var2 = var0.getInetAddress();
            if(var2 != null) {
                try {
                    var1 = var2.getHostName() + " - " + var2.getHostAddress();
                } catch (SecurityException var4) {
                    var1 = var2.getHostAddress();
                }

                if(var1 == null) {
                    var1 = var2.toString();
                }
            }
        }

        return var1;
    }

    public static void logAlertReceivedFromPeer(SSLSocket var0, int var1) {
        if(logSSLRejections() && var1 != 0 && var1 != 90) {
            String var2 = getPeerName(var0);
            Loggable var3 = null;
            switch(var1) {
                case 10:
                    var3 = SecurityLogger.logUnexpectedMessageAlertReceivedFromPeerLoggable(var2);
                    break;
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                default:
                    var3 = SecurityLogger.logAlertReceivedFromPeerLoggable(var2, Integer.toString(var1));
                    break;
                case 20:
                    var3 = SecurityLogger.logBadRecordMacAlertReceivedFromPeerLoggable(var2);
                    break;
                case 21:
                    var3 = SecurityLogger.logDecryptionFailedAlertReceivedFromPeerLoggable(var2);
                    break;
                case 22:
                    var3 = SecurityLogger.logRecordOverFlowAlertReceivedFromPeerLoggable(var2);
                    break;
                case 30:
                    var3 = SecurityLogger.logDecompressionFailureAlertReceivedFromPeerLoggable(var2);
                    break;
                case 40:
                    var3 = SecurityLogger.logHandshakeFailureAlertReceivedFromPeerLoggable(var2);
                    break;
                case 41:
                    var3 = SecurityLogger.logNoCertificateAlertReceivedFromPeerLoggable(var2);
                    break;
                case 42:
                    var3 = SecurityLogger.logBadCertificateAlertReceivedFromPeerLoggable(var2);
                    break;
                case 43:
                    var3 = SecurityLogger.logUnsupportedCertificateAlertReceivedFromPeerLoggable(var2);
                    break;
                case 44:
                    var3 = SecurityLogger.logCertificateRevokedAlertReceivedFromPeerLoggable(var2);
                    break;
                case 45:
                    var3 = SecurityLogger.logCertificateExpiredAlertReceivedFromPeerLoggable(var2);
                    break;
                case 46:
                    var3 = SecurityLogger.logCertificateUnknownAlertReceivedFromPeerLoggable(var2);
                    break;
                case 47:
                    var3 = SecurityLogger.logIllegalParameterAlertReceivedFromPeerLoggable(var2);
                    break;
                case 48:
                    var3 = SecurityLogger.logUnknownCAAlertReceivedFromPeerLoggable(var2);
                    break;
                case 49:
                    var3 = SecurityLogger.logAccessDeniedAlertReceivedFromPeerLoggable(var2);
                    break;
                case 50:
                    var3 = SecurityLogger.logDecodeErrorAlertReceivedFromPeerLoggable(var2);
                    break;
                case 51:
                    var3 = SecurityLogger.logDecryptErrorAlertReceivedFromPeerLoggable(var2);
                    break;
                case 60:
                    var3 = SecurityLogger.logExportRestrictionAlertReceivedFromPeerLoggable(var2);
                    break;
                case 70:
                    var3 = SecurityLogger.logProtocolVersionAlertReceivedFromPeerLoggable(var2);
                    break;
                case 71:
                    var3 = SecurityLogger.logInsufficientSecurityAlertReceivedFromPeerLoggable(var2);
                    break;
                case 80:
                    var3 = SecurityLogger.logInternalErrorAlertReceivedFromPeerLoggable(var2);
                    break;
                case 100:
                    var3 = SecurityLogger.logNoRenegotiationAlertReceivedFromPeerLoggable(var2);
            }

            var3.log();
            setFailureDetails(var0.getSession(), var3.getMessage());
        }
    }

    public static Properties getSSLTrustProperties(ServerMBean var0) {
        Properties var1 = new Properties();
        String var2 = var0.getKeyStores();
        if("DemoIdentityAndDemoTrust".equals(var2)) {
            add(var1, "TrustKeyStore", "DemoTrust");
            add(var1, "JavaStandardTrustKeyStorePassPhrase", var0.getJavaStandardTrustKeyStorePassPhrase());
        } else if("CustomIdentityAndJavaStandardTrust".equals(var2)) {
            add(var1, "TrustKeyStore", "JavaStandardTrust");
            add(var1, "JavaStandardTrustKeyStorePassPhrase", var0.getJavaStandardTrustKeyStorePassPhrase());
        } else if("CustomIdentityAndCustomTrust".equals(var2)) {
            add(var1, "TrustKeyStore", "CustomTrust");
            add(var1, "CustomTrustKeyStoreFileName", var0.getCustomTrustKeyStoreFileName());
            add(var1, "CustomTrustKeyStoreType", var0.getCustomTrustKeyStoreType());
            add(var1, "CustomTrustKeyStorePassPhrase", var0.getCustomTrustKeyStorePassPhrase());
        } else if(!"CustomIdentityAndCommandLineTrust".equals(var2)) {
            throw new RuntimeException(SecurityLogger.getAssertionIllegalKeystoresValue(var2));
        }

        return var1;
    }

    static boolean isFatClient() {
        return !Kernel.isServer();
    }

    public static void logSSLUsingNullCipher() {
        SecurityLogger.logSSLUsingNullCipher();
    }

    private static void add(Properties var0, String var1, String var2) {
        if(var2 != null) {
            var0.setProperty(var1, var2);
        }

    }
}
