#!/bin/bash -eu
cd $(dirname $0)

container_name=dbcache-mysql

docker stop $container_name || :
docker rm $container_name || :

docker run \
       --name $container_name \
       -e MYSQL_DATABASE=dbcache_test \
       -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \
       -p 3306:3306 \
       -d mysql:8

echo -n 'Starting mysql'
while ! docker exec $container_name mysql -h 127.0.0.1 -u root -e "select 1" > /dev/null 2>&1
do
    echo -n '.'
    sleep 1
done
echo ''

# Allow user to access from outside of container without password
docker exec $container_name \
       mysql -h 127.0.0.1 -u root -e "
CREATE USER 'travis'@'%' IDENTIFIED BY '';
GRANT ALL ON *.* TO 'travis'@'%';
FLUSH PRIVILEGES;
"
