package modac.coingame.ui.attend.qrcode;


import android.util.Base64;

/**
 * Created by Ahsen Saeed on 1/10/2018.
 */

public class EncryptionHelper {
    private static EncryptionHelper encryptionHelper = null;
    private String encryptionKey;
    private EncryptionHelper() {}
    public static EncryptionHelper getInstance() {
        if (encryptionHelper == null) {
            encryptionHelper = new EncryptionHelper();
        }
        return encryptionHelper;
    }

    public String encryptMsg() {
        return Base64.encodeToString(encryptionKey.getBytes(), Base64.DEFAULT);
    }
    public EncryptionHelper encryptionString(String encryptionKey) {
        this.encryptionKey = encryptionKey;
        return encryptionHelper;
    }
    public String getDecryptionString(String encryptedText) {
        return new String(Base64.decode(encryptedText.getBytes(), Base64.DEFAULT));
    }
}
