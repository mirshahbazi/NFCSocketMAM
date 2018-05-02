package com.mam.nfcsocket;

import java.io.IOException;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

public class NfcSendSocket implements ReaderCallback {

    public static final String TAG = NfcSendSocket.class.getSimpleName();

    public static final int NFC_MODE_FLAGS = NfcAdapter.FLAG_READER_NFC_A
            | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

	/* Status Code of Connecting */

    /**
     * When connecting to Tag successfully
     */
    public static final int CONNECT_SUCCESS = 100;

    /**
     * When connecting but no tag found
     */
    public static final int CONNECT_FAIL_NO_TAG = -101;

    /**
     * When connecting and tag are found but not {@link android.nfc.tech.IsoDep} instance
     */
    public static final int CONNECT_FAIL_NO_TRANCEIVER = -102;

    /**
     * When connecting, send select message but no response is received
     */
    public static final int CONNECT_FAIL_NO_RESPONSE = -103;

    /**
     * IO error when connecting
     */
    public static final int CONNECT_FAIL_IO_ERROR = -104;

    /**
     * The response message for selecting is not verified.
     */
    public static final int CONNECT_FAIL_WRONG_INFO = -105;

    /**
     * Unknown error
     */
    public static final int CONNECT_FAIL_UNKNOWN = -199;

    private static NfcSendSocket instance;

    private NfcAdapter nfcAdapter;

    private Tag currentTag;

    private IsoDep isoDep;

    private HashSet<NfcClientSocketListener> listenerSet;

    /**
     * Get the instance of NfcSendSocket
     *
     * @param context The application context
     * @return The instance of NfcSendSocket
     */
    public static NfcSendSocket getInstance(Context context) {
        if (instance == null) {
            instance = new NfcSendSocket(context);
        }

        return instance;
    }

    private NfcSendSocket(Context context) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        listenerSet = new HashSet<NfcClientSocketListener>();
        currentTag = null;
        isoDep = null;
    }

    /**
     * You MUST register before trying to connect.
     *
     * @param ls The listener has better to be implemented by your current
     *           activity.
     */
    public synchronized void register(NfcClientSocketListener ls) {
        if (ls != null) {
            if (!listenerSet.contains(ls)) {
                log("register client on "
                        + ls.getCurrentActivity().getLocalClassName());
                listenerSet.add(ls);
                enableNfcReaderMode(ls.getCurrentActivity());
            } else {

            }
        }

    }

    /**
     * You MUST unregister after closing your client socket
     *
     * @param ls The listener has better to be implemented by your current
     *           activity.
     */
    public synchronized void unregister(NfcClientSocketListener ls) {
        if (ls != null) {
            if (listenerSet.contains(ls)) {
                log("unregister client on "
                        + ls.getCurrentActivity().getLocalClassName());
                listenerSet.remove(ls);
                disableNfcReaderMode(ls.getCurrentActivity());
            }
        }
    }

    /**
     * Get the number of registered client currently. Generally speaking it
     * should be 1
     *
     * @return The number of registered client currently
     */
    public int getClientNum() {
        if (listenerSet != null) {
            return listenerSet.size();
        } else {
            return -1;
        }
    }

    /**
     * Specified the timeout of waiting for {@link NfcReciveSocket}
     * response.
     *
     * @param millisecond The timeout in millisecond
     */
    public void setTimeout(int millisecond) {

    }

    /**
     * Get the connection status
     *
     * @return the status True or False
     */
    public boolean isConnected() {
        if (currentTag == null || isoDep == null) {
            return false;
        }
        return isoDep.isConnected();
    }

    /**
     * Try to connect to {@link NfcReciveSocket} and send AID select message
     *
     * @return The connection status code.
     */
    public int connect() {

        if (currentTag == null) {
            return CONNECT_FAIL_NO_TAG;
        }

        if (isoDep == null) {
            return CONNECT_FAIL_NO_TRANCEIVER;
        }

        try {
            isoDep.connect();

            byte[] response = isoDep.transceive(Utils.createSelectAidApdu());

            if (checkConnectResponse(response)) {
                return CONNECT_SUCCESS;
            } else {
                return CONNECT_FAIL_WRONG_INFO;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return CONNECT_FAIL_IO_ERROR;
        }

    }

    /**
     * Send message via a Nfc connection.
     *
     * @param message The message in byte array form
     * @return The response from {@link NfcReciveSocket}. null might be returned
     * if <li>the connection is lost. <li> {@link NfcReciveSocket} does
     * not return response within timeout. <li> {@link NfcReciveSocket}
     * returns a null response.
     */
    public byte[] send(byte[] message) {
        if (currentTag == null) {
            return null;
        }

        if (isoDep == null) {
            isoDep = IsoDep.get(currentTag);
        }

        if (isoDep.isConnected()) {
            try {
                byte[] response = isoDep.transceive(message);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            log("isodep not connected");
            return null;
        }
    }

    /**
     * Close the current client socket
     */
    public void close() {
        currentTag = null;
        if (isoDep != null) {
            try {
                isoDep.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isoDep = null;
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        currentTag = tag;
        isoDep = IsoDep.get(currentTag);

        if (listenerSet != null) {
            for (NfcClientSocketListener listener : listenerSet) {
                if (listener != null) {
                    listener.onDiscoveryTag();
                }
            }
        }
    }

    /**
     * The NfcClientSocketListener should be implemented in the caller activity.
     * Registering and unrgistering should be carefully planned.
     * Created by mmirshahbazi on 5/2/2018.
     */
    public interface NfcClientSocketListener {

        /**
         * Get the current activity instance.
         *
         * @return The current foreground activity.
         */
        public Activity getCurrentActivity();

        /**
         * Get notifications when a tag is found.
         */
        public void onDiscoveryTag();

    }

    private void enableNfcReaderMode(Activity activity) {
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(activity, this, NFC_MODE_FLAGS, null);
        }
    }

    private void disableNfcReaderMode(Activity activity) {
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(activity);
        }
    }

    private boolean checkConnectResponse(byte[] data) {
        return true;
    }

    private void log(String message) {
        Log.d(TAG, message);
    }

}
