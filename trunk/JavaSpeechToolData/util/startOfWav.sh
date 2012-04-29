# http://digitalcardboard.com/blog/2009/08/25/the-sox-of-silence/comment-page-1/?replytocom=2523

name=`basename $1 .wav`
tDur=0.2        #  length of silence to end.
sox $1 ../silence.wav tmp.wav
sox tmp.wav ${name}_.wav  trim 0 $tDur
rm tmp.wav