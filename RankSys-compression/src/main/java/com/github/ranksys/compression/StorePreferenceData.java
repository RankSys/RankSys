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
package com.github.ranksys.compression;

import com.github.ranksys.compression.codecs.CODEC;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.ip;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.sp;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import static java.lang.Boolean.parseBoolean;
import static com.github.ranksys.compression.Conventions.getCodec;
import static com.github.ranksys.compression.Conventions.getFixedLength;
import static com.github.ranksys.compression.Conventions.getPath;
import com.github.ranksys.compression.preferences.BinaryCODECPreferenceData;
import com.github.ranksys.compression.preferences.RatingCODECPreferenceData;
import java.util.function.Function;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class StorePreferenceData {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) {
            args = new String[]{"/home/saul/recsys/ml20M/", "ml20M", "ifor", "fixed", "false"};
        }
        String path = args[0];
        String dataset = args[1];
        String idxCodec = args[2];
        String vCodec = args[3];
        boolean idxsByPop = parseBoolean(args[4]);

        if (idxCodec.equals("ranksys")) {
            System.err.println("does not apply");
            return;
        }
        if (dataset.equals("msd") && !vCodec.equals("null")) {
            System.err.println("does not apply here: implicit data");
            return;
        }
        if (vCodec.startsWith("i")) {
            System.err.println("integrated codec only for ids");
            return;
        }

        switch (dataset) {
            case "ml1M":
            case "ml20M":
            case "netflix":
            case "ymusic":
                store(path, dataset, idxCodec, vCodec, idxsByPop, ip, ip);
                break;
            case "msd":
            default:
                store(path, dataset, idxCodec, vCodec, idxsByPop, sp, sp);
                break;
        }
    }

    private static <U, I> void store(String path, String dataset, String idxCodec, String vCodec, boolean reassignIdxs, Parser<U> up, Parser<I> ip) throws IOException {

        FastUserIndex<U> users;
        FastItemIndex<I> items;
        if (reassignIdxs) {
            users = SimpleFastUserIndex.load(path + "users.txt.r", up, false);
            items = SimpleFastItemIndex.load(path + "items.txt.r", ip, false);
        } else {
            users = SimpleFastUserIndex.load(path + "users.txt", up, true);
            items = SimpleFastItemIndex.load(path + "items.txt", ip, true);
        }

        String uDataPath;
        String iDataPath;
        if (reassignIdxs) {
            uDataPath = path + "total.u.r";
            iDataPath = path + "total.i.r";
        } else {
            uDataPath = path + "total.u";
            iDataPath = path + "total.i";
        }

        Function<CODEC<?>[], FastPreferenceData<U, I>> cdf = cds -> {
            try {
                switch (dataset) {
                    case "msd":
                        return BinaryCODECPreferenceData.load(uDataPath, iDataPath, users, items, cds[0], cds[1]);
                    case "ml1M":
                    case "ml20M":
                    case "netflix":
                    case "ymusic":
                    default:
                        return RatingCODECPreferenceData.load(uDataPath, iDataPath, users, items, cds[0], cds[1], cds[2]);
                }

            } catch (FileNotFoundException ex) {
                throw new UncheckedIOException(ex);
            }
        };

        long time0 = System.nanoTime();
        int[] lens = getFixedLength(path, dataset);
        CODEC cd_uidxs = getCodec(idxCodec, lens[0]);
        CODEC cd_iidxs = getCodec(idxCodec, lens[1]);
        CODEC cd_vs = getCodec(vCodec, lens[2]);
        FastPreferenceData<U, I> preferences = cdf.apply(new CODEC[]{cd_uidxs, cd_iidxs, cd_vs});
        double loadingTime = (System.nanoTime() - time0) / 1_000_000_000.0;
        System.err.println("loaded " + dataset + " with " + idxCodec + "+" + vCodec + ": " + loadingTime);

        String fields = dataset + "\t" + idxCodec + "\t" + vCodec + "\t" + reassignIdxs;
        System.out.println(fields + "\tus\t" + cd_uidxs.stats()[1]);
        System.out.println(fields + "\tis\t" + cd_iidxs.stats()[1]);
        System.out.println(fields + "\tvs\t" + cd_vs.stats()[1]);

        if (preferences instanceof RatingCODECPreferenceData) {
            ((RatingCODECPreferenceData) preferences).serialize(getPath(path, dataset, idxCodec, vCodec, reassignIdxs));
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }

}
