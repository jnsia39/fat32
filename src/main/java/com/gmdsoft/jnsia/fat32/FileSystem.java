package com.gmdsoft.jnsia.fat32;

import tech.favware.result.Result;

import java.util.HashMap;
import java.util.Map;

public class FileSystem {
    final Map<String, Node> nodes;

    FileSystem() {
        this.nodes = new HashMap<>();
    }

    public Result<FileSystem> unfold(Node node) {
        try {
            for (Node child: node.children) {
                if (child.isExpandable())
                    _unfold(child,child.name + "/");

                nodes.put(child.name, child);
            }
        } catch (Exception ex) {
            return Result.err(ex);
        }

        return Result.ok(this);
    }

    private void _unfold(Node node, String path) {
        for (Node child: node.children) {
            String childPath = path + child.name;

            if (child.isExpandable())
                _unfold(child,childPath + "/");

            nodes.put(path + child.name, child);
        }
    }
    
    public Result<Node> get(String path) {
        Node node = nodes.get(path);

        return node == null
                ? Result.err(new Exception("Node Not Found"))
                : Result.ok(node);
    }

    @Override
    public String toString() {
        return nodes.keySet().toString().replace(",", "\n");
    }
}
