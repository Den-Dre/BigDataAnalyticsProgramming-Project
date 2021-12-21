DATA=/mnt/d/pcData/clean/PC/
for expon in $(seq -5 -2 -15); 
do
    eta=$(bc -l <<< "1*10^$expon")
    echo "Testing with eta $eta"
    time java -Xmx1800m -cp .:../../../bin Perceptron $eta $DATA "10E$expon".randw.out.pc.noise 100000
done

