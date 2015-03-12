/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.rec.Recommender;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface RecommendationRunner<U, I> {

    public default void run(Recommender<U, I> recommender, String path) throws IOException {
        run(recommender, new FileOutputStream(path));
    }
    
    
    public void run(Recommender<U, I> recommender, OutputStream out) throws IOException;
}
