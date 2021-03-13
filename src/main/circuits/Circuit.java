package main.circuits;

/**Interface to represent circuits. The difference between the Circuit and Node,
 * is that Circuit cannot be evaluated (only the nodes within it can).
 */
public interface Circuit {
    public void build();
}
