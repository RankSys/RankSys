/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware

import es.uam.eps.ir.ranksys.core.feature.FeatureData
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData
import es.uam.eps.ir.ranksys.core.preference.PreferenceData
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData
import es.uam.eps.ir.ranksys.core.util.FastStringSplitter
import org.ranksys.formats.feature.SimpleFeaturesReader
import static org.ranksys.formats.parsing.Parsers.lp
import static org.ranksys.formats.parsing.Parsers.sp
import org.ranksys.formats.preference.SimpleRatingPreferencesReader
import org.jooq.lambda.tuple.Tuple
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

import static spock.util.matcher.HamcrestSupport.that
import static spock.util.matcher.HamcrestMatchers.closeTo

class UserIntentModelSpec extends Specification {

    private static final double DELTA = 1e-8;

    private IntentModel<Long, Long, String> intentModel;

    void setup() {
        InputStream ratings = UserIntentModelSpec.class.getResourceAsStream("/intent_ratings");
        InputStream features = UserIntentModelSpec.class.getResourceAsStream("/intent_features");

        PreferenceData trainData = SimplePreferenceData.load(SimpleRatingPreferencesReader.get().read(ratings, lp, lp));
        
        FeatureData featureData = SimpleFeatureData.load(SimpleFeaturesReader.get().read(features, lp, sp));
        
        intentModel = new FeatureIntentModel(trainData.getUsersWithPreferences(), trainData, featureData);
    }

    def "check number of user's intents"() {
        when:
        IntentModel.UserIntentModel uim = intentModel.getModel(userId);
        Set intents = uim.getIntents();

        then:
        intents.size() == nIntents

        where:
        userId || nIntents
        1L     || 4
        5L     || 5
    }

    def "check user's intents"() {
        when:
        IntentModel.UserIntentModel uim = intentModel.getModel(userId);
        Set intents = uim.getIntents();

        then:
        intents.contains(intent) == contains

        where:
        userId | intent || contains
        1L     | "A"    || true
        1L     | "B"    || true
        1L     | "C"    || true
        1L     | "D"    || false
        1L     | "E"    || true
        5L     | "A"    || true
        5L     | "B"    || true
        5L     | "C"    || true
        5L     | "D"    || true
        5L     | "E"    || true
    }

    def "check user's item intents"() {
        when:
        IntentModel.UserIntentModel uim = intentModel.getModel(userId);
        Set itemIntents = uim.getItemIntents(itemId).collect(Collectors.toSet());

        then:
        itemIntents.size() == nIntents

        then:
        itemIntents.contains(intent) == contains

        where:
        userId | itemId | nIntents | intent | contains
        1L     | 3L     | 1        | "A"    | true
        1L     | 4L     | 1        | "E"    | true
        5L     | 3L     | 1        | "A"    | true
        5L     | 4L     | 2        | "D"    | true
        5L     | 4L     | 2        | "E"    | true
    }

    def "check user's intent probabilities"() {
        when:
        IntentModel.UserIntentModel uim = intentModel.getModel(userId);

        then:
        that uim.pf_u(intent), closeTo(p, DELTA)

        where:
        userId | intent || p
        1L     | "A"    || 0.33333333
        1L     | "B"    || 0.33333333
        1L     | "C"    || 0.16666667
        1L     | "D"    || 0.0
        1L     | "E"    || 0.16666667
        5L     | "A"    || 0.25
        5L     | "B"    || 0.25
        5L     | "C"    || 0.125
        5L     | "D"    || 0.125
        5L     | "E"    || 0.25
    }
}
