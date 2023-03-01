#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Usage: $0 name [path ...]";
    exit 2;
fi

name=${1//./\/};
shift;
path=${@:-.};

function check-jar() {
    jar -tf "$1" | grep -iH --label "$1" "$name";
}

status=1;

while read -r -d '' jarfile; do
    check-jar "$jarfile" && status=0;
done < <(find $path -type f -name '*.jar' -size +22c -print0)

exit $status;

# Usage: /bin/bash find-jar.sh "oracle.eclipselink.coherence.integrated.internal.cache.LockVersionExtractor"