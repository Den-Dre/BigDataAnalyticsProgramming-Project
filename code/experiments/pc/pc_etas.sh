DATA=/cw/bdap/assignment1/data/noisy/PC/ 
for expon in $(seq -15 2 -5); 
do
    eta=$(bc -l <<< "1*10^$expon")
    echo "Testing with eta $eta"
    time java -Xmx1800m -cp .:../bin Perceptron $eta $DATA "10E$expon".out.pc.noise 100000
done

