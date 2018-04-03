
# BoMPP
... is an XMPP/Jabber bot to execute remotely own written (Shell, Python, PHP, ...) scripts.
An use case is e.g. to controll a Raspberry PI.
All you need are two XMPP/Jabber accounts.

BoMPP is based on [Smack](https://github.com/igniterealtime/Smack).

## Setup

### Released .jar
If you download a release you have to create a folder anywhere which will act as a store and configuration.
So you must create a ```config.json``` in your folder with this structure:


	{
		"bot": {
			"jid": "jid@domain.com",
                        "pwd": "BetterPasswordThanThis",
                        "max_threads": "2",
                        "queue_size" : "10"
		},
		"cmds": [{
			"cmd": "/start",
			"type": "BASH",
			"script": ~/bompp_store/script.sh",
			"description": "Description will appear when calling /help"
		}]
	}

Then you call the .jar by changing to the directory where it lays and calling the following command:

```
java -jar bompp.jar STOREPATH
```
Where ``STOREPATH`` is the directory created above.

### Source from repo
If you downloaded the source code and opened the project in an IDE you must also create the storage folder with the config file.
But you can set a hard coded path url in ``Main.java`` with the variable ``storePath``.

## Encryption
Fortunately Smack has [Omemo](https://github.com/igniterealtime/Smack/blob/master/documentation/extensions/omemo.md) integrated which is an implementation of the Signal Protocol.

## Error handling
If you are getting an error like ```java.security.InvalidKeyException: Illegal key size``` you need to
[enable unlimited cryptography key sizes](https://stackoverflow.com/a/3864276/5725291).

## Further comments

There exists an earlier implementation of this in Python. Unfortunately the integration of OMEMO in Python is pretty poor, so I changed
to JAVA.

# Credits
Some parts of the code (esp. the OmemoController) bases on the [Command Line OMEMO Chat Client](https://github.com/vanitasvitae/clocc) by vanitasvitae.