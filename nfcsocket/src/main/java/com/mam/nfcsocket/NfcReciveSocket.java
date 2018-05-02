package com.mam.nfcsocket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by mmirshahbazi on 5/2/2018.
 */
public class NfcReciveSocket {

    public static final String TAG = NfcReciveSocket.class.getSimpleName();

    /**
     * MSG_SERVER_SELECT_MESSAGE is sent by {@link com.mam.nfcsocket.HCEService} when a
     * select
     * message is received.
     */
    public static final int MSG_SERVER_SELECT_MESSAGE = 1;

    /**
     * MSG_SERVER_NORMAL_MESSAGE is sent by {@link com.mam.nfcsocket.HCEService} when a
     * normal
     * message is received.
     */
    public static final int MSG_SERVER_NORMAL_MESSAGE = 2;

    /**
     * MSG_SERVER_DEACTIVE is send by {@link com.mam.nfcsocket.HCEService} when deactivate.
     * Two
     * possible scenarios making it happened: <li>The NFC link has been
     * deactivated or lost <li>A different AID has been selected and was
     * resolved to a different service component
     */
    public static final int MSG_SERVER_DEACTIVE = 3;

    /**
     * The key of response data in bundle.
     */
    public static final String DATA_KEY = "key";

    private static NfcReciveSocket instance;

    private NfcServerSocketListener listener;

    private Context context;

    private Messenger coreNfcMessenger;

    private final Messenger localMessenger = new Messenger(new MsgHandler());

    private Intent intent;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            log("hce service connected & send localMessenger");
            coreNfcMessenger = new Messenger(service);
            Message msg = Message.obtain(null, HCEService.MSG_REFRESH_SERVER);
            msg.replyTo = localMessenger;
            try {
                coreNfcMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log("hce service disconnected");
            if (coreNfcMessenger != null) {
                log("send null messenger");
                Message msg = Message.obtain(null,
                        HCEService.MSG_REFRESH_SERVER);
                msg.replyTo = null;
                try {
                    coreNfcMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    /**
     * Get an instance of NfcReciveSocket
     *
     * @param context The application context.
     * @return The instance of NfcReciveSocket
     */
    public static NfcReciveSocket getInstance(Context context) {
        if (instance == null) {
            instance = new NfcReciveSocket(context);
        }
        return instance;
    }

    private NfcReciveSocket(Context ctx) {
        context = ctx;
        intent = new Intent(context, HCEService.class);
    }

    /**
     * Start the Nfc service and listening the coming reader message.
     */
    public void listen() {
        if (listener != null) {
            log("start listen");
            context.bindService(intent, serviceConnection,
                    Context.BIND_AUTO_CREATE);
            context.startService(intent);
        }
    }

    /**
     * Stop the Nfc service.
     */
    public void close() {

        context.unbindService(serviceConnection);
        if (context.stopService(intent)) {
            log("stop nfc service");
        }
    }

    /**
     * Set a listener which can get callback when the Nfc server gets messages.
     *
     * @param lst A {NfcServerSocketListener} used to get the callback when
     *            messages come.
     */
    public void setListener(NfcServerSocketListener lst) {
        if (lst != null) {
            log("set listener");
            listener = lst;
        }
    }

    /**
     * Created by mmirshahbazi on 5/2/2018.
     */
    public interface NfcServerSocketListener {

        /**
         * The callback when the select command is received. The connection will
         * NOT be created if null is returned.
         *
         * @param message The select message containing AID generally.
         * @return You should not return null if you confirm to create a
         * connection with client side.
         */
        public byte[] onSelectMessage(byte[] message);

        /**
         * The callback when normal command (message) is received.
         *
         * @param message The normal message.
         * @return The response message. It could be null.
         */
        public byte[] onMessage(byte[] message);

    }

    private class MsgHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SERVER_SELECT_MESSAGE:
                    if (listener != null) {
                        byte[] response = listener.onSelectMessage(msg.getData()
                                .getByteArray(DATA_KEY));
                        sendResponse(response);
                    }
                    break;
                case MSG_SERVER_NORMAL_MESSAGE:
                    if (listener != null) {
                        byte[] response = listener.onMessage(msg.getData()
                                .getByteArray(DATA_KEY));
                        sendResponse(response);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

    private void sendResponse(byte[] response) {
        if (coreNfcMessenger != null) {
            Message message = Message
                    .obtain(null, HCEService.MSG_RESPONSE_APDU);
            Bundle dataBundle = new Bundle();
            dataBundle.putByteArray(HCEService.KEY_DATA, response);
            message.setData(dataBundle);
            try {
                coreNfcMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private void log(String logMessage) {
        Log.d(TAG, logMessage);
    }

}
