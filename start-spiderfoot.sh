#!/bin/bash
cd /opt/spiderfoot
nohup python3 ./sf.py -l 0.0.0.0:5001 > /var/log/spiderfoot.log 2> /var/log/spiderfoot.err &
echo "SpiderFoot started at http://localhost:5001"