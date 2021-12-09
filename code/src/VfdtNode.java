/*
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not copy or distribute
 * without permission. Written by Pieter Robberechts, 2021
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

  private int[] nbFeatureValues;
  private int nbOnes = 0;
  private int nbZeroes = 0;
  private int nbExamplesProcessed = 0;
  private int[] childrenIds;

  /* self-added fields */
  private int identifier = -1;

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
    this.children = null;
    this.nijk = new int[nbFeatureValues.length][][];

    // this.nijk = new int[possibleSplitFeatures.length][][];
    // Only keep counts for attributes still present in possibleSplitFeatures
    // We keep the array of size `nbFeatureValues` s.t. its indexes can still be used as feature values indexes
    // => this idea is not possible to implement due to the static type of the informationGain method

    List<Integer> splitFeatures = Arrays.stream(possibleSplitFeatures).boxed().collect(Collectors.toList());
    for (int i = 0; i < nijk.length; i++) {
      if (splitFeatures.contains(i))
        this.nijk[i] = new int[nbFeatureValues[i]][2];
      else
        // This feature has already been split on in one of this node's parents
        // thus, only one value of this feature will appear in the examples sorted to this node
        // -> No need to track counts
        // (We will need to keep an entry for this feature in nijk in order to be able to keep using
        //  indexes as feature id's)
        this.nijk[i] = null;
    }
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
    this.nbSplits++;
    this.splitFeature = splitFeature;

    /* FILL IN HERE */
    this.children = nodes;
  }

  /**
   * Returns the leaf node corresponding to the test attributeValues.
   *
   * <p>THIS METHOD IS REQUIRED.
   *
   * @param example is the test attributeValues to sort.
   */
  public VfdtNode sortExample(Integer[] example) {
    /* FILL IN HERE */
    if (this.children == null)
      return this;
    VfdtNode nextNode = children[example[splitFeature]];
    return nextNode.sortExample(example);
  }

  protected VfdtNode[] generateChildren(int X_a) {
    // This check should always be false:
//    if (IntStream.of(possibleSplitFeatures).noneMatch(f -> f == X_a))
//      return null;

    // Add a new leaf l_m , and let X_m = X − {X_a}
    VfdtNode[] children = new VfdtNode[nbFeatureValues[X_a]];

    // Only one value possible of attribute X_a for this leaf
    int[] newNbFeatureValues = nbFeatureValues;
    // newNbFeatureValues[X_a] = 1;
    // -> This is handled in the VfdtNode constructor when setting up the nijk array

    // This leaf can no longer split on feature X_a
    int[] newPossibleSplitFeatures = Arrays.stream(possibleSplitFeatures).filter(f -> f != X_a).toArray().clone();

    // Create the children
    for (int i = 0; i < children.length; i++)
      children[i] = new VfdtNode(newNbFeatureValues, newPossibleSplitFeatures);

    return children;
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

    if (S == 0.0)
      return 0.0;

    for (int v = 0; v < nijk[featureId].length; v++) { // values of features[featureId]
      zeroes = nijk[featureId][v][0];
      ones   = nijk[featureId][v][1];
      Si     = zeroes + ones;
      ig    -= (Si / S) * classEntropy(Si, ones, zeroes);
    }
    return ig;
  }

  // Functions correctly
  private static double[] classEntropy(int[][][] nijk) {
    // In our case, y is binary so can only be 1 or 0
    int nbOnes = 0;   // nb. of instances classified as 1
    int nbZeroes = 0; // nb. of instances classified as 0

    // TODO can this be done more efficiently?
    // We also hold count of the nb of ones and zeros in non static fields,
    // but the information gain method header was given to be static...
    // More efficient: calculate the number of seen ones and zeros based on the
    // first non-null entry in nijk[][]: this feature contains a one or zero for
    // every example that has been processed by this leaf.
    // -> No need to iterate over all leaves (this gives possibleSplitFeatures.length * nbOnes instead)
    for (int[][] feature : nijk) {       // Over all possible features...
      if (feature == null) continue; // skip features that have already been split on
      for (int[] featureValue : feature) {  // ... find all instances classified as one and zero
        nbOnes += featureValue[1];
        nbZeroes += featureValue[0];
      }
      break;
    }

    double S = (double) nbOnes + nbZeroes;
    if (S == 0.0) // then nbZeroes == nbOnes == S == 0, i.e. maximal impurity
      return new double[] {0.0, S};
    double p0 = (double) nbZeroes / S;
    double p1 = (double) nbOnes / S;
    return new double[] {- (p0 * Math.log(p0) / Math.log(2) + p1 * Math.log(p1) / Math.log(2)), S};
  }

  // Functions correctly
  private static double classEntropy(double Si, double ones, double zeroes) {
    // Calculate Class entropy for values of feature i
    if (Si == 0.0)
      return 1.0;
    double p0i = zeroes / Si;
    double p1i = ones / Si;
    double result = -(p0i * Math.log(p0i) / Math.log(2) + p1i * Math.log(p1i) / Math.log(2));
    return Double.isNaN(result) ? 0.0 : result;
  }

  /**
   * Increment the count of number of occurrences of
   * feature i with value j and class k by one.
   *
   * @param i: the feature id
   * @param j: the value of the feature j
   * @param k: the class value
   */
  public void incrementNijk(int i, int j, int k) {
    nijk[i][j][k]++;
  }

  /**
   * Modify this VfdtNode's instance count at the given indexes
   *
   * @param i: The feature of the instance's count to set
   * @param j: The value of feature i of the instance's count to set
   * @param k: The class of the instance's count to set
   * @param n: The count of instance with feature i, value j and class k to set
   */
  public void setNijk(int i, int j, int k, int n) {
    this.nijk[i][j][k] = n;
  }

  /**
   * Get a {@link Set} containing: <ul>
   *     <li>as first element a {@link List} of the internal nodes of this Vfdt,</li>
   *    <li> and as second element another {@link List} containing the leaf nodes of this Vfdt.</li>
   * </ul>
   *
   * @return nodes: a {@link Set} containing: <ul>
   *     <li> as first element a {@link List} of the internal nodes of this Vfdt,</li>
   *    <li> and as second element another {@link List} containing the leaf nodes of this Vfdt</li>
   * </ul>
   */
  public List<List<VfdtNode>> getNodes() {
    List<List<VfdtNode>> nodes = new ArrayList<>(); // nodes.get(0) == internal nodes, nodes.get(1) == leaf nodes
    nodes.add(new ArrayList<>()); // internal nodes
    nodes.add(new ArrayList<>()); // leaf nodes
    if (this.children == null)
      nodes.get(1).add(this); // works because of reference semantics
    else {
      nodes.get(0).add(this);
      for (VfdtNode child : children) {
        List<List<VfdtNode>> childResult;
        childResult = child.getNodes();
        nodes.get(0).addAll(childResult.get(0));
        nodes.get(1).addAll(childResult.get(1));
      }
    }
    return nodes;
  }

  @Override
  public String toString() {
    return "VfdtNode <" +
            "id: " + identifier +
            " Ch: " + Arrays.toString(childrenIds) +
            " pf: " + Arrays.toString(possibleSplitFeatures) +
            ">";
  }

  /**
   * Get the number of examples in this leaf node
   * that have class 1
   *
   * @return nbOnes: the number of examples in this leaf node
   *                 that have class 1
   */
  public int getNbOnes() {
    return nbOnes;
  }

  /**
   * Get the number of examples in this leaf node
   * that have class 0
   *
   * @return nbOnes: the number of examples in this leaf node
   *                 that have class 0
   */
  public int getNbZeroes() {
    return nbZeroes;
  }

  public int getNbExamplesProcessed() {
    return nbExamplesProcessed;
  }

  /**
   * Increment the number of instances classified as one or zero
   * by one, dependent of the given class value.
   */
  public void incrementClassCounts(int toAdd) {
    if (toAdd == 1)
      this.nbOnes++;
    else if (toAdd == 0)
      this.nbZeroes++;
    else
      throw new IllegalArgumentException("Class value must be 0 or 1");
    this.nbExamplesProcessed++;
  }

  /**
   * Get the features on which this leaf node
   * can still be split.
   *
   * @return possibleSplitFeatures: an integer array of features
   *  on which this node can still be split.
   */
  public int[] getPossibleSplitFeatures() {
    return possibleSplitFeatures;
  }

  /**
   * Get the instance counts of this leaf
   *
   * @return nijk, the instance counts of this leaf.
   * n[i][j][k] means: there are n instances which have
   * value j for feature i and are of class k
   */
  public int[][][] getNijk() {
    return nijk;
  }

  /**
   * Get the feature this node was split on
   *
   * @return splitFeature: the feature this node was split on
   */
  public int getSplitFeature() {
    return splitFeature;
  }

  /**
   * Get the children of this node
   *
   * @return children: an array of {@link VfdtNode} which contains
   * the children of this {@link VfdtNode}.
   */
  public VfdtNode[] getChildren() {
    return children;
  }

  public void setIdentifier(int id) {
    this.identifier = id;
  }

  public int getIdentifier() {
    return this.identifier;
  }

  public int[] getChildrenIds() {
    return childrenIds;
  }

  public void setChildrenIds(int[] ids) {
    this.childrenIds = ids;
  }

  public void setSplitFeature(int f) {
    this.splitFeature = f;
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
