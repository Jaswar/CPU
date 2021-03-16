package main;

import main.control.Input;
import main.control.Output;
import main.gates.binary.AND;
import main.gates.binary.NOR;
import main.gates.binary.OR;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        BitStream streamIn1 = new BitStream(1);
        BitStream streamIn2 = new BitStream(1);
        BitStream out = new BitStream(1);

        Input r = new Input(new boolean[]{false}, streamIn1, "R", false);
        Input s = new Input(new boolean[]{true}, streamIn2, "S", false);

        OR or = new OR(streamIn1, streamIn2, out);
        AND and = new AND(streamIn1, streamIn2, out);

        Output output = new Output(out, "Q", false);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(r, s));

        run(queue);

        System.out.println(output.getData());

    }

    private static void run(List<Node> queue) {
        while (queue.size() > 0) {
            Node toEvaluate = queue.remove(0);
            toEvaluate.evaluate(queue);
        }
    }
}
