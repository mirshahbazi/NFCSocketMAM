package com.mam.nfc;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mam.nfcsocket.NfcReciveSocket;
import com.mam.nfcsocket.NfcSendSocket;

/**
 * Created by mmirshahbazi on 5/2/2018.
 */
public class MainActivity extends Activity {

    private Button startServer;

    private Button stopServer;

    private Button send;

    private TextView console;

    private EditText message;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //turn off android beam
        NfcAdapter nfcAdapter;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // turn off sending Android Beam
        nfcAdapter.setNdefPushMessage(null, this);

        context = getApplicationContext();
        startServer = (Button) findViewById(R.id.startserver);
        stopServer = (Button) findViewById(R.id.stopserver);

        send = (Button) findViewById(R.id.send);
        console = (TextView) findViewById(R.id.text);
        message = (EditText) findViewById(R.id.message);

        startServer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                NfcSendSocket.getInstance(getApplicationContext())
                        .unregister(clientListener);
                NfcReciveSocket.getInstance(getApplicationContext())
                        .setListener(serverListener);
                NfcReciveSocket.getInstance(getApplicationContext()).listen();

            }

        });

        stopServer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                NfcSendSocket.getInstance(getApplicationContext()).register(
                        clientListener);
                NfcReciveSocket.getInstance(getApplicationContext()).close();
            }

        });

        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!NfcSendSocket.getInstance(getApplicationContext())
                        .isConnected()) {
                    int i = NfcSendSocket
                            .getInstance(getApplicationContext()).connect();
                    Log.d("BTR", i + "");
                    if (i == NfcSendSocket.CONNECT_SUCCESS) {
                        String tmp = getMessage();
                        appendMessage(false, tmp);
                        byte[] response = NfcSendSocket.getInstance(
                                getApplicationContext()).send(
                                tmp.getBytes());
                        showLog(response);
                        appendMessage(true, new String(response));
                    }
                } else {
                    String tmp = getMessage();
                    appendMessage(false, tmp);
                    byte[] response = NfcSendSocket.getInstance(
                            getApplicationContext()).send(tmp.getBytes());
                    showLog(response);
                    appendMessage(true, new String(response));
                }

                // NfcSendSocket.getInstance(getApplicationContext()).close();
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        NfcSendSocket.getInstance(getApplicationContext()).register(
                clientListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcSendSocket.getInstance(getApplicationContext()).unregister(
                clientListener);
    }

    private NfcReciveSocket.NfcServerSocketListener serverListener
            = new NfcReciveSocket.NfcServerSocketListener() {

        @Override
        public byte[] onSelectMessage(byte[] message) {
            Log.d("BTR", "selectMessage");
            appendMessage(true, new String(message));
            appendMessage(false, "welcome");
            return "welcome".getBytes();
        }

        @Override
        public byte[] onMessage(byte[] message) {
            Log.d("BTR", "normalMessage");
            appendMessage(true, new String(message));
            appendMessage(false, "I know");
            return "I know".getBytes();
        }

    };

    private NfcSendSocket.NfcClientSocketListener clientListener
            = new NfcSendSocket.NfcClientSocketListener() {

        @Override
        public void onDiscoveryTag() {
            Log.d("BTR", "tag!");
        }

        @Override
        public Activity getCurrentActivity() {
            return MainActivity.this;
        }
    };

    private void showLog(byte[] res) {
        if (res != null) {
            Log.d("BTR", new String(res));
        }

    }

    private void appendMessage(boolean isReceive, String message) {
        if (console != null) {
            String tmp = "";
            if (isReceive) {
                tmp += "Receive: ";
            } else {
                tmp += "Send: ";
            }

            tmp = tmp + message + "\n";
            console.append(tmp);
        }
    }

    private String getMessage() {
        if (message != null && message.getText().length() > 0) {
            return message.getText().toString();
        } else {
            return "helloMam";
        }
    }

}
