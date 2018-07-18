
# BoMPP
... is a XMPP/Jabber bot to execute remotely own written (Shell, Python, PHP, ...) scripts.
An use case is e.g. to controll a Raspberry PI.
All you need are two XMPP/Jabber accounts.

BoMPP is based on [Smack](https://github.com/igniterealtime/Smack).

## Setup

1. Download the latest [release](https://github.com/denniskawurek/BoMPP/releases)
2. Create a folder anywhere which will act as a store and configuration.
3. Create a ```config.json``` in this folder. The `config.json` must have the structure, which [is defined in the wiki](https://github.com/denniskawurek/BoMPP/wiki/Structure-of-config.json).
4. Download the [BouncyCastle Provider](https://www.bouncycastle.org/latest_releases.html) (section Signed JAR files).
5. Rename the provider to `bcprov.jar` and copy the jar to the same directory as the BoMPP.jar

Then you execute the JAR-File by calling the following command:

```
java -jar bompp.jar -p STOREPATH
```

``STOREPATH`` is the absolute path to the directory created above.

### From source

Take a look into the [wiki](https://github.com/denniskawurek/BoMPP/wiki/Setup-from-Source)

## Encryption
Fortunately Smack has [Omemo](https://github.com/igniterealtime/Smack/blob/master/documentation/extensions/omemo.md) integrated which is an implementation of the Signal Protocol.

Take a look into the [wiki](https://github.com/denniskawurek/BoMPP/wiki/Enable-encryption---how-to-trust-a-user) to see how to start sending and receiving encrypted messages.

## Error handling
If you are getting an error like ```java.security.InvalidKeyException: Illegal key size``` you need to
[enable unlimited cryptography key sizes](https://stackoverflow.com/a/3864276/5725291).

## Issues & Contributions
... are welcome!

## Architecture
[![Architecture of BoMPP](https://dkwr.de/images/bompp_architecture.svg)](https://dkwr.de/images/bompp_architecture.svg)
Further notes in the [wiki](https://github.com/denniskawurek/BoMPP/wiki/Architecture).

## Further comments
There exists an earlier implementation of this in Python. Unfortunately the integration of OMEMO in Python is pretty poor, so I changed to JAVA.

## Credits
Some parts of the code (esp. the OmemoController) bases on the [Command Line OMEMO Chat Client](https://github.com/vanitasvitae/clocc) by vanitasvitae.
