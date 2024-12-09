package project3;
import java.io.*;
import java.util.*;

public class BTree {
    private static final int DEGREE = 10; // Minimal degree
    private static final int MAX_KEYS = 2 * DEGREE - 1;
    private static final int MAX_CHILDREN = 2 * DEGREE;

    private RandomAccessFile file;
    private long rootNodeBlockId;

    public BTree(RandomAccessFile file) throws IOException {
        this.file = file;
        this.rootNodeBlockId = file.readLong();
        if (rootNodeBlockId == 0) {
            BTreeNode root = new BTreeNode(allocateNewBlockId(), true);
            writeNode(root);
            rootNodeBlockId = root.blockId;
            updateRootNodeBlockId(root.blockId);
        }
    }

    public boolean insert(long key, long value) {
        try {
            BTreeNode root = readNode(rootNodeBlockId);
            if (root.containsKey(key)) {
                return false; // Key already exists
            }
            if (root.isFull()) {
                BTreeNode newRoot = new BTreeNode(allocateNewBlockId(), false);
                newRoot.childPointers[0] = root.blockId;
                splitChild(newRoot, 0, root);
                writeNode(newRoot);
                rootNodeBlockId = newRoot.blockId;
                updateRootNodeBlockId(newRoot.blockId);
                insertNonFull(newRoot, key, value);
            } else {
                insertNonFull(root, key, value);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error during insertion: " + e.getMessage());
            return false;
        }
    }

    public Long search(long key) {
        try {
            return searchNode(readNode(rootNodeBlockId), key);
        } catch (IOException e) {
            System.out.println("Error during search: " + e.getMessage());
            return null;
        }
    }

    public void printTree() {
        try {
            printNode(readNode(rootNodeBlockId), 0);
        } catch (IOException e) {
            System.out.println("Error during print: " + e.getMessage());
        }
    }

    private Long searchNode(BTreeNode node, long key) throws IOException {
        int i = 0;
        while (i < node.keyCount && key > node.keys[i]) {
            i++;
        }
        if (i < node.keyCount && key == node.keys[i]) {
            return node.values[i];
        }
        if (node.isLeaf) {
            return null;
        }
        return searchNode(readNode(node.childPointers[i]), key);
    }

    private void insertNonFull(BTreeNode node, long key, long value) throws IOException {
        int i = node.keyCount - 1;
        if (node.isLeaf) {
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                node.values[i + 1] = node.values[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.values[i + 1] = value;
            node.keyCount++;
            writeNode(node);
        } else {
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }
            i++;
            BTreeNode child = readNode(node.childPointers[i]);
            if (child.isFull()) {
                splitChild(node, i, child);
                if (key > node.keys[i]) {
                    i++;
                }
            }
            insertNonFull(readNode(node.childPointers[i]), key, value);
        }
    }

    private void splitChild(BTreeNode parent, int index, BTreeNode fullChild) throws IOException {
        BTreeNode newChild = new BTreeNode(allocateNewBlockId(), fullChild.isLeaf);
        newChild.keyCount = DEGREE - 1;

        System.arraycopy(fullChild.keys, DEGREE, newChild.keys, 0, DEGREE - 1);
        System.arraycopy(fullChild.values, DEGREE, newChild.values, 0, DEGREE - 1);

        if (!fullChild.isLeaf) {
            System.arraycopy(fullChild.childPointers, DEGREE, newChild.childPointers, 0, DEGREE);
        }

        fullChild.keyCount = DEGREE - 1;

        System.arraycopy(parent.childPointers, index + 1, parent.childPointers, index + 2, parent.keyCount - index);
        parent.childPointers[index + 1] = newChild.blockId;

        System.arraycopy(parent.keys, index, parent.keys, index + 1, parent.keyCount - index);
        System.arraycopy(parent.values, index, parent.values, index + 1, parent.keyCount - index);
        parent.keys[index] = fullChild.keys[DEGREE - 1];
        parent.values[index] = fullChild.values[DEGREE - 1];
        parent.keyCount++;

        writeNode(fullChild);
        writeNode(newChild);
        writeNode(parent);
    }

    private void printNode(BTreeNode node, int level) throws IOException {
        System.out.println("Level " + level + " - " + Arrays.toString(Arrays.copyOf(node.keys, node.keyCount)));
        if (!node.isLeaf) {
            for (int i = 0; i <= node.keyCount; i++) {
                printNode(readNode(node.childPointers[i]), level + 1);
            }
        }
    }

    private long allocateNewBlockId() throws IOException {
        file.seek(16);
        long nextBlockId = file.readLong();
        file.seek(16);
        file.writeLong(nextBlockId + 1);
        return nextBlockId;
    }

    private void updateRootNodeBlockId(long blockId) throws IOException {
        file.seek(8);
        file.writeLong(blockId);
    }

    private BTreeNode readNode(long blockId) throws IOException {
        file.seek(blockId * 512);
        BTreeNode node = new BTreeNode(blockId, file.readBoolean());
        node.keyCount = file.readInt();
        for (int i = 0; i < node.keyCount; i++) {
            node.keys[i] = file.readLong();
            node.values[i] = file.readLong();
        }
        if (!node.isLeaf) {
            for (int i = 0; i <= node.keyCount; i++) {
                node.childPointers[i] = file.readLong();
            }
        }
        return node;
    }

    private void writeNode(BTreeNode node) throws IOException {
        file.seek(node.blockId * 512);
        file.writeBoolean(node.isLeaf);
        file.writeInt(node.keyCount);
        for (int i = 0; i < node.keyCount; i++) {
            file.writeLong(node.keys[i]);
            file.writeLong(node.values[i]);
        }
        if (!node.isLeaf) {
            for (int i = 0; i <= node.keyCount; i++) {
                file.writeLong(node.childPointers[i]);
            }
        }
    }
    
    private static class BTreeNode {
        long blockId;
        boolean isLeaf;
        int keyCount;
        long[] keys;
        long[] values;
        long[] childPointers;

        BTreeNode(long blockId, boolean isLeaf) {
            this.blockId = blockId;
            this.isLeaf = isLeaf;
            this.keyCount = 0;
            this.keys = new long[MAX_KEYS];
            this.values = new long[MAX_KEYS];
            this.childPointers = new long[MAX_CHILDREN];
        }

        boolean isFull() {
            return keyCount == MAX_KEYS;
        }

        boolean containsKey(long key) {
            for (int i = 0; i < keyCount; i++) {
                if (keys[i] == key) {
                    return true;
                }
            }
            return false;
        }
    }
}
