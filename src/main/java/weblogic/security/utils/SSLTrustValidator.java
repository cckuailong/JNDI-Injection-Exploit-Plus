//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package weblogic.security.utils;

import weblogic.kernel.Kernel;
import weblogic.logging.Loggable;
import weblogic.security.SSL.CertPathTrustManager;
import weblogic.security.SSL.TrustManager;
import weblogic.security.SecurityLogger;

import javax.net.ssl.SSLSocket;
import java.net.InetAddress;
import java.security.cert.X509Certificate;

public class SSLTrustValidator implements SSLTruster {
    private boolean peerCertsRequired = false;
    private boolean overrideAllowed = true;
    private TrustManager trustManager = null;
    private byte[][] rootCAFingerPrints = (byte[][]) null;
    private String proxyHostName = null;
    private String urlHostName = null;

    public SSLTrustValidator() {
        if (Kernel.isServer()) {
            this.setTrustManager(new CertPathTrustManager());
        }

    }

    public void setTrustManager(TrustManager var1) {
        this.trustManager = var1;
    }

    public void setRootCAFingerPrints(byte[][] var1) {
        this.rootCAFingerPrints = var1;
    }

    public boolean isPeerCertsRequired() {
        return this.peerCertsRequired;
    }

    public void setPeerCertsRequired(boolean var1) {
        this.peerCertsRequired = var1;
    }

    public void setAllowOverride(boolean var1) {
        this.overrideAllowed = var1;
    }

    public void setProxyMapping(String var1, String var2) {
        this.urlHostName = var2;
        this.proxyHostName = var1;
    }

    public int validationCallback(X509Certificate[] var1, int var2, SSLSocket var3, X509Certificate[] var4) {
//        boolean var5 = SSLSetup.isDebugEnabled();
//        int var6 = var2;
//        if(var5) {
//            SSLSetup.info("validationCallback: validateErr = " + var2);
//            if(var1 != null) {
//                for(int var7 = 0; var7 < var1.length; ++var7) {
//                    SSLSetup.info("  cert[" + var7 + "] = " + var1[var7]);
//                }
//            }
//        }
//
//        if((var2 & 16) != 0 && this.rootCAFingerPrints != null && var1 != null && var1.length > 0) {
//            try {
//                byte[] var15 = SSLCertUtility.getFingerprint(var1[var1.length - 1]);
//
//                for(int var8 = 0; var8 < this.rootCAFingerPrints.length; ++var8) {
//                    if(Arrays.equals(var15, this.rootCAFingerPrints[var8])) {
//                        var6 &= -21;
//                        if(var5) {
//                            SSLSetup.info("Untrusted cert now trusted by legacy check");
//                        }
//                        break;
//                    }
//                }
//            } catch (CertificateEncodingException var14) {
//                SSLSetup.debug(1, var14, "Error while getting encoded certificate during trust validation");
//            }
//        }
//
//        if(var1 == null || var1.length == 0) {
//            if(this.peerCertsRequired) {
//                if(var5) {
//                    SSLSetup.info("Required peer certificates not supplied by peer");
//                }
//
//                var6 |= 4;
//            } else {
//                if(var5) {
//                    SSLSetup.info("Peer certificates are not required and were not supplied by peer");
//                }
//
//                var6 = 0;
//            }
//        }
//
//        if(this.trustManager != null) {
//            TrustManagerEnvironment.push(var4, var3);
//            boolean var16 = false;
//
//            try {
//                var16 = this.trustManager.certificateCallback(var1, var6);
//            } finally {
//                TrustManagerEnvironment.pop();
//            }
//
//            if(!var16 && var6 == 0) {
//                var6 |= 32;
//            }
//
//            if(var5) {
//                SSLSetup.info("weblogic user specified trustmanager validation status " + var6);
//            }
//        }
//
//        if(var6 != 0) {
//            this.logValidationError(var6, var3);
//            if(!this.overrideAllowed) {
//                if(var5) {
//                    SSLSetup.info("User defined JSSE trustmanagers not allowed to override");
//                }
//
//                var6 |= 64;
//            }
//        }
//
//        if(var5) {
//            SSLSetup.info("SSLTrustValidator returns: " + var6);
//        }

        return 0;
    }

    private String getTrustManagerClassName() {
        return this.trustManager != null ? this.trustManager.getClass().getName() : null;
    }

    private String getPeerName(SSLSocket var1) {
        String var2 = SSLSetup.getPeerName(var1);
        if (this.proxyHostName != null && this.urlHostName != null) {
            InetAddress var3 = var1.getInetAddress();
            if (var3 != null && (this.proxyHostName.equals(var3.getHostName()) || this.proxyHostName.equals(var3.getHostAddress()))) {
                var2 = var2 + " --> " + this.urlHostName;
            }
        }

        return var2;
    }

    private void logValidationError(int var1, SSLSocket var2) {
        if (SSLSetup.logSSLRejections()) {
            String var3 = this.getPeerName(var2);
            Loggable[] var4 = new Loggable[5];
            int var5 = 0;
            if ((var1 & 1) != 0) {
                var4[var5++] = SecurityLogger.logHandshakeCertInvalidErrorLoggable(var3);
            }

            if ((var1 & 2) != 0) {
                var4[var5++] = SecurityLogger.logHandshakeCertExpiredErrorLoggable(var3);
            }

            if ((var1 & 4) != 0) {
                var4[var5++] = SSLSetup.isFatClient() ? SecurityLogger.logFatClientHandshakeCertIncompleteErrorLoggable(var3) : SecurityLogger.logHandshakeCertIncompleteErrorLoggable(var3);
            }

            if ((var1 & 16) != 0) {
                var4[var5++] = SSLSetup.isFatClient() ? SecurityLogger.logFatClientHandshakeCertUntrustedErrorLoggable(var3) : SecurityLogger.logHandshakeCertUntrustedErrorLoggable(var3);
            }

            if ((var1 & 32) != 0) {
                var4[var5++] = SSLSetup.isFatClient() ? SecurityLogger.logFatClientHandshakeCertValidationErrorLoggable(var3, this.getTrustManagerClassName()) : SecurityLogger.logHandshakeCertValidationErrorLoggable(var3, this.getTrustManagerClassName());
            }

            if (var5 > 0) {
                StringBuffer var6 = null;
                if (var2 != null) {
                    var6 = new StringBuffer();
                }

                for (int var7 = 0; var7 < var5; ++var7) {
                    var4[var7].log();
                    if (var2 != null) {
                        if (var7 > 0) {
                            var6.append(", ");
                        }

                        var6.append(var4[var7].getMessage());
                    }
                }

                if (var2 != null) {
                    SSLSetup.setFailureDetails(var2.getSession(), var6.toString());
                }
            }
        }

        if (SSLSetup.isDebugEnabled()) {
            SSLSetup.info("Validation error = " + var1);
            if ((var1 & 1) != 0) {
                SSLSetup.info("Certificate chain is invalid");
            }

            if ((var1 & 2) != 0) {
                SSLSetup.info("Expired certificate");
            }

            if ((var1 & 4) != 0) {
                SSLSetup.info("Certificate chain is incomplete");
            }

            if ((var1 & 16) != 0) {
                SSLSetup.info("Certificate chain is untrusted");
            }

            if ((var1 & 32) != 0) {
                SSLSetup.info("Certificate chain was not validated by the custom trust manager even though built-in SSL validated it.");
            }
        }

    }
}
