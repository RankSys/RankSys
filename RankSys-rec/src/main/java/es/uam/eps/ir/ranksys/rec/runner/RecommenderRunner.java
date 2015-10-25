/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.rec.Recommender;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Recommender runner. Implementing classes provide convenient ways of 
 * generating recommendations according to a specific recommendation 
 * evaluation methodology and storing/printing them.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface RecommenderRunner<U, I> {

    /**
     * Runs the recommender and stores the recommendations in a file.
     *
     * @param recommender recommender to be run
     * @param path path of the file where the recommendations are saved
     * @throws IOException when file does not exist or other IO error
     */
    public default void run(Recommender<U, I> recommender, String path) throws IOException {
        run(recommender, new FileOutputStream(path));
    }
    
    /**
     * Runs the recommender and prints the recommendations to an output
     * stream.
     *
     * @param recommender recommender to be run
     * @param out output stream
     * @throws IOException when an IO error
     */
    public void run(Recommender<U, I> recommender, OutputStream out) throws IOException;
}
