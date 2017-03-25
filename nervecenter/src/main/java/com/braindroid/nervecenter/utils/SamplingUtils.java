/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.braindroid.nervecenter.utils;

import java.util.Arrays;
import java.util.Locale;

import timber.log.Timber;

public final class SamplingUtils {
    public static short[][] getExtremes(short[] data, int sampleSize) {
        short[][] newData = new short[sampleSize][];
        int groupSize = data.length / sampleSize;

        for (int i = 0; i < sampleSize; i++) {
            short[] group = Arrays.copyOfRange(data, i * groupSize,
                    Math.min((i + 1) * groupSize, data.length));

            // Fin min & max values
            short min = Short.MAX_VALUE, max = Short.MIN_VALUE;
            for (short a : group) {
                min = (short) Math.min(min, a);
                max = (short) Math.max(max, a);
            }
            newData[i] = new short[] { max, min };
        }

        return newData;
    }

    public static void printSample(short[] sample, int columns) {
        StringBuilder builder = new StringBuilder("");
        for(int i = 0; i < sample.length; i++) {
            builder.append(
                    String.format(Locale.ENGLISH, "%7d", sample[i])
            ).append(" ");
            if(i > 0 && i % columns == 0) {
                builder.append("\n");
            }
        }
        Timber.v("---- Short Data [%s] ----\n%s", sample, builder.toString());
    }
}
