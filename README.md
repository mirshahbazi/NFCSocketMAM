NFCSocket [![Build Status](https://travis-ci.org/Chrisplus/NFCSocket.svg?branch=master)](https://travis-ci.org/Chrisplus/NFCSocket)
=========

NfcSocket is a lib for Android developers to implement communication via NFC in an easy way. Based on Host-card-emulator, NfcSocket implements P2P multi-rounds communication. But unlike Android Beam, NfcSocket allows devices communicating automatically without user intervention.

## Main Features

* Play Nfc communication in an easy way.
* Multi-rounds P2P communication without user touch (comparing to Beam).
* Handle incoming messages and make responses in own classes rather than HCE service.

## Usage and Sample Code

An example is provided in Example folder.

### Card-Side *plays as Server Socket*

* import ('NFCSocketMAM') and all useful functions can be accessed in

<code>
	NfcReciveSocket.getInstance()
</code>

* declare Recive in your ('AndroidManifest.xml')

<code>
	<service
			android:name="com.mam.nfcsocket.HCEService"
			android:exported="true"
			android:permission="android.permission.BIND_NFC_SERVICE">
			<intent-filter>
				<action android:name="android.nfc.cardemulation.action.HOST_APDU_SERVICE" />
			</intent-filter>

			<meta-data
				android:name="android.nfc.cardemulation.host_apdu_service"
				android:resource="@xml/apduservice" />
		</service>

</code>

* ('xml/apduservice') decalres the AID group. More information about AID group selection can be found in [HostApduService](https://developer.android.com/reference/android/nfc/cardemulation/HostApduService.html). ('xml/apduservice') in ('NfcSocketMAM') provides an example AID group. Note that if using custom AID group, you should override the corresponding methods in ('NfcSocket.Utils')\


### Others

* Currently all operations are conducted in main thread. The multi-thread operations will be supported later.
* setTimeout in NfcClientSocket is not available currently.
