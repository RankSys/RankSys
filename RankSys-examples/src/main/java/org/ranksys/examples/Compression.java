/*
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.examples;

import es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.lp;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import java.io.IOException;
import org.ranksys.compression.codecs.CODEC;
import org.ranksys.compression.codecs.dsi.FixedLengthBitStreamCODEC;
import org.ranksys.compression.codecs.lemire.IntegratedFORVBCODEC;
import org.ranksys.compression.preferences.RatingCODECPreferenceData;

/**
 * Example of usage of the RankSys-compression module.
 * 
 * For results of the RecSys 2015 poster please refer to
 * http://github.com/saulvargas/recsys2015
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class Compression {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String userPath = args[0];
        String itemPath = args[1];
        String dataPath = args[2];

        // READING USER, ITEM AND RATINGS FILES
        FastUserIndex<Long> users = SimpleFastUserIndex.load(userPath, lp);
        FastItemIndex<Long> items = SimpleFastItemIndex.load(itemPath, lp);
        FastPreferenceData<Long, Long> simpleData = SimpleFastPreferenceData.load(dataPath, lp, lp, DoubleParser.ddp, users, items);
        
        // CREATING A COMPRESSED PREFERENCE DATA
        CODEC<int[]> uCodec = new IntegratedFORVBCODEC();
        CODEC<int[]> iCodec = new IntegratedFORVBCODEC();
        // We assume here that the ratings are 1-5 stars
        CODEC<byte[]> vCodec = new FixedLengthBitStreamCODEC(3);
        FastPreferenceData<Long, Long> codecData = new RatingCODECPreferenceData<>(simpleData, users, items, uCodec, iCodec, vCodec);
        
        // PRINTING COMPRESSION STATISTICS
        System.out.println(uCodec.stats()[0] + "\t" + uCodec.stats()[1]);
        System.out.println(iCodec.stats()[0] + "\t" + iCodec.stats()[1]);
        System.out.println(vCodec.stats()[0] + "\t" + vCodec.stats()[1]);
        System.out.println(codecData.numPreferences());
    }

}
