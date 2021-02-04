# RankSys: Java 8 Recommender Systems framework for novelty, diversity and much more

## [Team](https://github.com/RankSys/RankSys/wiki/Team) || [References](https://github.com/RankSys/RankSys/wiki/References) || [Javadoc](http://ranksys.github.io/javadoc/) || [Wiki](https://github.com/RankSys/RankSys/wiki/) || [Twitter](https://twitter.com/ranksys)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.ranksys/RankSys/badge.png)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.ranksys%22)
[![Build Status](https://travis-ci.org/RankSys/RankSys.svg?branch=master)](https://travis-ci.org/RankSys/RankSys)
[![GitHub license](https://img.shields.io/github/license/ranksys/RankSys.svg)](https://www.mozilla.org/en-US/MPL/)

## Introduction

RankSys is a new framework for the implementation and evaluation of recommendation algorithms and techniques that has resulted from a line of research work that is currently documented in several publications (see [here](https://github.com/RankSys/RankSys/wiki/References)) and a [PhD thesis](http://saulvargas.es/phd-thesis.pdf).  While it is envisioned as a framework for the generic experimentation of recommendation technologies, it includes substantial support focusing on the evaluation and enhancement of novelty and diversity. RankSys derives its name from explicitly targeting the ranking task problem, rather than rating prediction. This decision is reflected in the design of the different core interfaces and components of the framework.

The framework has been programmed with Java 8, which is the most recent version of the popular programming language. We take advantage of many of the new features of the language, such as the use of lambda functions, `Stream`'s and facilities for automatic parallelization of the code. The code licensed under the [MPL 2.0](https://www.mozilla.org/en-US/MPL/2.0/).

The publicly available version of this framework (0.4.3) includes implementations of several collaborative filtering recommendation algorithms as well as a wide variety of novelty and diversity metrics and re-ranking techniques. The modules published to date are the following:
 * RankSys-core: common and auxiliary classes of the framework.
 * RankSys-fast: support for fast and efficient implementation of data structures and algorithms.
 * RankSys-metrics: interfaces and common components for defining metrics.
 * RankSys-rec: support for generating recommendation lists.
 * RankSys-nn: nearest neighbors recommendation algorithms.
 * RankSys-mf: matrix factorization recommendation algorithms.
 * RankSys-fm: factorisation machines for recommendation tasks using [JavaFM](https://github.com/RankSys/JavaFM).
 * RankSys-lda: Latent Dirichlet Allocation for collaborative filtering recommendation algorithms.
 * RankSys-novdiv: common resources for novelty and diversity metrics and enhancement techniques.
 * RankSys-novelty: novelty metrics and enhancement techniques.
 * RankSys-diversity: diversity metrics and enhancement techniques.
 * RankSys-compression: state-of-art compression techniques for in-memory collaborative filtering.
 * RankSys-examples: examples of usage of the previous modules.

## Maven

If you want to use the whole framework, include the following dependency:
~~~
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys</artifactId>
    <version>0.4.3</version>
</dependency>
~~~
Alternatively, include in your project dependencies only the modules of RankSys that you require:
~~~
<dependency>
    <groupId>org.ranksys</groupId>
    <artifactId>RankSys-MODULENAME</artifactId>
    <version>0.4.3</version>
</dependency>
~~~
where MODULENAME is replaced by core, fast, metrics, rec, etc.
