
# BoMPP
... is an XMPP/Jabber bot to execute remotely own written (Shell, Python, PHP, ...) scripts.
An use case is e.g. to controll a Raspberry PI.
All you need are two XMPP/Jabber accounts.

BoMPP is based on [Smack](https://github.com/igniterealtime/Smack).

## Setup

### Released .jar
If you download a release you have to create a folder anywhere which will act as a store and configuration.
So you must create a ```config.json``` in your folder with the structure, which [is defined in the wiki](https://github.com/denniskawurek/BoMPP/wiki/Structure-of-config.json).

Then you execute the JAR-File by calling the following command:

```
java -jar bompp.jar -p STOREPATH
```

``STOREPATH`` is the absolute path to the directory created above.

Note: Released .jar doesn't exist currently. :P

### Start from IDE
If you downloaded the source code and opened the project in an IDE you must also create the storage folder with the config file.
But you can set a hard coded path url in ``Main.java`` with the variable ``storePath``.

### Build from source
1. Clone the repo
```
git clone https://github.com/denniskawurek/BoMPP.git
cd BoMPP
```
2. Run `mvn package` in the root directory of BoMPP.
3. It will create a `/target` directory, where your `*-fat.jar` lays.
4. Download the [BouncyCastle Provider](https://www.bouncycastle.org/latest_releases.html) (section Signed JAR files)
5. Rename the provider to `bcprov.jar` and copy the jar to the same directory as the BoMPP.jar

## Encryption
Fortunately Smack has [Omemo](https://github.com/igniterealtime/Smack/blob/master/documentation/extensions/omemo.md) integrated which is an implementation of the Signal Protocol.

Take a look into the [wiki](https://github.com/denniskawurek/BoMPP/wiki/Enable-encryption---how-to-trust-a-user) to see how to start sending and receiving encrypted messages.

## Error handling
If you are getting an error like ```java.security.InvalidKeyException: Illegal key size``` you need to
[enable unlimited cryptography key sizes](https://stackoverflow.com/a/3864276/5725291).

## Further comments
There exists an earlier implementation of this in Python. Unfortunately the integration of OMEMO in Python is pretty poor, so I changed to JAVA.

## Issues & Contributions
... are welcome!

# Credits
Some parts of the code (esp. the OmemoController) bases on the [Command Line OMEMO Chat Client](https://github.com/vanitasvitae/clocc) by vanitasvitae.
