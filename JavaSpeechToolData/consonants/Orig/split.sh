# http://digitalcardboard.com/blog/2009/08/25/the-sox-of-silence/comment-page-1/?replytocom=2523
set -x
tTrigger=0.1      #  length of non silence to start.
tEnd=0.6         #  length of silence to end.
thresh=-40         #  silence in dB
normalize-audio $1
sox $1 $1  silence 1 ${tTrigger} ${thresh}d 1 ${tEnd} ${thresh}d  : newfile : restart