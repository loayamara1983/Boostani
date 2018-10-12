package com.boostani.backend.api.web.response.campaign;

public class CompainsComisionsStats {

	private CompainsAllTimeComisions allTimeComisions;
	
	private CompainsThisMonthComisions thisMonthComisions;
	
	public CompainsComisionsStats(CompainsAllTimeComisions allTimeComisions, CompainsThisMonthComisions thisMonthComisions) {
		super();
		this.allTimeComisions = allTimeComisions;
		this.thisMonthComisions = thisMonthComisions;
	}

	/**
	 * @return the allTimeComisions
	 */
	public CompainsAllTimeComisions getAllTimeComisions() {
		return allTimeComisions;
	}

	/**
	 * @param allTimeComisions the allTimeComisions to set
	 */
	public void setAllTimeComisions(CompainsAllTimeComisions allTimeComisions) {
		this.allTimeComisions = allTimeComisions;
	}

	/**
	 * @return the thisMonthComisions
	 */
	public CompainsThisMonthComisions getThisMonthComisions() {
		return thisMonthComisions;
	}

	/**
	 * @param thisMonthComisions the thisMonthComisions to set
	 */
	public void setThisMonthComisions(CompainsThisMonthComisions thisMonthComisions) {
		this.thisMonthComisions = thisMonthComisions;
	}
	
	
}
