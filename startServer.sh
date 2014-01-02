#!/bin/sh

set -e

if [ -f PID ]; then
	echo "Server already running.  PID: `cat PID`"
	exit 1;
else
	# Start Server
	echo "Starting RSA Message Server on port $1"
	echo "Logging at $2"
	java -jar ./bin/jar/RSAESserver.jar $1 $2 &

	# Save PID
	echo $!>PID
	chmod -w PID

	exit 0;
fi
