/**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
 This product was created by Lennard Stubbe
 and is licensed under the CC BY-NC-SA license.
 Therefore, the product may be changed and shared as desired,
 but not for commercial use.
 *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

package com.example.graphenprogramm.graphLogic.Algorithm;

import com.example.graphenprogramm.graphLogic.Node;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {
    protected List<Node> nodes;
    protected List<Node> inProcess = new ArrayList<>();
    protected List<Node> checked = new ArrayList<>();

    protected Node startNode;
    protected Node endNode;

    public static boolean animationIsPlaying = false;

    public Algorithm(List<Node> nodes, Node startNode, Node endNode) {
        this.nodes = nodes;
        this.startNode = startNode;
        this.endNode = endNode;
    }
}
