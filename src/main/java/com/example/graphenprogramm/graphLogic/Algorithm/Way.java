/**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
 This product was created by Lennard Stubbe
 and is licensed under the CC BY-NC-SA license.
 Therefore, the product may be changed and shared as desired,
 but not for commercial use.
 *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

package com.example.graphenprogramm.graphLogic.Algorithm;

import com.example.graphenprogramm.graphLogic.Edge;
import com.example.graphenprogramm.graphLogic.Node;

public class Way {
    private Node node;
    private Edge edge;

    public Node getNode() {
        return this.node;
    }

    public Edge getEdge() {
        return this.edge;
    }

    public Way(Node node, Edge edge) {
        this.node = node;
        this.edge = edge;
    }
}
