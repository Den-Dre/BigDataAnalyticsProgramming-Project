DATA=/cw/bdap/assignment1/data/clean/VFDT/ 
NB_FEATURE_VALUES=/cw/bdap/assignment1/data/nbFeatureValues.csv
TAU=0.05
NMIN=200
for expon in $(seq -11 2 -3); 
do
    delta=$(bc -l <<< "1*10^$expon")
    echo "Testing with delta $delta"
    time java -Xmx1800m -cp .:../../bin Vfdt $delta $TAU $NMIN $DATA $NB_FEATURE_VALUES "10E$expon".out.vfdt.clean 100000
done

