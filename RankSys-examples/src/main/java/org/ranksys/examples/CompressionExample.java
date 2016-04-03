/*
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.examples;

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
import org.ranksys.formats.index.ItemsReader;
import org.ranksys.formats.index.UsersReader;
import static org.ranksys.formats.parsing.Parsers.lp;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;

/**
 * Example of usage of the RankSys-compression module.
 * <br>
 * If you use this code, please cite the following papers:
 * <ul>
 * <li>Vargas, S., Macdonald, C., Ounis, I. (2015). Analysing Compression Techniques for In-Memory Collaborative Filtering. In Poster Proceedings of the 9th ACM Conference on Recommender Systems. <a href="http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf">http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf</a>.</li>
 * <li>Catena, M., Macdonald, C., Ounis, I. (2014). On Inverted Index Compression for Search Engine Efficiency. In ECIR (pp. 359–371). doi:10.1007/978-3-319-06028-6_30</li>
 * </ul>
 * The code that reproduces the results of the RecSys 2015 poster by Vargas et al. in a separated project: <a href="http://github.com/saulvargas/recsys2015">http://github.com/saulvargas/recsys2015</a>
 * <br>
 * The search index compression technologies of the ECIR paper by Catena et al. is part of the Terrier IR Platform: <a href="http://terrier.org/docs/v4.0/compression.html">http://terrier.org/docs/v4.0/compression.html</a>.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class CompressionExample {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String userPath = args[0];
        String itemPath = args[1];
        String dataPath = args[2];

        // READING USER, ITEM AND RATINGS FILES
        FastUserIndex<Long> users = SimpleFastUserIndex.load(UsersReader.read(userPath, lp));
        FastItemIndex<Long> items = SimpleFastItemIndex.load(ItemsReader.read(itemPath, lp));
        FastPreferenceData<Long, Long> simpleData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(dataPath, lp, lp), users, items);

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
