NFCSocket [![Build Status](https://travis-ci.org/Chrisplus/NFCSocket.svg?branch=master)](https://travis-ci.org/Chrisplus/NFCSocket)
=========

NfcSocket is a lib for Android developers to implement communication via NFC in an easy way. Based on Host-card-emulator, NfcSocket implements P2P multi-rounds communication. But unlike Android Beam, NfcSocket allows devices communicating automatically without user intervention. NfcSocket also allows developers to handle incoming messages both in the HCE service and their own classes.

## Main Features

* Play Nfc communication in an easy way.
* Multi-rounds P2P communication without user touch (comparing to Beam).
* Handle incoming messages and make responses in own classes rather than HCE service.

## Usage and Sample Code

An example is provided in Example folder.

### Card-Side *plays as Server Socket*

* import ('NFCSocket') and all useful functions can be accessed in

<code>
	NfcServerSocket.getInstance()
</code>

* declare service in your ('AndroidManifest.xml')

<code>

 	<service
		android:name="com.chrisplus.nfcsocket.HCEService"
        	android:exported="true"
        	android:permission="android.permission.BIND_NFC_SERVICE" >
        	<intent-filter>
                	<action android:name="android.nfc.cardemulation.action.HOST_APDU_SERVICE" />
        	</intent-filter>
        	<meta-data
                	android:name="android.nfc.cardemulation.host_apdu_service"
                	android:resource="@xml/apduservice" />
 	</service>

</code>

* ('xml/apduservice') decalres the AID group. More information about AID group selection can be found in [HostApduService](https://developer.android.com/reference/android/nfc/cardemulation/HostApduService.html). ('xml/apduservice') in ('NfcSocket') provides an example AID group. Note that if using custom AID group, you should override the corresponding methods in ('NfcSocket.Utils')\

<code>
public static byte[] createSelectAidApdu()
public static boolean isSelectAidApdu(byte[] apdu)
</code>

* To setup Nfc server socket and listen incoming message

<code>
NfcServerSocket.getInstance(getApplicationContext()).setListener(serverListener);
NfcServerSocket.getInstance(getApplicationContext()).listen();
</code>

### Reader-Side *plays as Client Socket*

* Register and unregister NfcClientSocket. Before connecting to NfcServerSocket, you should register first and unregister when you do not it any more. Note that your application only can play in one mode: either Server or Client. If NfcServerSocket is running, NfcClientSocket will not work.

<code>
NfcClientSocket.getInstance(getApplicationContext()).register(clientListener);
NfcClientSocket.getInstance(getApplicationContext()).unregister(clientListener);
</code>

* try to connect and the status code will be returned. More information about status code can be found in ('NfcSocket.NfcClientSocket')

<code>
int statusCode = NfcClientSocket.getInstance(getApplicationContext()).connect();
</code>

* Send Message to Server and wait for response. You are allowed to set the timeout for waiting responses.

<code>
byte[] response = NfcClientSocket.getInstance(getApplicationContext()).send(message.getBytes());
</code>

### Others

* Currently all operations are conducted in main thread. The multi-thread operations will be supported later.
* setTimeout in NfcClientSocket is not available currently.
