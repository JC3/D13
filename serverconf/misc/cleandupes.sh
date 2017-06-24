#!/bin/sh

for a in *.sql.gz ; do    
    echo Processing $a...
    gunzip -c $a | sed -e 's/^-- Dump completed.*//g' | gzip -c -1 > temp.gz
    chmod 600 temp.gz
    touch -r $a temp.gz
    mv temp.gz $a
done

echo Unzipping...
gunzip *.sql.gz
echo Dupes...
fdupes . -dN
echo Zipping...
gzip -9 *.sql
