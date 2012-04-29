#!/bin/bash

ROOT='../consonants/'
HOME=`pwd`
for dir in `ls "$ROOT"`; do
   if [ -d "$ROOT/$dir" ]; then
       echo $dir
       cd $ROOT/$dir
       for x in `ls`; do
         if [[ $x == *_* ]] ; then
            echo " existing file $x "
         else
            echo " processing file $x "
            ../../util/startOfWav.sh $x
         fi
       done
       cd $HOME
   fi
done
 