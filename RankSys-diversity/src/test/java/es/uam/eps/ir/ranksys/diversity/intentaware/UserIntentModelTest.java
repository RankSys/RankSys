package es.uam.eps.ir.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import static es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser.ddp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.lp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.sp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.vp;

public class UserIntentModelTest {

    private static final double DELTA = 1e-8;

    private static final Long USER1_ID = 1L;
    private static final Long USER2_ID = 5L;

    private static final Long ITEM1_ID = 3L;
    private static final Long ITEM2_ID = 4L;

    private static IntentModel<Long, Long, String> intentModel;

    @BeforeClass
    public static void setUp() throws Exception {
        InputStream ratings = UserIntentModelTest.class.getResourceAsStream("/intent_ratings");
        InputStream features = UserIntentModelTest.class.getResourceAsStream("/intent_features");

        PreferenceData<Long, Long, Void> trainData = SimplePreferenceData.load(ratings, lp, lp, ddp, vp);
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(features, lp, sp, v -> 1.0);
        intentModel = new IntentModel<>(trainData.getUsersWithPreferences(), trainData, featureData);
    }

    @Test
    public void testGetIntentsUser1() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER1_ID);
        Set<String> intents = uim.getIntents();
        Assert.assertEquals(4, intents.size());
        Assert.assertTrue(intents.contains("A"));
        Assert.assertTrue(intents.contains("B"));
        Assert.assertTrue(intents.contains("C"));
        Assert.assertTrue(intents.contains("E"));
    }

    @Test
    public void testGetIntentsUser2() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER2_ID);
        Set<String> intents = uim.getIntents();
        Assert.assertEquals(5, intents.size());
        Assert.assertTrue(intents.contains("A"));
        Assert.assertTrue(intents.contains("B"));
        Assert.assertTrue(intents.contains("C"));
        Assert.assertTrue(intents.contains("D"));
        Assert.assertTrue(intents.contains("E"));
    }

    @Test
    public void testGetItemIntentsUser1Item1() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER1_ID);
        Set<String> itemIntents = uim.getItemIntents(ITEM1_ID).collect(Collectors.toSet());
        Assert.assertEquals(1, itemIntents.size());
        Assert.assertTrue(itemIntents.contains("A"));
    }

    @Test
    public void testGetItemIntentsUser1Item2() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER1_ID);
        Set<String> itemIntents = uim.getItemIntents(ITEM2_ID).collect(Collectors.toSet());
        Assert.assertEquals(1, itemIntents.size());
        Assert.assertTrue(itemIntents.contains("E"));
    }

    @Test
    public void testGetItemIntentsUser2Item1() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER2_ID);
        Set<String> itemIntents = uim.getItemIntents(ITEM1_ID).collect(Collectors.toSet());
        Assert.assertEquals(1, itemIntents.size());
        Assert.assertTrue(itemIntents.contains("A"));
    }

    @Test
    public void testGetItemIntentsUser2Item2() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER2_ID);
        Set<String> itemIntents = uim.getItemIntents(ITEM2_ID).collect(Collectors.toSet());
        Assert.assertEquals(2, itemIntents.size());
        Assert.assertTrue(itemIntents.contains("D"));
        Assert.assertTrue(itemIntents.contains("E"));
    }

    @Test
    public void testPUser1() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER1_ID);
        double intentProbabilityA = uim.p("A");
        double intentProbabilityB = uim.p("B");
        double intentProbabilityC = uim.p("C");
        double intentProbabilityD = uim.p("D");
        double intentProbabilityE = uim.p("E");

        Assert.assertEquals(0.33333333, intentProbabilityA, DELTA);
        Assert.assertEquals(0.33333333, intentProbabilityB, DELTA);
        Assert.assertEquals(0.16666667, intentProbabilityC, DELTA);
        Assert.assertEquals(0.0, intentProbabilityD, DELTA);
        Assert.assertEquals(0.16666667, intentProbabilityE, DELTA);
    }

    @Test
    public void testPUser2() throws Exception {
        IntentModel<Long, Long, String>.AbstractUserIntentModel uim = intentModel.getModel(USER2_ID);
        double intentProbabilityA = uim.p("A");
        double intentProbabilityB = uim.p("B");
        double intentProbabilityC = uim.p("C");
        double intentProbabilityD = uim.p("D");
        double intentProbabilityE = uim.p("E");

        Assert.assertEquals(0.25, intentProbabilityA, DELTA);
        Assert.assertEquals(0.25, intentProbabilityB, DELTA);
        Assert.assertEquals(0.125, intentProbabilityC, DELTA);
        Assert.assertEquals(0.125, intentProbabilityD, DELTA);
        Assert.assertEquals(0.25, intentProbabilityE, DELTA);
    }
}