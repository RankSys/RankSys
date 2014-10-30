/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.diversity.itemnovelty.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.diversity.reranking.PermutationReranker;
import es.uam.eps.ir.ranksys.core.util.structs.IntDoubleTopN;
import es.uam.eps.ir.ranksys.core.util.Stats;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ItemNoveltyReranker<U, I> extends PermutationReranker<U, I> {

    private final double lambda;
    private final ItemNovelty<U, I> novelty;
    private final int cutoff;
    private final boolean norm;

    public ItemNoveltyReranker(double lambda, ItemNovelty<U, I> novelty, boolean norm) {
        this(lambda, novelty, 0, norm);
    }

    public ItemNoveltyReranker(double lambda, ItemNovelty<U, I> novelty, int cutoff, boolean norm) {
        this.lambda = lambda;
        this.novelty = novelty;
        this.cutoff = cutoff;
        this.norm = norm;
    }

    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation) {
        U user = recommendation.getUser();
        ItemNovelty.UserItemNoveltyModel uinm = novelty.getUserModel(user);
        
        int N = cutoff;
        if (cutoff == 0) {
            N = recommendation.getItems().size();
        }

        if (lambda == 0.0) {
            return getBasePerm(Math.min(N, recommendation.getItems().size()));
        }

        TObjectDoubleMap<Object> novMap = new TObjectDoubleHashMap<>();
        Stats relStats = new Stats();
        Stats novStats = new Stats();
        recommendation.getItems().forEach(itemValue -> {
            double nov = uinm.novelty(itemValue.id);
            novMap.put(itemValue.id, nov);
            relStats.accept(itemValue.v);
            novStats.accept(nov);
        });

        IntDoubleTopN topN = new IntDoubleTopN(N);
        List<IdDoublePair<I>> list = recommendation.getItems();
        for (int i = 0; i < list.size(); i++) {
            topN.add(i, value(list.get(i), relStats, novMap, novStats));
        }
        topN.sort();

        int[] perm = new int[topN.size()];
        for (int i = 0; i < topN.size(); i++) {
            perm[i] = topN.getKeyAt(topN.size() - i - 1);
        }

        return perm;
    }

    protected double norm(double score, Stats stats) {
        if (norm) {
            return (score - stats.getMean()) / stats.getStandardDeviation();
        } else {
            return score;
        }
    }

    protected double value(IdDoublePair<I> iv, Stats relStats, TObjectDoubleMap<Object> novMap, Stats novStats) {
        return (1 - lambda) * norm(iv.v, relStats) + lambda * norm(novMap.get(iv.id), novStats);
    }
}
