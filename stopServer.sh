#!/bin/sh

set -e

if [ ! -f PID ]; then
	echo "Cannot find running RSA Message Server"
	exit 1;
else
	echo "Stopping RSA Message Server"
	kill `cat PID`
	rm -f PID
	exit 0;
fi