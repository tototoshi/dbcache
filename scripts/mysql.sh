#!/bin/bash

cd $(dirname $0)

docker run -it --rm --network host mysql mysql -h 127.0.0.1 -u travis --database=dbcache_test 
