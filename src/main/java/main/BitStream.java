package main;

import main.exceptions.BitStreamInputSizeMismatch;
import main.utils.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class BitStream {

    private List<Node> endpoints;
    private boolean[] data;
    private Node source;

    /**Constructors for the BitStream class.
     *
     * @param size - the size of the stream (ie how many bits should be contained)
     * @param endpoints - a list of endpoints/nodes connected with this stream
     */
    public BitStream(int size, List<Node> endpoints) {
        this.endpoints = endpoints;
        this.data = new boolean[size];
        this.source = null;

        initializeBitStream();
    }

    public BitStream(int size) {
        this(size, new ArrayList<>());
    }

    /**Method used to initialize the data component to an array consisting of only "false".
     */
    private void initializeBitStream() {
        for (int i = 0; i < this.getSize(); i++) {
            this.data[i] = false;
        }
    }

    /**Getters for all the attributes of the BitStream class
     */
    public List<Node> getEndpoints() {
        return endpoints;
    }

    public boolean[] getData() {
        return data;
    }

    public int getSize() {
        return this.data.length;
    }

    public Node getSource() {return this.source;}


    /**Setters for all the attributes of the BitStream class
     */
    public void setEndpoints(List<Node> endpoints) {
        this.endpoints = endpoints;
    }

    public void setData(boolean[] data) {
        if (data.length != this.data.length) {
            throw new BitStreamInputSizeMismatch(this);
        }
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
        }
    }

    public void setSource(Node source) {this.source = source;}


    /**Add a new endpoint to the BitStream.
     *
     * @param node - the endpoint to add
     */
    public void addNewEndpoint(Node node) {this.endpoints.add(node);}

    /**Remove an endpoint from the BitStream.
     *
     * @param node - the endpoint to remove
     */
    public void removeEndpoint(Node node) {
        this.endpoints.remove(node);
    }

    /**Get all neighbours of a node connected with this BitStream.
     *
     * @param node - the node whose neighbours should be returned
     * @return - the list of neighbours of node
     */
    public List<Node> getAllNeighbours(Node node) {
        List<Node> out = new ArrayList<>();

        int countCallingNode = 0;
        for (Node neighbour : this.endpoints) {
            if (neighbour != node) {
                out.add(neighbour);
            }
            else {
                countCallingNode++;
            }
        }

        for (int i = 0; i < countCallingNode - 1; i++) {
            out.add(node);
        }
        return out;
    }

    /**Override the default toString method.
     *
     * @return - a String represenation of this
     */
    @Override
    public String toString() {
        return "BitStream<" + this.getSize() + ", " +
                DataConverter.convertBoolToBin(this.data) + ">";
    }
}
