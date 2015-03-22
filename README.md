# RankSys: Java 8 Recommender Systems framework for novelty, diversity and much more

## Authors
 * [Saúl Vargas](http://www.saulvargas.es)
 * [Pablo Castells](http://ir.ii.uam.es/castells/)

## References

If you publish research that uses RankSys, please cite the papers in the following list that best match the parts of the framework that you used:
 * S. Vargas. Novelty and Diversity Evaluation and Enhancement in Recommender Systems. PhD Thesis. Universidad Autónoma de Madrid, Spain, February 2015.
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, Redundancy and Size-Awareness in Genre Diversity for Recommender Systems. 8th ACM Conference on Recommender Systems (RecSys 2014). Foster City, CA, USA, October 2014, pp. 209-216.
 * S. Vargas, P. Castells, D. Vallet. Explicit Relevance Models in Intent-Oriented Information Retrieval Diversification. 35th International ACM SIGIR Conference on Research and Development in Information Retrieval (SIGIR 2012). Portland, OR, USA, August 2012, pp. 75-84.
 * S. Vargas, P. Castells. Rank and Relevance in Novelty and Diversity Metrics for Recommender Systems. 5th ACM Conference on Recommender Systems (RecSys 2011). Chicago, Illinois, October 2011, pp. 109-116.
 * S. Vargas, P. Castells, D. Vallet. Intent-oriented Diversity in Recommender Systems. 34th international ACM SIGIR conference on Research and development in Information Retrieval (SIGIR 2012). Beijing, PR China, pp. 1211-1212.

## Introduction

RankSys is a new framework for the implementation and evaluation of recommendation algorithms and techniques that has resulted from a line of research work that is currently documented in several publications (see above) and a PhD thesis (["Novelty and Diversity Evaluation and Enhancement in Recommender Systems"](http://ir.ii.uam.es/saul/saulvargas-thesis.pdf)).  While it is envisioned as a framework for the generic experimentation of recommendation technologies, it includes substantial support focusing on the evaluation and enhancement of novelty and diversity. RankSys derives its name from explicitly targeting the ranking task problem, rather than rating prediction. This decision is reflected in the design of the different core interfaces and components of the framework.

The framework has been programmed with Java 8, which is the most recent version of the popular programming language. We take advantage of many of the new features of the language, such as the use of lambda functions, `Stream`'s and facilities for automatic parallelization of the code. The code licensed under the GPL V3, which allows the free use, study, distribution and modification of the software as long as derived works are distributed under the same license.

To date, the publicly available version of this framework includes the modules that implement novelty and diversity metrics and re-ranking techniques and the required core components of the framework:
 * RankSys-core, which contains the common and auxiliary classes of the framework.
 * RankSys-metrics, which contains the interfaces and common components for defining metrics.
 * RankSys-diversity, which contains the novelty and diversity metrics and re-ranking strategies.
 * RankSys-examples, which provides examples of usage of the previous modules.
 
In the rest of the appendix, we provide a high-level description of the different components of the current release of the software.

## Input Data

In the current version of RankSys we consider two types of input data: interactions between users and items - in the form of ratings, play counts, etc. - and feature information about the items - genres, language, etc. In both cases, the data can be interpreted as pairs of entities - user-item pairs and item-feature pairs, respectively - for which we may have some additional information - such as ratings, play counts or weights. In this section, we provide a description of the interfaces and classes employed to represent the input data of our recommendation platform that are part of the RankSys-core module.

As identified in the previous paragraph, our framework considers three different sets of entities, namely users, items and features, whose pairs define the input data of our recommendation algorithms. For each of these entities, we consider an index-like interface that allows us to keep track of its members. For example, the set of users of our system is accessed by means of classes that implement the following interface `UserIndex`:
~~~
public interface UserIndex<U> {
    public boolean containsUser(U u);
    public int numUsers();
    public Stream<U> getAllUsers();
}
~~~
Analogous interfaces have been defined for the sets of items (`ItemIndex`) and features (`FeatureIndex`). In the current version, we do not provide direct implementations of these interfaces, but extend them in our input data interfaces as we describe next.

The interaction data between users and items, which is the main information used in collaborative filtering algorithms, is handled by means of classes implementing the interface `RecommenderData`, which extends the `UserIndex` and `ItemIndex` interfaces and adds methods to access the information regarding the interactions between users and items:
~~~
public interface RecommenderData<U, I, V> extends UserIndex<U>,
 ItemIndex<I> {
    public int numUsers(I i);
    public int numItems(U u);
    public int numPreferences();
    public Stream<IdValuePair<I, V>> getUserPreferences(U u);
    public Stream<IdValuePair<U, V>> getItemPreferences(I i);
}
~~~
Note that the type of feedback is left as a generic type `V`, which allows to consider every type of possible feedback data such as ratings, play counts or series of timestamps. Implementations of this `RecommenderData` can be backed by in-memory structures or by a database. We provide a simple `SimpleRecommenderData` class that implements this interface by simply storing the user-item pairs in two different hash tables indexed by user and item, respectively.

The information about item features, which can be used by content-based recommendation algorithms or our novelty and diversity metrics and re-ranking techniques, is managed in our framework by an interface `FeatureData` similar to `RecommenderData`, which is defined as follows:
~~~
public interface FeatureData<I, F, V> extends ItemIndex<I>,
 FeatureIndex<F> {
    Stream<IdValuePair<I, V>> getFeatureItems(final F f);
    Stream<IdValuePair<F, V>> getItemFeatures(final I i);
    int numFeatures(I i);
    int numItems(F f);
}
~~~
Analogously to the user-item data, we provide a hash table-backed implementation of this interface in the class `SimpleFeatureData`.

## Recommendations

As stated in the introduction, our framework considers ranked lists of items as the natural output of recommendation algorithms. In particular, we require the order or recommendation lists to be determined by decreasing order of a scoring function. We therefore define the class `Recommendation` that encapsulates the information about the user that receives the recommendation and a list of item-score pairs that compose the recommendation:
~~~
public class Recommendation<U, I> {
    public Recommendation(U user, List<IdDoublePair<I>> items) {...}
    public U getUser() {...}
    public List<IdDoublePair<I>> getItems() {...}
}
~~~

In our experiments, recommendations can be conveniently stored in files for later access of metrics that evaluate their accuracy, novelty or diversity. For that purpose, we include a `RecommendationFormat` interface, the implementations of which specify the format in which recommendations are written in and read from files:
~~~
public interface RecommendationFormat<U, I> {
    ...
    public Writer<U, I> getWriter(OutputStream out) throws IOException;
    public interface Writer<U, I> extends Closeable {
        public void write(Recommendation<U, I> recommendation) throws
         IOException;
    }
    ...
    public Reader<U, I> getReader(InputStream in) throws IOException;
    public interface Reader<U, I> {
        public Stream<Recommendation<U, I>> readAll() throws
         IOException;
    }
}
~~~
As it can be observed, the `RecommendationFormat` interface is in turn composed of two different interfaces for reading and writing that have to be reciprocal, that is, the first needs to be able to read the format used by the second. We provide a `SimpleRecommendationFormat` class that implements this interface by printing in plain text files sets of recommendations as user-item-score triplets sorted by decreasing score for each user.

## Metrics

The common infrastructure for metrics is defined in the RankSys-metrics module. It consists of two different interfaces for metrics and some common components for defining rank and relevance-awareness in metrics.

We distinguish two types of metrics that evaluate the output of recommendation algorithms: user-side metrics that evaluate the ability of a particular recommendation to satisfy the needs of the user that receives it, and business or system-side metrics that evaluate the overall effectiveness of a set of recommendations issued to a community of users.
For user-side metrics, implementations simply have to comply with the following `RecommendationMetric` interface:
~~~
public interface RecommendationMetric<U, I> {
    public double evaluate(Recommendation<U, I> recommendation);
}
~~~
This simple interface provides a numerical value for each recommendation. Its implementations are expected to be immutable, i.e. calculating the result of a recommendation does not change the internal state of the instance of the class. This allows instances of `RecommendationMetric` to be called concurrently. As examples, we include in RankSys-metrics implementations of two widely used accuracy metrics for ranking tasks, namely precision and nDCG, which are implemented in the classes `Precision` and `NDCG`, respectively.
System-side metrics have to implement the following, mutable `SystemMetric` interface:
~~~
public interface SystemMetric<U, I> {
    public void add(Recommendation<U, I> recommendation);
    public void combine(SystemMetric<U, I> other);
    public double evaluate();
	public void reset();
}
~~~
This interface is considerably different to the `RecommendationMetric` interface and has been designed so that the value of the metric can be computed by means of a [*mutable reduction*](http://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html\#MutableReduction) of the recommendations provided, thus allowing the calculation of a metric for a system in a parallel fashion. As an example, we provide in RankSys-metrics a `AverageRecommendationMetric` class that calculates the average value across users of any instance of `RecommendationMetric`.

Additionally, the module RankSys-metrics provides generic models to take into account the ranking and relevance of the items in recommendations. The ranking model defined in the interface `RankingDiscountModel` defines a discount function based on the rank of an item in a recommendation:
~~~
public interface RankingDiscountModel {
    public double disc(int k);
}
~~~
Implementations of this interface can be plugged into metrics to consider different ranking discounts. We provide four different classes that implement this interface: `NoDiscountModel` for ignoring any rank position discount, `LogarithmicDiscountModel` as in nDCG, `ExponentialDiscountModel` as in RBP and `ReciprocalDiscount` as in ERR.
The relevance model considers the perception of the users about the relevance of the recommended items. It is defined in the abstract class `RelevanceModel`, which extends an auxiliary `PersonalizableModel` class that allows the caching of the resulting user relevance models:
~~~
public abstract class RelevanceModel<U, I> extends
 PersonalizableModel<U> {
    ...
    protected abstract UserRelevanceModel<U, I> get(U user);
    ...
    public interface UserRelevanceModel<U, I> extends UserModel<U> {
        public boolean isRelevant(I item);
        public double gain(I item);
    }
}
~~~
As it can be observed, the abstract class `RelevanceModel` defines an interface `UserRelevanceModel` that has two methods to determine whether an item is found relevant to a user and the gain that is obtained when recommending it. An extension to this interface is defined in `IdealRelevanceModel` to retrieve the set of all items that the user finds relevant, which is useful in metrics that are normalized by the maximum possible score, such as nDCG. We include in this module two instantiable relevance models: `NoRelevanceModel` to ignore the relevance of items and `BinRelevanceModel` which applies a threshold on a test partition of the user-item interaction data to determine the relevance of the recommended items. Some of the metrics provided in this module or the novelty and diversity metrics in RankSys-diversity may define their own relevance models depending on the definition of the metric they are implementing.

## Novelty and Diversity

The module RankSys-diversity contains the implementation of an array of novelty and diversity metrics and re-ranking strategies. The metrics implement the interfaces defined in the module RankSys-metric, while the re-ranking methods share a common set of interfaces and classes defined in this module. In this section, we provide a description of the common re-ranking interfaces and classes and an overview of the different novelty and diversity models grouped in the different packages of the module.

### Re-ranking Strategies

The novelty and diversity enhancement algorithms take the form of a re-ranking operations on the output of baseline recommendations. Two types of re-ranking are supported: one that is the result of a direct re-scoring of the scores provided by the original recommendation ranking, and a greedy selection in which some set-wise magnitude is maximized by iteratively selection those items that maximize it. In both cases, we provide a high-level interface `Reranker` which, given an original recommendation, returns another recommendation that is a re-ranking of the first:
~~~
public interface Reranker<U, I> {
    public Recommendation<U, I> rerankRecommendation
    	(Recommendation<U, I> recommendation);
}
~~~

We provide an abstract class `PermutationReranker` that, rather than returning a `Recommendation` object, returns the permutation that results from the re-ranking.
The purpose of this `PermutationReranker` is to have a more compact representation of re-rankings. By saving only the permutation that defines the recommendation, we can efficiently store in disk or keep in memory many re-rankings of a single recommendation baseline.
As a direct instantiable implementation of this `PermutationReranker`, we include a `RandomReranker` which returns randomly generated permutations. Re-ranking strategies based on direct re-scoring of a recommender's output also implement directly this interface. Re-ranking methods based on greedy selection extend the abstract class `GreedyReranker`, which performs a greedy selection based on an objective function that is updated after each step of the selection. Since most of our greedy re-ranking algorithms are themselves based on a linear combination of the original recommender's scores and some novelty component, we provide an abstract `LambdaReranker` class that performs a normalized linear combination of the original scoring and the novelty component.

### Item Novelty Metrics and Re-Ranking Strategies

The user-side metrics defined in (Vargas and Castells 2011) and in Chapter 4 of (Vargas 2015) - with the exception of EILD - are implemented in package `es.uam.eps.ir.ranksys.diversity.itemnovelty`. This package includes a generic `ItemNovelty` interface for personalized novelty models, which is the base for the abstract `ItemNoveltyMetric` class for metrics and the abstract `ItemNoveltyReranker` class for direct re-ranking strategies:
~~~
public abstract class ItemNovelty<U, I> extends
 PersonalizableModel<U> {
    ...
    public UserItemNoveltyModel<U, I> getUserModel(U u) {...}
    public interface UserItemNoveltyModel<U, I> extends UserModel<U> {
        public double novelty(I i);
    }
}
~~~

In the current version of the framework, three sub-classes of `ItemNovelty` are included to represent the popularity complement (PC), free discovery (FD) and profile distance (PD) item novelty models defined in (Vargas and Castells 2011) and (Vargas 2015).

### Distance-Based Metrics and Re-Ranking Strategies

For better readability of the code, the intra-list distance-based metrics and re-ranking algorithms of (Vargas and Castells 2011) and Chapter 4 in (Vargas 2015) do not extend from the previous item novelty model package and are separated in its own package `es.uam.eps.ir.ranksys.diversity.distance`. This package defines an `ItemDistanceModel` for considering different definitions for the distance between items:
~~~
public interface ItemDistanceModel<I> {
    public double dist(I i, I j);
}
~~~
We include an abstract class `FeatureItemDistanceModel` that takes a `FeatureData` object to compute the distance between items by means of their features. Two distance functions based on Jaccard and cosine similarity are implemented by extending `FeatureItemDistanceModel`. On top of this distance models, the `EILD` class provides the implementation of the EILD metric and, respectively, the `MMR` class implements the corresponding re-ranking strategy.

### Sales Diversity Metrics

Rank and relevance-unware Sales Diversity metrics in Chapter 4 of (Vargas 2015) are implemented in the package `es.uam.eps.ir.ranksys.diversity.sales.metrics`. Since these are business-side metrics, they implement the interface `SystemMetric`. In particular, the implemented metrics are Aggregate Diversity, Entropy, Gini Index and Gini-Simpson Index. Since the three last metrics are based on the number of times each item is recommended to the community of users, they conveniently extend the abstract `AbstractSalesDiversityMetric` class that implements the count of how many items each item is recommended to users.

### Intent-Aware Metrics and Re-Ranking Strategies

Our adaptation of the Intent-Aware metrics and diversification techniques in Chapter 5 of (Vargas 2015) is contained in the package `es.uam.eps.ir.ranksys.diversity.intentaware`. The basis of this package is the `IntentModel` class, which represents the concept of user aspect space when it is defined by item features in the user profile. This `IntentModel` is then used in the implementations of the metrics ERR-IA (Agrawal et al. 2009) and &alpha;-nDCG (Clarke et al. 2008) and the xQuAD diversification method (Santos et al. 2010) provided in this package.

### Binomial Metrics and Re-Ranking Strategies

The metrics and re-ranking strategies of the Binomial framework proposed in Chapter 6 are found in package `es.uam.eps.ir.ranksys.diversity.binom`. All of them use the `BinomialModel` class, which implements the binomial probability model that defines the coverage and redundancy scores for a given recommendation list size. Metrics and re-ranking strategies for coverage, redundancy and joint diversity are included in this package.

## Examples

The module RankSys-examples contains two examples of use of the metrics and re-ranking strategies defined in the other modules. Together with this documentation, they should be used as a starting point to familiarize with the code in the framework. As more modules are added to the framework, additional example code will be added to this module.
