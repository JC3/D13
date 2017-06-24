#!/bin/sh

dbuser=user
dbpass=pass
dbname=name

basename=disorient_d15

outfile=/var/dbbackup/$basename-`date +%Y-%m-%d-%H%M%S`.sql.gz

mysqldump --opt --complete-insert --flush-logs --single-transaction --user=$dbuser --password=$dbpass $dbname | gzip -9 > $outfile

curfile=/var/dbbackup-local/$basename-latest.sql.gz
prefile=/var/dbbackup-local/$basename-previous.sql.gz

cp -fp $curfile $prefile
cp -fp $outfile $curfile

chmod 600 $curfile
chmod 600 $prefile
chmod 600 $outfile

sync
