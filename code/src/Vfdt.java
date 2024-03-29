/*
 * Copyright (c) DTAI - KU Leuven – All rights reserved. Proprietary, do not copy or distribute
 * without permission. Written by Pieter Robberechts, 2021
 */

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/** This class is a stub for VFDT. */
public class Vfdt extends IncrementalLearner<Integer> {

  private int[] nbFeatureValues;
  private double delta;
  private double tau;
  private double nmin;

  private VfdtNode root;

  /* self-added attributes */
  private int nbOfNodes;
  private int nbOfSplits;

  /**
   * Vfdt constructor
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param nbFeatureValues are nb of values of each feature. e.g. nbFeatureValues[3]=5 means that
   *     feature 3 can have values 0,1,2,3 and 4.
   * @param delta is the parameter used for the Hoeffding bound
   * @param tau is the parameter that is used to deal with ties
   * @param nmin is the parameter that is used to limit the G computations
   */
  public Vfdt(int[] nbFeatureValues, double delta, double tau, int nmin) {
    this.nbFeatureValues = nbFeatureValues;
    this.delta = delta;
    this.tau = tau;
    this.nmin = nmin;

    nbExamplesProcessed = 0;
    int[] possibleFeatures = new int[nbFeatureValues.length];
    for (int i = 0; i < nbFeatureValues.length; i++) possibleFeatures[i] = i;
    root = new VfdtNode(nbFeatureValues, possibleFeatures);
  }

  /**
   * This method will update the parameters of you model using the given example.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param example is a training example
   */
  @Override
  public void update(Example<Integer> example) {
    super.update(example);

    VfdtNode leaf = root.sortExample(example.attributeValues);
    for (int feature : leaf.getPossibleSplitFeatures()) {
      // For each xij in x such that Xi ∈ Xl
      //    Increment n_ijk(l).
        leaf.incrementNijk(feature, example.attributeValues[feature], example.classValue);
    }

    // Update number of times examples of this class have been seen
    leaf.incrementClassCounts(example.classValue);

    // Limit number of G computations
    if (leaf.getNbExamplesProcessed() % nmin != 0)
      return;

    // If the examples seen so far at l are not all of the same
    // class, then
    if (leaf.getNbOnes() == 0 || leaf.getNbZeroes() == 0)
      return;

    // Compute G_l(X_i) for each attribute X_i ∈ X_l − {X_∅}
    // using the counts n_ijk(l).
    double Gl_Xa = 0;
    double Gl_Xb = 0;
    int feature_Xa = 0;
    double currentGl;
    // Compute Gl(Xi) for each attribute Xi ∈ Xl
    for (int feature : leaf.getPossibleSplitFeatures()) {
      // Only compute Gl(Xi) for the attributes of `example` on which leaf's parents haven't already split
        currentGl = leaf.splitEval(feature);
        if (currentGl > Gl_Xa) {
          Gl_Xb = Gl_Xa;
          Gl_Xa = currentGl;
          feature_Xa = feature;
        } else if (currentGl > Gl_Xb) {
          Gl_Xb = currentGl;
        }
    }

    double eps = epsilon(leaf.getNbExamplesProcessed());
    if (Gl_Xa - Gl_Xb > eps || eps < tau) {

      if (Gl_Xa == 0) // minimum value of Information Gain
        // -> Xa == X∅
        // Then splitting on best attribute wouldn't increase information gain
        return;

      // Replace l by an internal node that splits on X_a
      VfdtNode[] children = leaf.generateChildren(feature_Xa);
      leaf.addChildren(feature_Xa, children);

      this.nbOfNodes += children.length;
      this.nbOfSplits++;

      // For each class y_k and each value x_ij of each attribute X_i ∈ X_m − {X_∅}
      // Let n_ijk(l_m) = 0.
      // -> This is done when calling the constructor of VfdtNode in generateChildren
    }
  }

  /**
   * Calculate the Hoeffding bound of a leaf which has processed {@code n} examples
   *
   * @param n: the number of examples processed by the leaf the bound is calculated for
   * @return bound: the Hoeffding bound at a leaf which has processed {@code n} examples
   */
  private double epsilon(double n) {
    // R := log2(#classes) = log2(2) = 1
    // Use number of instances processed in this leaf only, rather than in entire VFDT!
    return Math.sqrt(Math.log(1/delta) / (2*n));
  }

  /**
   * Uses the current model to calculate the probability that an attributeValues belongs to class
   * "1";
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param example is a test instance to classify
   * @return the probability that attributeValues belongs to class "1"
   */
  @Override
  public double makePrediction(Integer[] example) {
    if (nbExamplesProcessed == 0)
      return 0.5;
    VfdtNode leaf = root.sortExample(example);
    return (double) leaf.getNbOnes() / ((double) leaf.getNbOnes() + leaf.getNbZeroes());
  }

  /**
   * Writes the current model to a file.
   *
   * <p>The written file can be read in with readModel.
   *
   * <p>THIS METHOD IS REQUIRED
   *
   * @param path the path to the file
   * @throws IOException if the file can't be written
   */
  @Override
  public void writeModel(String path) throws IOException {
    StringBuilder outputBuilder = new StringBuilder();
    outputBuilder.append(this.nbOfNodes).append("\n");
    List<List<VfdtNode>> nodes = root.getNodes();
    List<VfdtNode> internalNodes = nodes.get(0);
    List<VfdtNode> leafNodes = nodes.get(1);

    // Write leaf node representations
    int id = 0;
    for (VfdtNode leaf : leafNodes) {
      leaf.setIdentifier(id);
      outputBuilder.append(id++).append(" L pf:[");
      for (int f : leaf.getPossibleSplitFeatures())
        outputBuilder.append(f).append(",");
      outputBuilder.append("] nijk:[");
      int[][][] nijk = leaf.getNijk();
      for (int i = 0; i < nijk.length; i++) {
        if (nijk[i] == null) continue;
        for (int j = 0; j < nijk[i].length; j++) {
          for (int k = 0; k <= 1; k++) {
            outputBuilder
                    .append(i).append(":")
                    .append(j).append(":")
                    .append(k).append(":")
                    .append(nijk[i][j][k]).append(",");
          }
        }
      }
      outputBuilder.append("]\n");
    }

    // Write internal node representations
    if (internalNodes.get(0).getIdentifier() != -1) {
      internalNodes.sort(Comparator.comparingInt(VfdtNode::getIdentifier));
    }
    for (VfdtNode node : internalNodes) {
      outputBuilder .append(node.getIdentifier() != -1 ? node.getIdentifier() : id) .append(" D f:").append(node.getSplitFeature()).append(" ch:[");
      for (VfdtNode child: node.getChildren()) {
        // Add identifier of child to writer
        // Child's identifier was set in the loop above
        outputBuilder.append(child.getIdentifier()).append(",");
      }
      outputBuilder.append("]\n");
      id++;
    }

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
   * @throws IOException: if the model can't be read.
   */
  @Override
  public void readModel(String path, int nbExamplesProcessed) throws IOException {
    super.readModel(path, nbExamplesProcessed);

    this.nbExamplesProcessed = nbExamplesProcessed;

    System.out.println(System.getProperty("user.dir"));
    Scanner scanner;
    path = System.getProperty("user.dir") + "/" + path;
    scanner = new Scanner(new FileReader(path));
    this.nbOfNodes = Integer.parseInt(scanner.nextLine());

    String[] splitLine;
    Map<Integer, VfdtNode> leafNodes = new LinkedHashMap<>();     // Preserve insertion order
    Map<Integer, VfdtNode> internalNodes = new LinkedHashMap<>(); // Preserve insertion order
    int id;
    VfdtNode node;

    // Read in the file and convert to VfdtNodes
    while (scanner.hasNext()) {
      splitLine = scanner.nextLine().split(" ");
      id = Integer.parseInt(splitLine[0]);
      if (splitLine[1].equals("L"))
        leafNodes.put(id, parseLeaf(splitLine));
      else {
        node = parseInternalNode(splitLine);
        if (!scanner.hasNext()) // Last line of file
          this.root = node;
        internalNodes.put(id, node);
      }
    }

    // Build the Vfdt
    for (Entry<Integer, VfdtNode> internalNode : internalNodes.entrySet()) {
      node = internalNode.getValue();
      int[] childrenIds = node.getChildrenIds();
      VfdtNode[] children = new VfdtNode[childrenIds.length];

      int currId;
      for (int i = 0; i < childrenIds.length; i++) {
        currId = childrenIds[i];
        children[i] = leafNodes.containsKey(currId) ? leafNodes.get(currId) : internalNodes.get(currId);
      }
      node.addChildren(node.getSplitFeature(), children);
    }
  }

  private VfdtNode parseLeaf(String[] split) {
    String pfArray = split[2];
    int nbPossibleSplitFeatures = (int) (((double) pfArray.length() - 5) / 2.0);
    int[] possibleSplitFeatures = new int[nbPossibleSplitFeatures];
    int i = 0;
    for (String feature : pfArray.substring(4,pfArray.length()-1).split(",")) {
      possibleSplitFeatures[i] = Integer.parseInt(feature);
      i++;
    }

    VfdtNode leaf = new VfdtNode(this.nbFeatureValues, possibleSplitFeatures);
    leaf.setIdentifier(Integer.parseInt(split[0]));

    String[] counts = getCounts(split);
    if (counts.length == 1) // Empty nijk array on this line
      return leaf;

    String[] numbers;
    for (String count : counts) {
      numbers = count.split(":");
      leaf.setNijk(
              Integer.parseInt(numbers[0]),
              Integer.parseInt(numbers[1]),
              Integer.parseInt(numbers[2]),
              Integer.parseInt(numbers[3])
      );
    }
    return leaf;
  }

  private String[] getCounts(String[] split) {
    // Return an array containing only the nijk quartets "x0:x1:x2:x3"
    return split[3].substring(6, split[3].length()-1).split(",");
  }

  private VfdtNode parseInternalNode(String[] split) {
    int f = Character.getNumericValue(split[2].charAt(2));
    int id = Integer.parseInt(split[0]);
    String ch = split[3];
    int nbChildren = (int) (((double) (ch.length() - 5)) / 2.0);
    int[] childrenIds = new int[nbChildren];

    int i = 0;
    String[] idStrings = ch.substring(4,ch.length()-1).split(",");
    for (String childId : idStrings) {
      childrenIds[i] = Integer.parseInt(childId);
      i++;
    }

    VfdtNode internalNode = new VfdtNode(nbFeatureValues, new int[] {f});
    internalNode.setIdentifier(id);
    internalNode.setChildrenIds(childrenIds);
    internalNode.setSplitFeature(f);
    return internalNode;
  }

  /**
   * Return the visualization of the tree.
   *
   * <p>DO NOT CHANGE THIS METHOD.
   *
   * @return Visualization of the tree
   */
  public String getVisualization() {
    return root.getVisualization("");
  }

  /**
   * Info to print when testing. This is mainly for debug purpose
   *
   * <p>You can override this method in VFDT or LogisticRegression to print the information you
   * wish.
   */
  @Override
  public String getInfo() {
    return "Nb. of splits: " + this.nbOfSplits;
  }

  /**
   * This runs your code to generate the required output for the assignment.
   *
   * <p>DO NOT CHANGE THIS METHOD.
   */
  public static void main(String[] args) {
    if (args.length < 7) {
      System.err.println(
          "Usage: java Vfdt <delta> <tau> <nmin> <data set> <nbFeatureValues> <output file>"
              + " <reportingPeriod> [-writeOutAllPredictions]");
      throw new Error("Expected 7 or 8 arguments, got " + args.length + ".");
    }
    try {
      // parse input
      double delta = Double.parseDouble(args[0]);
      double tau = Double.parseDouble(args[1]);
      int nmin = Integer.parseInt(args[2]);
      Data<Integer> data = new IntData(args[3], ",");
      int[] nbFeatureValues = parseNbFeatureValues(args[4]);
      String out = args[5];
      int reportingPeriod = Integer.parseInt(args[6]);
      boolean writeOutAllPredictions =
          args.length > 7 && args[7].contains("writeOutAllPredictions");

      // initialize learner
      Vfdt vfdt = new Vfdt(nbFeatureValues, delta, tau, nmin);

      // generate output for the learning curve
      vfdt.makeLearningCurve(data, 0.5, out + ".vfdt", reportingPeriod, writeOutAllPredictions);

    } catch (IOException e) {
      System.err.println(e.toString());
    }
  }

  /**
   * This method parses the file that specifies the nb of possible values for each feature.
   *
   * <p>DO NOT CHANGE THIS METHOD.
   */
  private static int[] parseNbFeatureValues(String path) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    reader.readLine(); // skip header
    String[] splitLine = reader.readLine().split(",");
    int[] nbFeatureValues = new int[splitLine.length];

    for (int i = 0; i < nbFeatureValues.length; i++) {
      nbFeatureValues[i] = Integer.parseInt(splitLine[i]);
    }
    reader.close();
    return nbFeatureValues;
  }
}

/**
 * This class implements Data for Integers
 *
 * <p>DO NOT CHANGE THIS CLASS
 */
class IntData extends Data<Integer> {

  public IntData(String dataDir, String sep) throws FileNotFoundException {
    super(dataDir, sep);
  }

  @Override
  protected Integer parseAttribute(String attrString) {
    return Integer.parseInt(attrString);
  }

  @Override
  protected Integer[] emptyAttributes(int i) {
    return new Integer[i];
  }

  public static void main(String[] args) {
    if (args.length < 3) {
      throw new Error("Expected 2 arguments, got " + args.length + ".");
    }

    try {
      Data<Integer> d = new IntData(args[0], args[1]);
      d.print();
    } catch (FileNotFoundException e) {
      System.err.print(e.toString());
    }
  }
}
