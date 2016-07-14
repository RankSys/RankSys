package es.uam.eps.ir.ranksys.nn.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;

import static java.lang.Math.pow;

public class Similarities {

    public static AbstractSimilarity ratingJaccard(FastPreferenceData<?, ?> preferences, boolean dense) {
        return new AbstractSimilarity(preferences, dense) {
            @Override
            protected double sim(double product, double norm2A, double norm2B, int intersectionSize, int sizeA, int sizeB) {
                return product / (norm2A + norm2B - product);
            }
        };
    }

    public static AbstractSimilarity ratingCosine(FastPreferenceData<?, ?> preferences, boolean dense, double alpha) {
        return new AbstractSimilarity(preferences, dense) {
            @Override
            protected double sim(double product, double norm2A, double norm2B, int intersectionSize, int sizeA, int sizeB) {
                return product / (pow(norm2A, alpha) * pow(norm2B, 1.0 - alpha));
            }
        };
    }

    public static AbstractSimilarity binaryJaccard(FastPreferenceData<?, ?> preferences, boolean dense) {
        return new AbstractSimilarity(preferences, dense) {
            @Override
            protected double sim(double product, double norm2A, double norm2B, int intersectionSize, int sizeA, int sizeB) {
                return intersectionSize / (double) (sizeA + sizeB - intersectionSize);
            }
        };
    }

    public static AbstractSimilarity binaryCosine(FastPreferenceData<?, ?> preferences, boolean dense, double alpha) {
        return new AbstractSimilarity(preferences, dense) {
            @Override
            protected double sim(double product, double norm2A, double norm2B, int intersectionSize, int sizeA, int sizeB) {
                return intersectionSize / (pow(sizeA, alpha) * pow(sizeB, 1.0 - alpha));
            }
        };
    }

    public static AbstractSimilarity significanceWeighting(AbstractSimilarity similarity, int minIntersectionSize) {
        return new AbstractSimilarity(similarity.data, similarity.dense) {
            @Override
            protected double sim(double product, double norm2A, double norm2B, int intersectionSize, int sizeA, int sizeB) {
                double sim = similarity.sim(product, norm2A, norm2B, intersectionSize, sizeA, sizeB);
                if (intersectionSize < minIntersectionSize) {
                    return sim * Math.min(1.0, intersectionSize / (double) minIntersectionSize);
                } else {
                    return sim;
                }
            }
        };
    }
}
