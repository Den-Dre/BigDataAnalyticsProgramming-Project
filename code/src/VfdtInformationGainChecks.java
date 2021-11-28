import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class VfdtInformationGainChecks {
    private Vfdt learner;
    private Example<Integer> example1;
    private Example<Integer> example2;
    private Example<Integer> example3;
    private Example<Integer> example4;
    private Example<Integer> example5;
    private Example<Integer> example6;
    private Example<Integer> example7;
    private Example<Integer> example8;
    private Example<Integer> example9;
    private Example<Integer> example10;
    private Example<Integer> example11;
    private Example<Integer> example12;
    private Example<Integer> example13;
    private Example<Integer> example14;

    @Before
    public void before() {
        /*
         * https://www.humaneer.org/blog/data-science-information-gain-and-entropy-explained/
         * Outlook : [sunny, overcast, rain]
         * Humidity: [normal, high]
         * Wind    : [weak, strong]
         */
        learner = new Vfdt(new int[] {3, 2, 2}, 0.05, 0.05, 1);
        example1 = new Example<>(new Integer[] {0, 1, 0}, 0);
        example2 = new Example<>(new Integer[] {0, 1, 1}, 0);
        example3 = new Example<>(new Integer[] {1, 1, 0}, 1);
        example4 = new Example<>(new Integer[] {2, 1, 0}, 1);
        example5 = new Example<>(new Integer[] {2, 1, 0}, 1);
        example6 = new Example<>(new Integer[] {2, 0, 1}, 0);
        example7 = new Example<>(new Integer[] {1, 0, 1}, 1);
        example8 = new Example<>(new Integer[] {0, 1, 0}, 0);
        example9 = new Example<>(new Integer[] {0, 0, 0}, 1);
        example10 = new Example<>(new Integer[] {2, 0, 0}, 1);
        example11 = new Example<>(new Integer[] {0, 0, 1}, 1);
        example12 = new Example<>(new Integer[] {1, 1, 1}, 1);
        example13 = new Example<>(new Integer[] {1, 0, 0}, 1);
        example14 = new Example<>(new Integer[] {2, 1, 1}, 0);
    }

    @Test
    public void testIG() {
        int[][][] nijk = new int[3][][]; // 3 features
        nijk[0] = new int[3][2]; // three values for feature0: Outlook
        nijk[1] = new int[2][2]; // two values for feature1: Humidity
        nijk[2] = new int[2][2]; // two values for feature2: Wind
        updateNijk(nijk, example1);
        updateNijk(nijk, example2);
        updateNijk(nijk, example3);
        updateNijk(nijk, example4);
        updateNijk(nijk, example5);
        updateNijk(nijk, example6);
        updateNijk(nijk, example7);
        updateNijk(nijk, example8);
        updateNijk(nijk, example9);
        updateNijk(nijk, example10);
        updateNijk(nijk, example11);
        updateNijk(nijk, example12);
        updateNijk(nijk, example13);
        updateNijk(nijk, example14);

        // Rounded information gain of splitting on Outlook should be equal to 0.247
        // https://www.humaneer.org/blog/data-science-information-gain-and-entropy-explained/
        assertEquals(0.247, VfdtNode.informationGain(0, nijk), 0.001);
    }

    private void updateNijk(int[][][] nijk, Example<Integer> example) {
        Integer[] avs = example.attributeValues;
        for (int a = 0; a < avs.length; a++) {
            nijk[a][avs[a]][example.classValue] += 1;
        }
    }

    @Test
    public void addExamples() throws IOException {
        learner.readModel("models/vfdtSanity0.model", 0);
        learner.update(example1);
        learner.update(example2);
        learner.update(example3);
        learner.update(example4);
        learner.update(example5);
        learner.update(example6);
        learner.update(example7);
        learner.update(example8);
        learner.update(example9);
        learner.update(example10);
        learner.update(example11);
        learner.update(example12);
        learner.update(example13);
        learner.update(example14);
    }

}
