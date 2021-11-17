/*
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not copy or distribute
 * without permission. Written by Pieter Robberechts, 2021
 */

import java.util.Arrays;

/** This class is a stub for VFDT. */
public class VfdtNode {

  private VfdtNode[] children; /* child children (null if node is a leaf) */

  private final int[] possibleSplitFeatures; /* The features that this node can split on */

  private int splitFeature; /* splitting feature */

  private int[][][] nijk; /* instance counts (see papert ) */

  // NOT SURE IF THIS IS SUPPOSED TO BE ADDED IN THIS WAY
  // I'VE ADDED THIS S.T. THE EXISTING CODE DIDN'T GIVE ANY MORE ERRORS
  // AS IT DIDN'T RECOGNISE THE FOLLOWING VARIABLE
  private int nbSplits;


  /* FILL IN HERE */

  private final int[] nbFeatureValues;

  /**
   * Create and initialize a leaf node.
   *
   * <p>THIS METHOD IS REQUIRED.
   *
   * @param nbFeatureValues are the nb of values for each feature in this node. If a feature has k
   *     values, then the values are [0:k-1].
   */
  public VfdtNode(int[] nbFeatureValues, int[] possibleSplitFeatures) {
    this.possibleSplitFeatures = possibleSplitFeatures;
    this.nbFeatureValues = nbFeatureValues;
    children = null;

    /* FILL IN HERE */
  }


  /**
   * Turn a leaf node into an internal node.
   *
   * <p>THIS METHOD IS REQUIRED.
   *
   * @param splitFeature is the feature to test on this node.
   * @param nodes are the children (the index of the node is the value of the splitFeature).
   */
  public void addChildren(int splitFeature, VfdtNode[] nodes) {
    if (nodes == null) throw new IllegalArgumentException("null children");
    nbSplits++;

    /* FILL IN HERE */

  }

  /**
   * Returns the leaf node corresponding to the test attributeValues.
   *
   * <p>THIS METHOD IS REQUIRED.
   *
   * @param example is the test attributeValues to sort.
   */
  public VfdtNode sortExample(Integer[] example) {

    VfdtNode leaf = null; // change this

    /* FILL IN HERE */

    return leaf;
  }

  /**
   * Split evaluation method (function G in the paper)
   *
   * <p>Compute a splitting score for the feature featureId. For now, we'll use information gain,
   * but this may be changed. You can test your code with other split evaluations, but be sure to
   * change it back to information gain in the submitted code and for the experiments with default
   * values.
   *
   * @param featureId is the feature to be considered.
   */
  public double splitEval(int featureId) {
    return informationGain(featureId, nijk);
  }

  /**
   * Compute the information gain of a feature for this leaf node.
   *
   * <p>THIS METHOD IS REQUIRED.
   *
   * @param featureId is the feature to be considered.
   * @param nijk are the instance counts.
   */
  public static double informationGain(int featureId, int[][][] nijk) {
    double ig;

    /* FILL IN HERE */
    double S, Si, zeroes, ones;
    double[] entropyS;

    entropyS = classEntropy(nijk);
    ig = entropyS[0]; // Initialise information gain with class entropy
    S  = entropyS[1];

    for (int v = 0; v < nijk[featureId].length; v++) { // values of features[featureId]
      zeroes = nijk[featureId][v][0];
      ones   = nijk[featureId][v][1];
      Si     = zeroes + ones;
      ig    -= (Si / S) * classEntropy(nijk[featureId][v], Si);
    }
    return ig;
  }

  private static double[] classEntropy(int[][][] nijk) {
    // In our case, y is binary so can only be 1 or 0
    int nbOnes = 0;   // nb. of instances classified as 1
    int nbZeroes = 0; // nb. of instances classified as 0

    // TODO can this be done more efficiently?
    for (int i = 0; i < nijk.length; i++) {       // Over all possible features...
      for (int j = 0; j < nijk[i].length; j++) {  // ... find all instances classified as one and zero
        nbOnes += nijk[i][j][1];
        nbZeroes += nijk[i][j][0];
      }
    }

    double S = (double) nbOnes + nbZeroes;
    double p0 = (double) nbZeroes / S;
    double p1 = (double) nbOnes / S;
    return new double[] {- (p0 * Math.log(p0) / Math.log(2) + p1 * Math.log(p1) / Math.log(2)), S};
  }

  private static double classEntropy(int[] nk, double Si) {
    // Calculate Class entropy for values of feature i
    double p0i = (double) nk[0] / Si;
    double p1i = (double) nk[1] / Si;
    return - p0i * Math.log(p0i) / Math.log(2) + p1i * Math.log(p1i) / Math.log(2);
  }

  /**
   * Return the visualization of the tree.
   *
   * <p>DO NOT CHANGE THIS METHOD.
   *
   * @return Visualization of the tree
   */
  public String getVisualization(String indent) {
    if (children == null) {
      return indent + "Leaf\n";
    } else {
      String visualization = "";
      for (int v = 0; v < children.length; v++) {
        visualization += indent + splitFeature + "=" + v + ":\n";
        visualization += children[v].getVisualization(indent + "| ");
      }
      return visualization;
    }
  }


}
