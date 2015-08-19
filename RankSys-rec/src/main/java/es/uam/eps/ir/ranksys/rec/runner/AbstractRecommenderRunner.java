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

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generic recommender runner. This class handles the print of the output.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class AbstractRecommenderRunner<U, I> implements RecommenderRunner<U, I> {

    private final List<U> users;
    private final RecommendationFormat<U, I> format;

    /**
     * Constructor.
     *
     * @param users target users for which recommendations are generated
     * @param format output recommendation format
     */
    public AbstractRecommenderRunner(Stream<U> users, RecommendationFormat<U, I> format) {
        this.users = users.sorted().collect(Collectors.toList());
        this.format = format;
    }

    /**
     * Prints the recommendations.
     *
     * @param recProvider function that provides the recommendations by calling
     * a recommender
     * @param out output stream through which recommendations are printed
     * @throws IOException when IO error
     */
    protected void run(Function<U, Recommendation<U, I>> recProvider, OutputStream out) throws IOException {
        try (RecommendationFormat.Writer<U, I> writer = format.getWriter(out)) {
            Map<U, Recommendation<U, I>> pendingRecommendations = new HashMap<>();
            List<U> usersAux = new ArrayList<>(users);
            
            users.parallelStream()
                    .map(user -> recProvider.apply(user))
                    .forEachOrdered(recommendation -> {
                        if (recommendation.getUser().equals(usersAux.get(0))) {
                            writeCatchExceptions(writer, recommendation);
                            usersAux.remove(0);

                            while (!usersAux.isEmpty() && pendingRecommendations.containsKey(usersAux.get(0))) {
                                recommendation = pendingRecommendations.get(usersAux.get(0));
                                writeCatchExceptions(writer, recommendation);
                                usersAux.remove(0);
                            }
                        } else {
                            pendingRecommendations.put(recommendation.getUser(), recommendation);
                        }
                    });

            usersAux.forEach(user -> writeCatchExceptions(writer, pendingRecommendations.get(user)));
        }
    }

    private void writeCatchExceptions(RecommendationFormat.Writer<U, I> writer, Recommendation<U, I> recommendation) {
        try {
            writer.write(recommendation);
        } catch (IOException ex) {
            getLogger(FilterRecommenderRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
