#RSA Encryption Suite
A set of programs designed to facilitate secure communication using an implementation the RSA encryption algorithm.  The suite includes a program to start a server to hold the messages and a separate program to run a client to interface with that server.

##How to Use
Apache Ant should be installed to take advantage of the included [build file](../master/build.xml).  This file uses the Java 6 compiler.
To build and run the server and client: 
```bash
ant build
ant runServer -Darg0=DESIRED_PORT
ant runClient
```

##Features
###Server
* Persist state through multiple executions using saved data file.
* Communicate with client through custom server protocol described in dedicated section below
 * Authenticate users
 * Add new users to server database
 * Receive messages from clients
 * Send messages to appropriate clients

###Client
* Send encrypted messages to multiple users in address book
* Receive encrypted messages from multiple servers
* Manage list of servers
 * Add and name servers by hostname and port
 * Delete unwanted servers
 * Set active server
* Manage an inbox
 * Sort by date, recipient, or message text
 * Delete unwanted messages
 * Preview and full message view modes
* Manage an address book of contacts
 * Add contacts with a public key file
 * Delete unwanted contacts
* Export public key file for sharing with others
* Export private key (No current use)

## Description of Cryptographic Security
Upon user profile creation, a set of corresponding public and private keys are created.  The public key is shared with other users while the private key is kept only within the program (unless the user manually exports it).  No plaintext communications are sent outside of the client infrastructure.

### Message Encryption
Messages are encrypted when the user presses send before they are sent to the server.  The server then stores messages in their encrypted form.  The recipient will then retrieve the encrypted message from the server and decrypt it upon receipt before displaying in the inbox.

### Server Authentication
In order to communicate with the server, a client must first be authenticated.  This authentication involves the client presenting his userfile to the server.  The server will then send an RSA challenge to the client using the public key provided in the userfile.  If the user successfully completes the challenge, the server will allow communication to proceed.  Otherwise, the connection is terminated.
Because the user ID used to store messages on the server depends only on the user's public key, this authentication is secure (assuming irreversibility of the hash used).

## RSA Implementation Details
The security is based upon the following implementation of the RSA algorithm.  Encryption is done as follows:
1. The message is converted to a byte array.
2. The message is split into chunks of size specified by _RSAMessage#readChunkSize_ [here](../master/src/rsaEncrypt/message/RSAMessage.java)
3. A random bit string is generated.
4. The message chunk `xor` random bit string is encrypted using the given keyfile
5. The random bit string is written to the encrypted message, preceded by the original byte length.  The encrypted bit string is written to the encrypted message, preceded by the original byte length.  (This allows proper reconstruction including leading or trailing null bytes in the chunk)
6. Steps 2--5 are repeated until the whole message is encrypted.

Decryption is done reversing the process.

## Server Protocol
The communication proceeds as follows, using special bytes as defined [here](../master/src/shared/serverComm/CommBytes.java).  The following tables show the flow of communication, where the information in the Client column is **_sent_** by the client and information in Server column is **_sent_** by the server.  
The basic pattern is send *Ready* byte, receive data, send *Ack* byte.  If the server detects an error, it will send the HANGUP byte and terminate the connection.

#### Establish Connection and Intent
Client | Server
:---: | :---:
|Ready
Ready|
Ack|
|Ready
Action Byte| 
|Ack
|Ready
Userfile|
|Ack

#### Authenticate
Client | Server
:---: | :---:
|Ready
Ready|
|Keyfile
Ack|
Ready|
|RSA Challenge
Ack|
|Ready
Response to Challenge|
|Ack
|Success / Failure

#### Add New User (if requested)
No communication.

#### (Client) Send Messages (if requested)
Client | Server
:---: | :---:
|Ready
Messages|
|Ack
|Success / Failure (each message)

#### (Client) Receive Messages (if requested)
Client | Server
:---: | :---:
|Ready
Ready|
|Messages
Ack|
