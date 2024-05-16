package game.packet;

import event.EventBus;

import java.util.Arrays;
import java.util.HashMap;

public abstract class AbstractPacket {

    /**
     * Packet format:
     * 32 bits - source
     * 32 bits - destination
     * 128 bits - content
     * 2 bit - fragmented
     * 4 bits - fragmentNo.
     * 4 bits - fragmentPart.
     * 6 bits - packetID.
     * 48 bits - Unused
     */
    private byte[] packetFormat;
    private static HashMap<String, Range> definitionTable = new HashMap<>();

    static {
        definitionTable.put("Source", new Range(0, 32));
        definitionTable.put("Destination", new Range(32, 64));
        definitionTable.put("Content", new Range(64, 196));
        definitionTable.put("Fragmented", new Range(196, 198));
        definitionTable.put("FragmentNumber", new Range(198, 202));
        definitionTable.put("FragmentPart", new Range(202, 206));
        definitionTable.put("PacketID", new Range(206, 212));
        definitionTable.put("Unused", new Range(212, 256));
    }


    public AbstractPacket () {
        this.packetFormat = new byte[256];
    }

    public void fillPacket (String source, String destination, String content) {
        appendToPart("Source", source);
        appendToPart("Destination", destination);
        appendToPart("Content", content);
    }


    public boolean appendToPart(String part, String source) {
        Range range = getRangeFromPart(part);
        int n = range.max - range.min;
        if(source.length() > n) {
            System.err.println("Length of source is longer than number of spaces defined. Fragmenting is necessary for content: " + source);
            return true;
        }

        for(int i = range.min; i < range.max; i++) {
            if((i - range.min) >= source.length()) {
                packetFormat[i] = 0;
                continue;
            }
            packetFormat[i] = (byte) source.charAt(i - range.min);
        }

        return false;
    }

    @Override
    public String toString () {
        return Arrays.toString(packetFormat);
    }

    public String decodePart (String part) {
        StringBuilder out = new StringBuilder();
        Range range = getRangeFromPart(part);
        for(int i = range.min; i < range.max; i++) {
            if(packetFormat[i] == 0x0) {
                break;
            }
            out.append((char) packetFormat[i]);
        }
        return out.toString();
    }

    private Range getRangeFromPart (String part) {
        Range range = definitionTable.getOrDefault(part, new Range(-1, -1));
        if (range.compareTo(new Range(-1, -1)) == 0) {
            System.err.printf("Part of the packet %s is not defined\n", part);
        }
        return range;
    }

    private record Range(int min, int max) implements Comparable<Range> {
        @Override
        public int compareTo(Range o) {
            if(o.min == this.min && o.max == this.max) {
                return 0;
            } else {
                if(Math.abs((o.max - o.min) - (this.max - this.min)) > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }

    public void raise () {

    }

}
