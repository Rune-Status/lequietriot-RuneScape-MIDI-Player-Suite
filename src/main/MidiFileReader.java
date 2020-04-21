package main;

import main.utils.Buffer;

public class MidiFileReader {

    static final byte[] field2478;

    Buffer buffer;
    int division;
    int[] trackStarts;
    int[] trackPositions;
    int[] trackLengths;
    int[] field2474;
    int field2471;
    long field2477;

    static {
        field2478 = new byte[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    MidiFileReader(byte[] var1) {
        this.buffer = new Buffer(null);
        this.parse(var1);
    }

    MidiFileReader() {
        this.buffer = new Buffer(null);
    }

    void parse(byte[] var1) {
        this.buffer.array = var1;
        this.buffer.index = 10;
        int var2 = this.buffer.readUnsignedShort();
        this.division = this.buffer.readUnsignedShort();
        this.field2471 = 500000;
        this.trackStarts = new int[var2];

        Buffer var10000;
        int var3;
        int var5;
        for (var3 = 0; var3 < var2; var10000.index += var5) {
            int var4 = this.buffer.readInt();
            var5 = this.buffer.readInt();
            if (var4 == 1297379947) {
                this.trackStarts[var3] = this.buffer.index;
                ++var3;
            }

            var10000 = this.buffer;
        }

        this.field2477 = 0L;
        this.trackPositions = new int[var2];

        for (var3 = 0; var3 < var2; ++var3) {
            this.trackPositions[var3] = this.trackStarts[var3];
        }

        this.trackLengths = new int[var2];
        this.field2474 = new int[var2];
    }

    void clear() {
        this.buffer.array = null;
        this.trackStarts = null;
        this.trackPositions = null;
        this.trackLengths = null;
        this.field2474 = null;
    }

    boolean isReady() {
        return this.buffer.array != null;
    }

    int trackCount() {
        return this.trackPositions.length;
    }

    void gotoTrack(int var1) {
        this.buffer.index = this.trackPositions[var1];
    }

    void markTrackPosition(int var1) {
        this.trackPositions[var1] = this.buffer.index;
    }

    void setTrackDone() {
        this.buffer.index = -1;
    }

    void readTrackLength(int var1) {
        int var2 = this.buffer.readVarInt();
        int[] var10000 = this.trackLengths;
        var10000[var1] += var2;
    }

    int readMessage(int var1) {
        int var2 = this.readMessage0(var1);
        return var2;
    }

    int readMessage0(int var1) {
        byte var2 = this.buffer.array[this.buffer.index];
        int var5;
        if (var2 < 0) {
            var5 = var2 & 255;
            this.field2474[var1] = var5;
            ++this.buffer.index;
        } else {
            var5 = this.field2474[var1];
        }

        if (var5 != 240 && var5 != 247) {
            return this.method3934(var1, var5);
        } else {
            int var3 = this.buffer.readVarInt();
            if (var5 == 247 && var3 > 0) {
                int var4 = this.buffer.array[this.buffer.index] & 255;
                if (var4 >= 241 && var4 <= 243 || var4 == 246 || var4 == 248 || var4 >= 250 && var4 <= 252 || var4 == 254) {
                    ++this.buffer.index;
                    this.field2474[var1] = var4;
                    return this.method3934(var1, var4);
                }
            }

            Buffer var10000 = this.buffer;
            var10000.index += var3;
            return 0;
        }
    }

    int method3934(int var1, int var2) {
        int var4;
        if (var2 == 255) {
            int var7 = this.buffer.readUnsignedByte();
            var4 = this.buffer.readVarInt();
            Buffer var10000;
            if (var7 == 47) {
                var10000 = this.buffer;
                var10000.index += var4;
                return 1;
            } else if (var7 == 81) {
                int var5 = this.buffer.readMedium();
                var4 -= 3;
                int var6 = this.trackLengths[var1];
                this.field2477 += (long)var6 * (long)(this.field2471 - var5);
                this.field2471 = var5;
                var10000 = this.buffer;
                var10000.index += var4;
                return 2;
            } else {
                var10000 = this.buffer;
                var10000.index += var4;
                return 3;
            }
        } else {
            byte var3 = field2478[var2 - 128];
            var4 = var2;
            if (var3 >= 1) {
                var4 = var2 | this.buffer.readUnsignedByte() << 8;
            }

            if (var3 >= 2) {
                var4 |= this.buffer.readUnsignedByte() << 16;
            }

            return var4;
        }
    }

    long method3935(int var1) {
        return this.field2477 + (long)var1 * (long)this.field2471;
    }

    int getPrioritizedTrack() {
        int var1 = this.trackPositions.length;
        int var2 = -1;
        int var3 = Integer.MAX_VALUE;

        for (int var4 = 0; var4 < var1; ++var4) {
            if (this.trackPositions[var4] >= 0 && this.trackLengths[var4] < var3) {
                var2 = var4;
                var3 = this.trackLengths[var4];
            }
        }

        return var2;
    }

    boolean isDone() {
        int var1 = this.trackPositions.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            if (this.trackPositions[var2] >= 0) {
                return false;
            }
        }

        return true;
    }

    void reset(long var1) {
        this.field2477 = var1;
        int var3 = this.trackPositions.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            this.trackLengths[var4] = 0;
            this.field2474[var4] = 0;
            this.buffer.index = this.trackStarts[var4];
            this.readTrackLength(var4);
            this.trackPositions[var4] = this.buffer.index;
        }

    }
}
