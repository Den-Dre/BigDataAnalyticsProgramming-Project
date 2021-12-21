DATA=/mnt/d/pcData/clean/PC/
for expon in $(seq -3 -2 -15); 
do
    eta=$(bc -l <<< "1*10^$expon")
    echo "Testing with eta $eta"
    time java -Xmx1800m -cp .:../../bin Perceptron $eta $DATA "10E$expon".001.randw.out.pc.noise 100000
done

