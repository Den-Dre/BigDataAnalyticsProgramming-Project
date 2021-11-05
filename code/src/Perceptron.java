/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not copy or distribute
 * without permission. Written by Pieter Robberechts, 2021
 */
import java.io.*;

/** This class is a stub for incrementally building a Perceptron model. */
public class Perceptron extends IncrementalLearner<Double> {

  private double learningRate;
  private double[] weights;


  /**
   * Perceptron constructor.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param numFeatures is the number of features.
   * @param learningRate is the learning rate
   */
  public Perceptron(int numFeatures, double learningRate) {
    this.nbExamplesProcessed = 0;
    this.learningRate = learningRate;

    /*
      FILL IN HERE
      You will need other data structures, initialize them here
    */

  }

  /**
   * This method will update the parameters of you model using the given example.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param example is a training example
   */
  @Override
  public void update(Example<Double> example) {
    super.update(example);
    /*
      FILL IN HERE
      Update the parameters given the new data to improve J(weights)
    */

  }

  /**
   * Uses the current model to calculate the likelihood that an attributeValues belongs to class
   * "1";
   *
   * <p>This method gives the output of the perceptron, before it is passed through the threshold
   * function.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param example is a test attributeValues
   * @return the likelihood that attributeValues belongs to class "1"
   */
  @Override
  public double makePrediction(Double[] example) {
    double pr = 0;
    /* FILL IN HERE */


    return pr;
  }

  /**
   * Writes the current model to a file.
   *
   * <p>The written file can be read in with readModel.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param path the path to the file
   * @throws IOException
   */
  @Override
  public void writeModel(String path) throws IOException {

    /* FILL IN HERE */

  }

  /**
   * Reads in the model in the file and sets it as the current model. Sets the number of examples
   * processed.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param path the path to the model file
   * @param nbExamplesProcessed the nb of examples that were processed to get to the model in the
   *     file.
   * @throws IOException
   */
  @Override
  public void readModel(String path, int nbExamplesProcessed) throws IOException {
    super.readModel(path, nbExamplesProcessed);

    /* FILL IN HERE */

  }

  /**
   * This runs your code to generate the required output for the assignment.
   *
   * <p>DO NOT CHANGE THIS METHOD
   */
  public static void main(String[] args) {
    if (args.length < 4) {
      System.err.println(
          "Usage: java Perceptron <learningRate> <data set> <output file> <reportingPeriod>"
              + " [-writeOutAllPredictions]");
      throw new Error("Expected 4 or 5 arguments, got " + args.length + ".");
    }
    try {
      // parse input
      double learningRate = Double.parseDouble(args[0]);
      DoubleData data = new DoubleData(args[1], ",");
      String out = args[2];
      int reportingPeriod = Integer.parseInt(args[3]);
      boolean writeOutAllPredictions =
          args.length > 4 && args[4].contains("writeOutAllPredictions");

      // initialize learner
      Perceptron perceptron = new Perceptron(data.getNbFeatures(), learningRate);

      // generate output for the learning curve
      perceptron.makeLearningCurve(data, 0, out + ".pc", reportingPeriod, writeOutAllPredictions);

    } catch (FileNotFoundException e) {
      System.err.println(e.toString());
    }
  }
}

/**
 * This class implements Data for Doubles
 *
 * <p>DO NOT CHANGE THIS CLASS
 */
class DoubleData extends Data<Double> {

  public DoubleData(String dataDir, String sep) throws FileNotFoundException {
    super(dataDir, sep);
  }

  @Override
  protected Double parseAttribute(String attrString) {
    return Double.parseDouble(attrString);
  }

  @Override
  protected Double[] emptyAttributes(int i) {
    return new Double[i];
  }
}