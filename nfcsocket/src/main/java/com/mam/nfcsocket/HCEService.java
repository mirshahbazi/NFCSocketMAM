package com.mam.nfcsocket;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class HCEService extends CustomHostApduService {

    public static final String TAG = HCEService.class.getSimpleName();

    private Messenger serverMessenger;

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        if (serverMessenger != null) {
            if (Utils.isSelectAidApdu(commandApdu)) {
                sendMessage(NfcReciveSocket.MSG_SERVER_SELECT_MESSAGE,
                        commandApdu);
            } else {
                sendMessage(NfcReciveSocket.MSG_SERVER_NORMAL_MESSAGE,
                        commandApdu);
            }
        }

        return null;
    }

    @Override
    public void onDeactivated(int reason) {

    }

    @Override
    public void onRefreshListener(Messenger sMessenger) {
        serverMessenger = sMessenger;
    }

    private void sendMessage(int what, byte[] data) {
        Message msg = Message.obtain(null, what);
        Bundle bundle = new Bundle();
        bundle.putByteArray(NfcReciveSocket.DATA_KEY, data);
        msg.setData(bundle);
        msg.replyTo = mMessenger;
        try {
            serverMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
