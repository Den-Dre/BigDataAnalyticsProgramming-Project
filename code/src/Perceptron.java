/*
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not copy or distribute
 * without permission. Written by Pieter Robberechts, 2021
 */

import java.io.*;
import java.util.Random;
import java.util.Scanner;

/** This class is a stub for incrementally building a Perceptron model. */
public class Perceptron extends IncrementalLearner<Double> {

  private final double learningRate;
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
    // Initialise weights to random small values
    // Make sure to sample evenly from negative and positive values
    // Don't initialise these to 0 as this will make the weights independent of eta!
    // source: https://datascience.stackexchange.com/questions/26134/initialize-perceptron-weights-with-zero
    this.weights = new double[numFeatures+1]; // Take bias term b into account
    final double stdDev = 0.001;
    Random r = new Random();
    // Initialise bias to 0, weights to normally distributed numbers with standard deviation of `stdDev` and mean 0
    for (int i = 1; i < this.weights.length; i++) {
      this.weights[i] = r.nextGaussian() * stdDev;
    }

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
    updateWeights(example);
  }


  /**
   * The Heaviside step function: the threshold function of the
   * standard perceptron.
   *
   * @param input: The instance to pass in the threshold function
   * @return input < 0 ? -1 : 1
   */
  private double thresholdFunction(double input) {
    return input < 0.0 ? -1.0 : 1.0;
  }

  /**
   * Calculate the dot product of {@code this.weights} and
   * the given {@code example}'s {@code attributeValue}s.
   *
   * @param example: The example that contains the {@code attributeValue}s.
   * @return the dot product of {@code this.weights} and
   *         the given {@code example}'s {@code attributeValue}s.
   */
  private double weightsDotProduct(Example<Double> example) {
    double sum = weights[0];
    for (int i = 0; i < example.attributeValues.length; i++) {
      sum += example.attributeValues[i] * weights[i+1];
    }
    return sum;
  }

  /**
   * Update the weights of this Perceptron
   * using the delta rule
   */
  private void updateWeights(Example<Double> example) {
    // NOTE we offset the attributeValues indexes by 1, as attributeValues[0] is assumed to be 1
    // s.t. weights[0] * attributeValues[0] = weights[0] = b to account for the bias
    double predicted  = weightsDotProduct(example);
    // map input of 0 and 1 to -1 and 1 respectively:
    double update = learningRate * (example.classValue*2-1 - thresholdFunction(predicted));
    weights[0] += update;
    for (int i = 0; i < example.attributeValues.length; i++) {
      // We implicitly apply Stochastic Gradient Descent as we're only using one example
      // to update the weights, instead of using all examples in our data set.
      weights[i+1] += update * example.attributeValues[i];
    }
  }

  /**
   * Uses the current model to calculate the likelihood that an attributeValues belongs to class
   * "1";
   *
   * <p>This method gives the output of the perceptron, before it is passed through the threshold
   * function, i.e.: the exactly calculated predicted probability.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param example is a test attributeValues
   * @return the likelihood that attributeValues belongs to class "1"
   */
  @Override
  public double makePrediction(Double[] example) {
    double pr = weights[0];
    /* FILL IN HERE */
    for (int i = 0; i < example.length; i++) {
      pr += weights[i+1] * example[i];
    }
    return pr;
  }

  /**
   * Info to print when testing. This is mainly for debug purpose
   *
   * <p>You can override this method in VFDT or LogisticRegression to print the information you
   * wish.
   */
  @Override
  public String getInfo() {
    return "weights: " + weights[0] + " " + weights[1] + " " +  weights[2];
  }

  /**
   * Writes the current model to a file.
   *
   * <p>The written file can be read in with readModel.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param path the path to the file
   * @throws IOException if the file specified by the {@code path}
   *                     parameter can't be read
   */
  @Override
  public void writeModel(String path) throws IOException {
    /* FILL IN HERE */
    StringBuilder outputBuilder = new StringBuilder();
    for (double weight : weights) {
      outputBuilder.append(weight).append(" ");
    }
    outputBuilder.deleteCharAt(outputBuilder.length()-1); // Delete trailing whitespace
    BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
    writer.write(outputBuilder.toString());
    writer.flush();
    writer.close();
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
   * @throws IOException if the file specified by {@code path} parameter can't be read
   */
  @Override
  public void readModel(String path, int nbExamplesProcessed) throws IOException {
    super.readModel(path, nbExamplesProcessed);

    /* FILL IN HERE */
    System.out.println(System.getProperty("user.dir"));
    Scanner scanner;
    path = System.getProperty("user.dir") + "/" + path;
    scanner = new Scanner(new FileReader(path));

    // Model should only consist of one line
    String newWeights = scanner.nextLine();

    String[] splitWeights = newWeights.split(" ");
    for (int i = 0; i < splitWeights.length; i++ ) {
      nbExamplesProcessed++;
      weights[i] = Double.parseDouble(splitWeights[i]);
    }
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
