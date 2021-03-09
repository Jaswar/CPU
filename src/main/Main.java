package main;

import main.control.Input;
import main.control.Output;
import main.gates.BinaryGate;
import main.gates.NOR;
import main.gates.OR;

import java.util.ArrayList;
import java.util.List;

public class Main {


//    public static void main(String[] args) {
//        BitStream streamIn1 = new BitStream(1);
//        BitStream streamIn2 = new BitStream(1);
//        BitStream streamOut1 = new BitStream(1);
//        BitStream streamOut2 = new BitStream(1);
//
//        Input r = new Input(new boolean[]{false}, streamIn1, "R", false);
//        Input s = new Input(new boolean[]{false}, streamIn2, "S", false);
//
//        NOR upperNor = new NOR(streamIn1, streamOut2, streamOut1, "upper NOR", false);
//        NOR lowerNor = new NOR(streamIn2, streamOut1, streamOut2, "lower NOR", false);
//
//        Output q = new Output(streamOut1, "Q", false);
//        Output notQ = new Output(streamOut2, "-Q", false);
//
//        List<Node> queue = new ArrayList<>();
//        queue.addAll(List.of(r, s));
//
//        run(queue);
//
//        System.out.println(streamOut1);
//        q.debug();
//        notQ.debug();
//
//    }
//
//    private static void run(List<Node> queue) {
//        while (queue.size() > 0) {
//            Node toEvaluate = queue.remove(0);
//            toEvaluate.evaluate(queue);
//        }
//    }
}
