#!/bin/bash
  
# In the process of figuring out how Maven works,
# therefore in the meantime, we use this script to
# do a quick compile.

HOSTNAME=$1
PORT=$2

java -cp src ether.MinionService HOSTNAME PORT