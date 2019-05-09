#!/bin/bash -eu
cd $(dirname $0)

container_id=$(docker run \
       -e MYSQL_ROOT_PASSWORD=password \
       -e MYSQL_DATABASE=dbcache_test \
       -e MYSQL_USER=travis \
       -e MYSQL_PASSWORD=password \
       -p 3306:3306 \
       -d mysql:5.7)

echo -n 'Starting mysql'
for i in $(seq 1 10)
do
    echo -n '.'
    sleep 1
done
echo ''

docker exec -it $container_id \
       mysql -h 127.0.0.1 -u root -e "
GRANT ALL PRIVILEGES ON dbcache_test.* to 'travis'@'%' IDENTIFIED by 'password' WITH GRANT OPTION;
FLUSH PRIVILEGES;
SHOW GRANTS FOR 'travis'@'%';
" -p
