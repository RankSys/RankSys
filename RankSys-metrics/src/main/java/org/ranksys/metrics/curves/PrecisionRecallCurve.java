/*
 *  Copyright (C) 2016 Information Retrieval Group at Universidad Aut�noma
 *  de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.metrics.curves;

import java.util.ArrayList;
import java.util.List;
import org.ranksys.core.Recommendation;
import org.ranksys.core.util.tuples.Tuple2od;
import org.ranksys.metrics.rel.BinaryRelevanceModel;

/**
 * Class for computing the Precision-Recall curve of a recommender.
 * This class averages the output for different recommendations.
 * 
 * @author Javier Sanz-Cruzado Puig
 * @param <U> Type of the users
 * @param <I> Type of the items.
 */
public class PrecisionRecallCurve<U,I> extends Curve
{
    /**
     * The number of divisions.
     */
    private final static int NUMDIV = 11;
    /**
     * The interval for the recall values.
     */
    private final static double INTERVAL = 0.1;
    /**
     * Binary relevance model, for establishing which elements are relevant and which not.
     */
    private final BinaryRelevanceModel<U,I> binRel;
    
    /**
     * Number of recommendations
     */
    private int numRecs;
    
    /**
     * Constructor
     * @param binRel Binary relevance model which establishes the elements relevant to each user.
     */
    public PrecisionRecallCurve(BinaryRelevanceModel<U, I> binRel)
    {
        super();
        this.binRel = binRel;
        this.numRecs = 0;
    }
    
    /**
     * Finds the Precision-Recall curve for a single recommendation.
     * @param rec the recommendation.
     * @return the curve for the recommendation.
     */
    public Curve computePRCurve(Recommendation<U,I> rec)
    {
        Curve curve = new Curve();
        
        List<Double> precisions = new ArrayList<>();
        List<Integer> kr = new ArrayList<>();

        int numRels = 0;
        
        double currentR = INTERVAL;
        double maxp = 0.0;
        
        List<Tuple2od<I>> items = rec.getItems();
        U u = rec.getUser();
        int numRel = binRel.getModel(u).getRelevantItems().size();
        
        for(int i = 0; i < items.size(); ++i)
        {
            if(binRel.getModel(u).isRelevant(items.get(i).v1))
            {
                numRels++;
            }
            
            double p = (numRels + 0.0)/(i + 1.0);
            double r = (numRels + 0.0)/(numRel+0.0);
            precisions.add(p);

            if(p > maxp)
            {
                maxp = p;
            }
            
            if(r > currentR)
            {
                currentR += INTERVAL;
                kr.add(i);
            }
        }
        
        int currentKrIndex = kr.size() - 1;
        int currentKr = kr.get(currentKrIndex);
        double currentmaxp = 0.0;
       
        for(int i = precisions.size(); i >= kr.get(0); ++i)
        {
            if(precisions.get(i) > currentmaxp)
            {
                currentmaxp = precisions.get(i);
            }
            
            if(i < currentKr)
            {
                curve.addPoint(1, currentR, currentmaxp);
                currentR -= 0.1;
                currentKrIndex--;
                currentKr = kr.get(currentKrIndex);
            }
        }
        
        for(int i = kr.size(); i < NUMDIV; ++i)
        {
            curve.addPoint(INTERVAL*i, 0.0);
        }
        
        this.addPoint(0, 0.0, maxp);
        
        return curve;
    }
    
    /**
     * Adds a recommendation.
     * @param rec the recommendation to add.
     */
    public void addRecommendation(Recommendation<U,I> rec)
    {
        if(numRecs == 0)
        {
            this.append(this.computePRCurve(rec));
        }
        else
        {
            Curve c = this.computePRCurve(rec);
            for(int i = 0; i < NUMDIV; ++i) // Update the values
            {
                double r = this.getPoint(i).v1;
                double oldvalue = this.getPoint(i).v2;
                double newvalue = oldvalue + (c.getPoint(i).v2 - oldvalue)/(numRecs+1.0);
                this.replacePoint(i, r, newvalue);
            }
        }
        this.numRecs++;
    }
    
    /**
     * Resets the metric.
     */
    public void reset()
    {
        this.points.clear();
        this.numRecs = 0;
    }
}
