/* 
 * Copyright (C) 2015 RankSys http://ranksys.github.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ranksys.compression.recsys2015;

import org.ranksys.compression.codecs.CODEC;
import org.ranksys.compression.codecs.NullCODEC;
import org.ranksys.compression.codecs.catena.GroupVByteCODEC;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.sp;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import org.ranksys.compression.codecs.dsi.FixedLengthBitStreamCODEC;
import org.ranksys.compression.codecs.dsi.GammaBitStreamCODEC;
import org.ranksys.compression.codecs.dsi.IntegratedEliasFanoBitStreamCODEC;
import org.ranksys.compression.codecs.dsi.RiceBitStreamCODEC;
import org.ranksys.compression.codecs.dsi.ZetaBitStreamCODEC;
import org.ranksys.compression.codecs.lemire.FORVBCODEC;
import org.ranksys.compression.codecs.lemire.FastPFORVBCODEC;
import org.ranksys.compression.codecs.lemire.IntegratedFORVBCODEC;
import org.ranksys.compression.codecs.lemire.IntegratedVByteCODEC;
import org.ranksys.compression.codecs.lemire.NewPFDVBCODEC;
import org.ranksys.compression.codecs.lemire.OptPFDVBCODEC;
import org.ranksys.compression.codecs.lemire.Simple16CODEC;
import org.ranksys.compression.codecs.lemire.VByteCODEC;
import org.ranksys.compression.preferences.BinaryCODECPreferenceData;
import org.ranksys.compression.preferences.RatingCODECPreferenceData;

/**
 * Common conventions for the test programs.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class Conventions {

    /**
     * Get path to serialized PreferenceData instances.
     *
     * @param path base path
     * @param dataset name of the dataset
     * @param idxCodec codec of identifiers
     * @param vCodec codec of ratings
     * @param reassignIdxs are ids re-assigned?
     * @return path of the preference data serialized file
     */
    public static String getPath(String path, String dataset, String idxCodec, String vCodec, boolean reassignIdxs) {
        return path + "/preference-data/" + idxCodec + "-" + vCodec + "-" + reassignIdxs + ".obj";
    }

    /**
     * Get the bits required for identifiers and ratings for identifiers
     * and ratings.
     *
     * @param path base path
     * @param dataset name of dataset
     * @return an array of number of bits for user identifiers, item identifiers and ratings
     * @throws IOException when IO error
     */
    public static int[] getFixedLength(String path, String dataset) throws IOException {
        FastUserIndex<String> users = SimpleFastUserIndex.load(path + "/users.txt", sp);
        FastItemIndex<String> items = SimpleFastItemIndex.load(path + "/items.txt", sp);

        int uFixedLength = 32 - Integer.numberOfLeadingZeros(users.numUsers() - 1);
        int iFixedLength = 32 - Integer.numberOfLeadingZeros(items.numItems() - 1);
        int vFixedLength;
        switch (dataset) {
            case "msd":
                vFixedLength = 1;
                break;
            case "ml20M":
                vFixedLength = 4;
                break;
            case "ml1M":
            case "netflix":
            case "ymusic":
                vFixedLength = 3;
                break;
            default:
                vFixedLength = 32;
        }

        return new int[]{uFixedLength, iFixedLength, vFixedLength};
    }

    /**
     * De-serialize preference data object.
     *
     * @param <U> type of user
     * @param <I> type of item
     * @param path path to file
     * @param dataset name of dataset
     * @param idxCodec codec of identifiers
     * @param vCodec codec of ratings
     * @param reassignIdxs are ids re-assigned?
     * @return preference data from file
     * @throws IOException when IO error
     * @throws ClassNotFoundException when reading bad file
     */
    public static <U, I> FastPreferenceData<U, I> deserialize(String path, String dataset, String idxCodec, String vCodec, boolean reassignIdxs) throws IOException, ClassNotFoundException {
        int[] lens = getFixedLength(path, dataset);
        String dataPath = getPath(path, dataset, idxCodec, vCodec, reassignIdxs);
        CODEC<?> u_codec = getCodec(idxCodec, lens[0]);
        CODEC<?> i_codec = getCodec(idxCodec, lens[1]);
        CODEC<?> v_codec = getCodec(vCodec, lens[2]);
        switch (dataset) {
            case "msd":
                return BinaryCODECPreferenceData.deserialize(dataPath, u_codec, i_codec);
            case "ml1M":
            case "ml20M":
            case "netflix":
            case "ymusic":
            default:
                return RatingCODECPreferenceData.deserialize(dataPath, u_codec, i_codec, v_codec);
        }
    }

    /**
     * Returns a codec by name.
     *
     * @param name name of the codec
     * @param fixedLength number of bits for fixed-length coding
     * @return codec
     */
    public static CODEC<?> getCodec(String name, int fixedLength) {
        int k = 3;
        if (name.contains("_")) {
            String[] tokens = name.split("_");
            name = tokens[0];
            k = parseInt(tokens[1]);
        }

        switch (name) {
            case "null":
                return new NullCODEC();
            case "gamma":
                return new GammaBitStreamCODEC();
            case "zeta":
                return new ZetaBitStreamCODEC(k);
            case "rice":
                return new RiceBitStreamCODEC();
            case "vbyte":
                return new VByteCODEC();
            case "ivbyte":
                return new IntegratedVByteCODEC();
            case "for":
                return new FORVBCODEC();
            case "ifor":
                return new IntegratedFORVBCODEC();
            case "simple":
                return new Simple16CODEC();
            case "optpfd":
                return new OptPFDVBCODEC();
            case "newpfd":
                return new NewPFDVBCODEC();
            case "fastpfor":
                return new FastPFORVBCODEC();
            case "succint":
                return new IntegratedEliasFanoBitStreamCODEC();
            case "fixed":
                return new FixedLengthBitStreamCODEC(fixedLength);
            case "gvbyte":
                return new GroupVByteCODEC();
            default:
                System.err.println("I don't know what " + name + " is :-(");
                System.exit(-1);
                return null;
        }
    }

}
