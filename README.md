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


* ('xml/apduservice') decalres the AID group. More information about AID group selection can be found in [HostApduService](https://developer.android.com/reference/android/nfc/cardemulation/HostApduService.html). ('xml/apduservice') in ('NfcSocketMAM') provides an example AID group. Note that if using custom AID group, you should override the corresponding methods in ('NfcSocket.Utils')\


### Others

* Currently all operations are conducted in main thread. The multi-thread operations will be supported later.

### How to
* Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
* Step 2. Add the dependency

dependencies {
	        implementation 'com.github.mirshahbazi:NFCSocketMAM:-SNAPSHOT'
	}
	
[![](https://jitpack.io/v/mirshahbazi/NFCSocketMAM.svg)](https://jitpack.io/#mirshahbazi/NFCSocketMAM)
https://twitter.com/intent/tweet?original_referer=https%3A%2F%2Fjitpack.io%2F&ref_src=twsrc%5Etfw&text=Version%20-SNAPSHOT%20of%20NFCSocketMAM%20is%20now%20available%20on%20&tw_p=tweetbutton&url=http%3A%2F%2Fjitpack.io%2F%23mirshahbazi%2FNFCSocketMAM%2F-SNAPSHOT
	
