# RankSys
## Java 8 Recommender Systems framework for novelty, diversity and much more

## Authors
 * [Saúl Vargas](http://www.saulvargas.es), [University of Glasgow](http://www.gla.ac.uk/)
 * [Pablo Castells](http://ir.ii.uam.es/castells/), [UAM](http://www.uam.es/)
 * [Contributors](https://github.com/RankSys/RankSys/wiki/Contributors)

## References

If you publish research that uses RankSys, please cite the papers listed [here](https://github.com/RankSys/RankSys/wiki/References) that best match the parts of the framework that you used.

## Introduction

RankSys is a new framework for the implementation and evaluation of recommendation algorithms and techniques that has resulted from a line of research work that is currently documented in several publications (see above) and a [PhD thesis](http://saulvargas.github.io/phd-thesis.pdf) (Vargas 2015).  While it is envisioned as a framework for the generic experimentation of recommendation technologies, it includes substantial support focusing on the evaluation and enhancement of novelty and diversity. RankSys derives its name from explicitly targeting the ranking task problem, rather than rating prediction. This decision is reflected in the design of the different core interfaces and components of the framework.

The framework has been programmed with Java 8, which is the most recent version of the popular programming language. We take advantage of many of the new features of the language, such as the use of lambda functions, `Stream`'s and facilities for automatic parallelization of the code. The code licensed under the [MPL 2.0](https://www.mozilla.org/en-US/MPL/2.0/).

The publicly available version of this framework (v0.4) includes implementations of several collaborative filtering recommendation algorithms as well as a wide variety of novelty and diversity metrics and re-ranking techniques. The modules published to date are the following:
 * RankSys-core: common and auxiliary classes of the framework.
 * RankSys-fast: support for fast and efficient implementation of data structures and algorithms.
 * RankSys-metrics: interfaces and common components for defining metrics.
 * RankSys-rec: support for generating recommendation lists.
 * RankSys-nn: nearest neighbors recommendation algorithms.
 * RankSys-mf: matrix factorization recommendation algorithms.
 * RankSys-novdiv: common resources for novelty and diversity metrics and enhancement techniques.
 * RankSys-novelty: novelty metrics and enhancement techniques
 * RankSys-diversity: diversity metrics and enhancement techniques.
 * RankSys-compression: state-of-art compression techniques for in-memory collaborative filtering.
 * RankSys-examples: examples of usage of the previous modules.
 
If you want to know more, the [wiki](https://github.com/RankSys/RankSys/wiki) provides a high-level description of the different components of the current release of the software.

## Maven

If you want to include RankSys in an existing Maven project, first include the following repository:
~~~
<repository>
 <id>jitpack.io</id>
 <url>https://jitpack.io</url>
</repository>
~~~
and, from the following dependencies, those you require:
~~~
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-core</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-fast</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-metrics</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-rec</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-nn</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-mf</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-novdiv</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-novelty</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-diversity</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-examples</artifactId>
    <version>0.4</version>
</dependency>
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-compression</artifactId>
    <version>0.4</version>
</dependency>
~~~
