/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.diversity.itemnovelty;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import static java.lang.Math.log;
import java.util.IntSummaryStatistics;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class FDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final TObjectDoubleMap<I> itemNovelty;

    public FDItemNovelty(RecommenderData<U, I, ?> recommenderData) {
        IntSummaryStatistics stats = recommenderData.getAllItems().mapToInt(i -> recommenderData.numUsers(i)).summaryStatistics();
        long norm = stats.getSum();
        double maxNov = -log(stats.getMin() / norm) / log(2);
        
        itemNovelty = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, maxNov);
        recommenderData.getAllItems().forEach(i -> {
            itemNovelty.put(i, -log(recommenderData.numUsers(i) / (double) norm) / log(2));
        });
    }

    @Override
    public double novelty(I i, U u) {
        return itemNovelty.get(i);
    }

}
