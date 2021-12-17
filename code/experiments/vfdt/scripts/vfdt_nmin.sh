DATA=/cw/bdap/assignment1/data/clean/VFDT/ 
NB_FEATURE_VALUES=/cw/bdap/assignment1/data/nbFeatureValues.csv
TAU=0.05
DELTA=0.0000001
nmins=(100 200 400 800)
for nmin in ${nmins[@]}
do
    echo "Testing with nmin $nmin"
    time java -Xmx1800m -cp .:../../bin Vfdt $DELTA $TAU $nmin $DATA $NB_FEATURE_VALUES "NMIN$nmin".out.vfdt.clean 100000
done

