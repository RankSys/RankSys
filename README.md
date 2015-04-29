# RankSys
## Java 8 Recommender Systems framework for novelty, diversity and much more

## Authors
 * [Saúl Vargas](http://www.saulvargas.es)
 * [Pablo Castells](http://ir.ii.uam.es/castells/)

## References

If you publish research that uses RankSys, please cite the papers listed [here](https://github.com/ir-uam/RankSys/wiki/References) that best match the parts of the framework that you used.

## Introduction

RankSys is a new framework for the implementation and evaluation of recommendation algorithms and techniques that has resulted from a line of research work that is currently documented in several publications (see above) and a [PhD thesis](http://ir.ii.uam.es/saul/saulvargas-thesis.pdf) (Vargas 2015).  While it is envisioned as a framework for the generic experimentation of recommendation technologies, it includes substantial support focusing on the evaluation and enhancement of novelty and diversity. RankSys derives its name from explicitly targeting the ranking task problem, rather than rating prediction. This decision is reflected in the design of the different core interfaces and components of the framework.

The framework has been programmed with Java 8, which is the most recent version of the popular programming language. We take advantage of many of the new features of the language, such as the use of lambda functions, `Stream`'s and facilities for automatic parallelization of the code. The code licensed under the GPL V3, which allows the free use, study, distribution and modification of the software as long as derived works are distributed under the same license.

To date, the publicly available version of this framework includes the modules that implement novelty and diversity metrics and re-ranking techniques and the required core components of the framework:
 * RankSys-core, which contains the common and auxiliary classes of the framework.
 * RankSys-metrics, which contains the interfaces and common components for defining metrics.
 * RankSys-diversity, which contains the novelty and diversity metrics and re-ranking strategies.
 * RankSys-examples, which provides examples of usage of the previous modules.
 
If you want to know more, the [wiki](https://github.com/ir-uam/RankSys/wiki) provides a high-level description of the different components of the current release of the software.
