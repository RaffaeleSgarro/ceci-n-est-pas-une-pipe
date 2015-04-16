Ceci n'est pas une pipe
=======================

![Ceci n'est pas une pipe by Magritte](/src/stackoverflow/ceci-n-est-pas-une-pipe.jpg)

Encrypt a message (for example the image shown above, which is distributed along
with this program) and wrap it in an image to easily transmit it.
The output of a program run is the following PNG image:

![sample output PNG](/sample-encrypted.png)

This is a work-in-progress I originally started to answer a [question][1]
on StackOverflow

Development notes
=================

The transport blob (the PNG) contains:

- the length (number of bytes as a Java `int`) of the secret message. Read with `ObjectInputStream.readInt()`
- headers in a `Map<String, Serializable>`. Read with `ObjectInputStream.readObject()`
- the encrypted message (each byte is the blue component in the transport pixel)
- final padding


[1]: http://stackoverflow.com/questions/29669104
