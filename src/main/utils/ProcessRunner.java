package main.utils;

import main.Node;

import java.util.ArrayList;
import java.util.List;

public class ProcessRunner {

    /**Run the circuit starting from the nodes specified as parameters.
     *
     * @param nodes - the nodes from which the evaluation should start
     */
    public static void run(Node... nodes) {
        List<Node> queue = new ArrayList<>();
        for (Node node : nodes) {
            queue.add(node);
        }
        while (queue.size() > 0) {
            Node next = queue.remove(0);
            next.evaluate(queue);
        }
    }
}
