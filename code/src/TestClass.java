import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class TestClass {
        public static void main(String[] args) throws IOException {
        double[] weights = new double[]{1, 2, 4};
//            double[] weights = new double[]{0, 0, 0};
//            Scanner scanner = new Scanner(new File("./code/src/testData.model"));
//
//            // Model should only exist of one line
//            String newWeights = scanner.nextLine();
//
//            String[] splitWeights = newWeights.split(" ");
//            for (int i = 0; i < splitWeights.length; i++ ) {
//                weights[i] = Double.parseDouble(splitWeights[i]);
//            }
//            System.out.println(Arrays.toString(weights));

            /* FILL IN HERE */
            StringBuilder outputBuilder = new StringBuilder();
            for (double weight : weights) {
                outputBuilder.append(weight).append(" ");
            }
            outputBuilder.deleteCharAt(outputBuilder.length()-1); // Delete trailing whitespace
            BufferedWriter writer = new BufferedWriter(new FileWriter("./code/src/testData.model", false));
            writer.write(outputBuilder.toString());
            writer.flush();
            writer.close();
        }
}
