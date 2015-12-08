/*
 * Copyright 2015 Laukviks Bedrifter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laukvik.db.csv.io;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public enum BOM {

    UTF8((byte) 239, (byte) 187, (byte) 191),
    UTF16BE((byte) 254, (byte) 255),
    UTF16LE((byte) 255, (byte) 254),
    UTF32BE((byte) 0, (byte) 0, (byte) 254, (byte) 255),
    UTF32LE((byte) 0, (byte) 0, (byte) 255, (byte) 254);

    private final byte[] bytes;

    private BOM(byte... chars) {
        this.bytes = chars;
    }

    public static BOM parse(byte... bytes) {
        for (BOM bom : values()) {
//            System.out.println("Checking: " + bom);
            boolean isMatching = true;
            for (int x = 0; x < bom.bytes.length; x++) {
//                System.out.println(x + " : " + bom.bytes[x] + " " + bytes.length + " " + bom.bytes.length);
                byte b1 = bom.bytes[x];
                if (x < bytes.length) {
                    byte b2 = bytes[x];
//                    System.out.println(b1 + " = " + b2);
                    if (b1 != b2) {
                        isMatching = false;
                    }
                } else {
                    isMatching = false;
                }
            }
            if (isMatching) {
                return bom;
            }
        }
        return null;
    }

}
