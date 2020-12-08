package net.dgg.utils.request;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Description: TODO
 * @Author Created by yan.x on 2020-09-08 .
 **/
public class DggSslUtil {
    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static class miTM implements TrustManager, X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            return;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            return;
        }
    }

    /**
     * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
     *
     * @throws Exception
     */
    public static void ignoreSsl() throws Exception {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
}
