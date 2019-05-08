#!/bin/bash
cd $(dirname $0)

docker run \
       --name some-postgres \
       -e POSTGRES_DB=dbcache_test \
       -e POSTGRES_USER=postgres \
       -e POSTGRES_PASSWORD=password \
       -p 5432:5432 \
       -d postgres:9.6
