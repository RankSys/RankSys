/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.nn.neighborhood;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.IdxObject;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class InvertedNeighborhood implements Neighborhood {

    private final IntArrayList[] idxla;
    private final DoubleArrayList[] simla;

    public InvertedNeighborhood(int n, Neighborhood neighborhood, IntPredicate filter) {
        this.idxla = new IntArrayList[n];
        this.simla = new DoubleArrayList[n];

        IntStream.range(0, n).parallel().filter(filter).forEach(idx -> {
            this.idxla[idx] = new IntArrayList();
            this.simla[idx] = new DoubleArrayList();
        });

        IntStream.range(0, n).parallel().mapToObj(idx -> {
            return new IdxObject<>(idx, neighborhood.getNeighbors(idx));
        }).forEachOrdered(in -> {
            int idx = in.idx;
            in.v.forEach(is -> {
                if (this.idxla[is.idx] != null) {
                    this.idxla[is.idx].add(idx);
                    this.simla[is.idx].add(is.v);
                }
            });
        });
    }

    @Override
    public Stream<IdxDouble> getNeighbors(int idx) {
        IntArrayList idxl = idxla[idx];
        DoubleArrayList siml = simla[idx];
        return IntStream.range(0, idxl.size()).mapToObj(i -> new IdxDouble(idxl.getInt(i), siml.getDouble(i)));
    }

}
