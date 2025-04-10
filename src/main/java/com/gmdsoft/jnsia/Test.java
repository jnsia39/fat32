//package com.gmdsodt.jskim;
//
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.channels.FileChannel;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Test {
//    public boolean bootRecordTest(FileChannel fileChannel) {
//        try {
//            BootRecord bootRecord = new BootRecord();
//            bootRecord.analyze(fileChannel);
//
//            System.out.println("--- Boot Record Analysis Result ---");
//            System.out.println(bootRecord);
//            System.out.println();
//        } catch (Exception ex) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public FATArea fatAreaTest() {
//        String testHexString = """
//        F8 FF FF 0F FF FF FF FF FF FF FF 0F FF FF FF 0F
//        FF FF FF 0F FF FF FF 0F FF FF FF 0F 08 00 00 00
//        09 00 00 00 0A 00 00 00 0B 00 00 00 0C 00 00 00
//        0D 00 00 00 0E 00 00 00 0F 00 00 00 10 00 00 00
//        11 00 00 00 12 00 00 00 13 00 00 00 14 00 00 00
//        15 00 00 00 16 00 00 00 17 00 00 00 18 00 00 00
//        19 00 00 00 1A 00 00 00 1B 00 00 00 1C 00 00 00
//        1D 00 00 00 1E 00 00 00 1F 00 00 00 20 00 00 00
//        21 00 00 00 22 00 00 00 23 00 00 00 24 00 00 00
//        25 00 00 00 26 00 00 00 27 00 00 00 28 00 00 00
//        29 00 00 00 2A 00 00 00 2B 00 00 00 2C 00 00 00
//        2D 00 00 00 2E 00 00 00 2F 00 00 00 30 00 00 00
//        31 00 00 00 32 00 00 00 33 00 00 00 34 00 00 00
//        35 00 00 00 36 00 00 00 37 00 00 00 38 00 00 00
//        39 00 00 00 3A 00 00 00 3B 00 00 00 3C 00 00 00
//        3D 00 00 00 3E 00 00 00 3F 00 00 00 40 00 00 00
//        41 00 00 00 42 00 00 00 43 00 00 00 44 00 00 00
//        45 00 00 00 46 00 00 00 47 00 00 00 48 00 00 00
//        49 00 00 00 4A 00 00 00 4B 00 00 00 4C 00 00 00
//        4D 00 00 00 4E 00 00 00 4F 00 00 00 50 00 00 00
//        51 00 00 00 52 00 00 00 53 00 00 00 54 00 00 00
//        55 00 00 00 56 00 00 00 57 00 00 00 58 00 00 00
//        59 00 00 00 5A 00 00 00 5B 00 00 00 5C 00 00 00
//        5D 00 00 00 5E 00 00 00 5F 00 00 00 60 00 00 00
//        61 00 00 00 62 00 00 00 63 00 00 00 64 00 00 00
//        65 00 00 00 66 00 00 00 67 00 00 00 68 00 00 00
//        69 00 00 00 6A 00 00 00 6B 00 00 00 6C 00 00 00
//        6D 00 00 00 6E 00 00 00 6F 00 00 00 70 00 00 00
//        71 00 00 00 72 00 00 00 73 00 00 00 74 00 00 00
//        75 00 00 00 76 00 00 00 77 00 00 00 78 00 00 00
//        79 00 00 00 7A 00 00 00 7B 00 00 00 7C 00 00 00
//        7D 00 00 00 7E 00 00 00 7F 00 00 00 80 00 00 00
//        81 00 00 00 82 00 00 00 83 00 00 00 84 00 00 00
//        85 00 00 00 86 00 00 00 87 00 00 00 88 00 00 00
//        89 00 00 00 8A 00 00 00 8B 00 00 00 8C 00 00 00
//        8D 00 00 00 8E 00 00 00 8F 00 00 00 90 00 00 00
//        91 00 00 00 92 00 00 00 93 00 00 00 94 00 00 00
//        95 00 00 00 96 00 00 00 FF FF FF 0F 98 00 00 00
//        99 00 00 00 9A 00 00 00 9B 00 00 00 9C 00 00 00
//        9D 00 00 00 9E 00 00 00 9F 00 00 00 A0 00 00 00
//        A1 00 00 00 A2 00 00 00 A3 00 00 00 A4 00 00 00
//        A5 00 00 00 A6 00 00 00 A7 00 00 00 A8 00 00 00
//        A9 00 00 00 AA 00 00 00 AB 00 00 00 AC 00 00 00
//        AD 00 00 00 AE 00 00 00 AF 00 00 00 B0 00 00 00
//        B1 00 00 00 B2 00 00 00 B3 00 00 00 B4 00 00 00
//        B5 00 00 00 B6 00 00 00 B7 00 00 00 B8 00 00 00
//        B9 00 00 00 BA 00 00 00 BB 00 00 00 BC 00 00 00
//        BD 00 00 00 BE 00 00 00 BF 00 00 00 C0 00 00 00
//        C1 00 00 00 C2 00 00 00 C3 00 00 00 C4 00 00 00
//        C5 00 00 00 C6 00 00 00 C7 00 00 00 C8 00 00 00
//        C9 00 00 00 CA 00 00 00 CB 00 00 00 CC 00 00 00
//        CD 00 00 00 CE 00 00 00 CF 00 00 00 D0 00 00 00
//        D1 00 00 00 D2 00 00 00 D3 00 00 00 D4 00 00 00
//        D5 00 00 00 D6 00 00 00 D7 00 00 00 D8 00 00 00
//        D9 00 00 00 DA 00 00 00 DB 00 00 00 DC 00 00 00
//        DD 00 00 00 DE 00 00 00 DF 00 00 00 E0 00 00 00
//        E1 00 00 00 E2 00 00 00 E3 00 00 00 E4 00 00 00
//        E5 00 00 00 E6 00 00 00 E7 00 00 00 E8 00 00 00
//        E9 00 00 00 EA 00 00 00 EB 00 00 00 EC 00 00 00
//        ED 00 00 00 EE 00 00 00 EF 00 00 00 F0 00 00 00
//        F1 00 00 00 F2 00 00 00 F3 00 00 00 F4 00 00 00
//        F5 00 00 00 F6 00 00 00 F7 00 00 00 F8 00 00 00
//        F9 00 00 00 FA 00 00 00 FB 00 00 00 FC 00 00 00
//        FD 00 00 00 FE 00 00 00 FF 00 00 00 00 01 00 00
//        FF FF FF 0F FF FF FF 0F 00 00 00 00 00 00 00 00
//        """;
//
//        byte[] data = hexStringToByteArray(testHexString);
//        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//
//        FATArea fatArea = new FATArea();
//        if (!fatArea.makeFatChain(byteBuffer))
//            return null;
//
//        System.out.println("--- FAT Area Analysis Result ---");
//        System.out.println(fatArea);
//        System.out.println();
//
//        return fatArea;
//    }
//
//    public boolean fatChainTest(FATArea fatArea) {
//        System.out.println("--- FAT Chain Analysis Result ---");
//        System.out.println("Cluster No.7 FAT Chain: " + fatArea.getClusters(0x7).toString().substring(0, 200) + ", ...]");
//        System.out.println();
//
//        return true;
//    }
//
//    public boolean dirEntryTest() {
//        String testHexString = """
//        44 49 52 31 20 20 20 20 20 20 20 10 08 7F C9 71
//        A3 4C A3 4C 00 00 91 71 A3 4C 06 00 00 00 00 00
//        """;
//
//        try {
//            byte[] data = hexStringToByteArray(testHexString);
//            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//
//            DirEntry dirEntry = new DirEntry();
//            dirEntry.analyze(byteBuffer);
//
//            System.out.println("--- Directory Entry Analysis Result ---");
//            System.out.println(dirEntry);
//            System.out.println();
//        } catch (Exception ex) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public boolean scatteredStreamTest() {
//        try (FileChannel file = new RandomAccessFile("test.txt", "r").getChannel()) {
//            ;List<Extent> extents = new ArrayList<>();
//
//            extents.add(new Extent(0, 40));
//            extents.add(new Extent(130, 20));
//            extents.add(new Extent(100, 10));
//
//            ScatteredStream stream = new ScatteredStream(extents, file);
//            stream.position(30);
//            ByteBuffer bb = stream.read(40);
//
//            byte[] bytes = new byte[bb.position()];
//            bb.flip();
//            bb.get(bytes);
//
//            String s = new String(bytes);
//            System.out.println(s);
//
//            return true;
//        } catch (IOException ex) {
//            return false;
//        }
//    }
//
//    private byte[] hexStringToByteArray(String hexString) {
//        String cleanedHex = hexString.replaceAll("[^0-9A-Fa-f]", "");
//        int len = cleanedHex.length();
//        byte[] data = new byte[len / 2];
//
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(cleanedHex.charAt(i), 16) << 4)
//                    + Character.digit(cleanedHex.charAt(i + 1), 16));
//        }
//
//        return data;
//    }
//}
