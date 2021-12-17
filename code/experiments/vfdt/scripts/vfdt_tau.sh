DATA=/cw/bdap/assignment1/data/clean/VFDT/ 
NB_FEATURE_VALUES=/cw/bdap/assignment1/data/nbFeatureValues.csv
DELTA=0.0000001
NMIN=200
taus=(0.01 0.05 0.1 0.2 0.5)
for tau in ${taus[@]}
do
    echo "Testing with tau $tau"
    time java -Xmx1800m -cp .:../../bin Vfdt $DELTA $tau $NMIN $DATA $NB_FEATURE_VALUES "TAU$tau".out.vfdt.clean 100000
done

