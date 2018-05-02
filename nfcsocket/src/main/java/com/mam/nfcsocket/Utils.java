package com.mam.nfcsocket;


/**
 * Created by mmirshahbazi on 5/2/2018.
 */
public abstract class Utils {

    public static byte[] CLA_INS_P1_P2 = {0x00, (byte) 0xA4, 0x04, 0x00};

    public static byte[] AID_ANDROID = {(byte) 0xF0, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06};

    public static void setUnitByte(byte[] unitByte) {
        System.arraycopy(unitByte, 0, CLA_INS_P1_P2, 0, unitByte.length);
    }

    public static byte[] createSelectAidApdu() {
        byte[] result = new byte[6 + AID_ANDROID.length];
        System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
        result[4] = (byte) AID_ANDROID.length;
        System.arraycopy(AID_ANDROID, 0, result, 5, AID_ANDROID.length);
        result[result.length - 1] = 0;
        return result;
    }

    public static boolean isSelectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte) CLA_INS_P1_P2[0]
                && apdu[1] == (byte) CLA_INS_P1_P2[1];
    }

    public static boolean isConnectStatusSuccess(int connectStatusCode) {
        return connectStatusCode > 0;
    }

}
