package com.tempo.hierarchy;

import java.util.ArrayList;

/**
 * A node is present in the filtered hierarchy iff its node ID passes the predicate and all of its ancestors pass it as well.
 */
class HierarchyFilter {
    public static Hierarchy filter(Hierarchy hierarchy, java.util.function.IntPredicate nodeIdPredicate) {
        // todo implement
        ArrayList<Integer> keptIds = new ArrayList<>();
        ArrayList<Integer> keptDepths = new ArrayList<>();

        boolean[] includedAtDepth = new boolean[Math.max(1, hierarchy.size())];

        for (int i = 0; i < hierarchy.size(); i++) {
            int nodeId = hierarchy.nodeId(i);
            int depth = hierarchy.depth(i);

            // If at least one of the depth-1 node is kept, then this node will have a valid parent.
            // Check if the node is a root or its parent is kept
            boolean parentIncluded = depth == 0 || includedAtDepth[depth - 1];
            // If the node's parent is kept and the node passes the predicate, then this node will be kept
            // If this node is kept, set includedAtDepth to be true for the depth of this node.
            boolean keep = parentIncluded && nodeIdPredicate.test(nodeId);
            includedAtDepth[depth] = keep;

            if (keep) {
                keptIds.add(nodeId);
                keptDepths.add(depth);
            }
        }

        int[] ids = new int[keptIds.size()];
        int[] ds = new int[keptDepths.size()];

        for (int i = 0; i < keptIds.size(); i++) {
            ids[i] = keptIds.get(i);
            ds[i] = keptDepths.get(i);
        }

        return new ArrayBasedHierarchy(ids, ds);
    }
}
