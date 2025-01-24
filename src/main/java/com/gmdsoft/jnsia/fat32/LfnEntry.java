package com.gmdsoft.jnsia.fat32;

import java.nio.ByteBuffer;
import java.util.Stack;

public class LfnEntry extends Stack {
    Stack<String> stack;

    LfnEntry() {
        this.stack = new Stack<>();
    }

    public String makeLfnFrom(ByteBuffer buf) {
        int[] offsets = {1, 3, 5, 7, 9, 14, 16, 18, 20, 22, 24, 28, 30};
        StringBuilder sb = new StringBuilder();

        for (int offset : offsets) {
            char c = (char) buf.getShort(offset);

            if (c == 0xFFFF)
                break;

            if (c != 0x0000)
                sb.append(c);
        }

        return sb.toString();
    }
}
