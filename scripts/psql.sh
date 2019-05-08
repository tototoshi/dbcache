#!/bin/bash
cd $(dirname $0)

docker run -it --rm \
       --network host \
       postgres \
       psql -h localhost -U postgres -d dbcache_test
