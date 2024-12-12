package com.gmdsodt.jskim;

import java.util.HashMap;
import java.util.Map;

public class FileSystem {
    final Map<String, Node> nodes;

    FileSystem() {
        this.nodes = new HashMap<>();
    }

    public boolean unfold(Node node, String path) {
        try {
            for (Node child: node.children) {
                String childPath = path + child.name;

                if (child.isExpandable()) {
                    unfold(child,childPath + "/");
                }

                nodes.put(path + child.name, child);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    
    public Node get(String path) {
        return nodes.get(path);
    }

    @Override
    public String toString() {
        return nodes.keySet().toString().replace(",", "\n");
    }
}
