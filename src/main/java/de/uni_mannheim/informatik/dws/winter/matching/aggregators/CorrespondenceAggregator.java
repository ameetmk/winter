/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.uni_mannheim.informatik.dws.winter.matching.aggregators;

import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.processing.DataAggregator;

/**
 * An aggregation function for correspondences that sums up the scores of all correspondences in the same group.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 *
 */
public class CorrespondenceAggregator<TypeA extends Matchable, TypeB extends Matchable> 
	implements DataAggregator<Pair<TypeA, TypeA>, Correspondence<TypeA,TypeB>, Correspondence<TypeA,TypeB>> 
{
	
	private static final long serialVersionUID = 1L;
	
	private double finalThreshold = 0.0;
	
	
	public CorrespondenceAggregator(double finalThreshold) {
		this.finalThreshold = finalThreshold;
	}
	
	/**
	 * @return the finalThreshold
	 */
	public double getFinalThreshold() {
		return finalThreshold;
	}
	
	/**
	 * Calculates the weight of a vote
	 * @param cor
	 * @return The similarity score
	 */
	protected double getSimilarityScore(Correspondence<TypeA, TypeB> cor) {
		return cor.getSimilarityScore();
	}
	
	@Override
	public Correspondence<TypeA, TypeB> initialise(Pair<TypeA, TypeA> keyValue) {
		return null;
	}
	
	@Override
	public Correspondence<TypeA, TypeB> aggregate(Correspondence<TypeA, TypeB> previousResult,
			Correspondence<TypeA, TypeB> record) {
	
		if(previousResult==null) {
			record.setsimilarityScore(getSimilarityScore(record));
			return record;
		} else {
			previousResult.setsimilarityScore(previousResult.getSimilarityScore() + getSimilarityScore(record));
			previousResult.setCausalCorrespondences(previousResult.getCausalCorrespondences().append(record.getCausalCorrespondences()));
			return previousResult;
		}
	}

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.processing.DataAggregator#createFinalValue(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Correspondence<TypeA, TypeB> createFinalValue(Pair<TypeA, TypeA> keyValue,
			Correspondence<TypeA, TypeB> result) {
		
		if(result.getSimilarityScore()>0.0 && result.getSimilarityScore()>=getFinalThreshold()) {
			return result;
		} else {
			return null;
		}
	}
}

