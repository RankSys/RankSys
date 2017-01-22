package es.uam.eps.ir.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import org.junit.Assert;
import org.junit.Test;
import org.ranksys.core.util.tuples.Tuples;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.jooq.lambda.tuple.Tuple.tuple;

public class AveragePrecisionTest {

    private final PreferenceData<String, String> prefs = SimplePreferenceData.load(Stream.of(
            tuple("a", "A", 1.0),
            tuple("a", "B", 1.0),
            tuple("a", "C", 1.0),
            tuple("a", "D", 1.0),
            tuple("b", "A", 1.0)
    ));

    private final RecommendationMetric<String, String> ap = new AveragePrecision<>(5, new BinaryRelevanceModel<>(false, prefs, 1.0));

    @Test
    public void testA() {
        Recommendation<String, String> recA = new Recommendation<>("a", Arrays.asList(
                Tuples.tuple("A", 9.0),
                Tuples.tuple("L", 8.0),
                Tuples.tuple("B", 7.0),
                Tuples.tuple("N", 6.0),
                Tuples.tuple("O", 5.0),
                Tuples.tuple("P", 4.0),
                Tuples.tuple("Q", 3.0),
                Tuples.tuple("L", 2.0),
                Tuples.tuple("C", 1.0),
                Tuples.tuple("R", 0.0)
        ));

        Assert.assertEquals(5.0 / 12.0, ap.evaluate(recA), 1e-12);
    }

    @Test
    public void testB() {
        Recommendation<String, String> recB = new Recommendation<>("b", Arrays.asList(
                Tuples.tuple("L", 8.0),
                Tuples.tuple("M", 7.0),
                Tuples.tuple("N", 6.0),
                Tuples.tuple("O", 5.0),
                Tuples.tuple("P", 4.0),
                Tuples.tuple("Q", 3.0),
                Tuples.tuple("B", 2.0),
                Tuples.tuple("C", 1.0),
                Tuples.tuple("R", 0.0)
        ));

        Assert.assertEquals(0.0, ap.evaluate(recB), 0.0);
    }

    @Test
    public void testC() {
        Recommendation<String, String> recC = new Recommendation<>("c", Arrays.asList(
                Tuples.tuple("A", 9.0),
                Tuples.tuple("L", 8.0),
                Tuples.tuple("M", 7.0),
                Tuples.tuple("N", 6.0),
                Tuples.tuple("O", 5.0),
                Tuples.tuple("P", 4.0),
                Tuples.tuple("Q", 3.0),
                Tuples.tuple("B", 2.0),
                Tuples.tuple("C", 1.0),
                Tuples.tuple("R", 0.0)
        ));

        Assert.assertEquals(0.0, ap.evaluate(recC), 0.0);
    }
}
