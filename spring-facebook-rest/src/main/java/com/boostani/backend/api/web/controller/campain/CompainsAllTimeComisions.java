package com.boostani.backend.api.web.controller.campain;

public class CompainsAllTimeComisions {

	private Long totalCommisions;
	private Long totalSales;
	private Long totalPendingCommisions;
	private Long totalUnpaidApprovedCommisions;
	
	public CompainsAllTimeComisions(Long totalCommisions, Long totalSales, Long totalPendingCommisions,
			Long totalUnpaidApprovedCommisions) {
		super();
		this.totalCommisions = totalCommisions;
		this.totalSales = totalSales;
		this.totalPendingCommisions = totalPendingCommisions;
		this.totalUnpaidApprovedCommisions = totalUnpaidApprovedCommisions;
	}
	/**
	 * @return the totalCommisions
	 */
	public Long getTotalCommisions() {
		return totalCommisions;
	}
	/**
	 * @param totalCommisions the totalCommisions to set
	 */
	public void setTotalCommisions(Long totalCommisions) {
		this.totalCommisions = totalCommisions;
	}
	/**
	 * @return the totalSales
	 */
	public Long getTotalSales() {
		return totalSales;
	}
	/**
	 * @param totalSales the totalSales to set
	 */
	public void setTotalSales(Long totalSales) {
		this.totalSales = totalSales;
	}
	/**
	 * @return the totalPendingCommisions
	 */
	public Long getTotalPendingCommisions() {
		return totalPendingCommisions;
	}
	/**
	 * @param totalPendingCommisions the totalPendingCommisions to set
	 */
	public void setTotalPendingCommisions(Long totalPendingCommisions) {
		this.totalPendingCommisions = totalPendingCommisions;
	}
	/**
	 * @return the totalUnpaidApprovedCommisions
	 */
	public Long getTotalUnpaidApprovedCommisions() {
		return totalUnpaidApprovedCommisions;
	}
	/**
	 * @param totalUnpaidApprovedCommisions the totalUnpaidApprovedCommisions to set
	 */
	public void setTotalUnpaidApprovedCommisions(Long totalUnpaidApprovedCommisions) {
		this.totalUnpaidApprovedCommisions = totalUnpaidApprovedCommisions;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((totalCommisions == null) ? 0 : totalCommisions.hashCode());
		result = prime * result + ((totalPendingCommisions == null) ? 0 : totalPendingCommisions.hashCode());
		result = prime * result + ((totalSales == null) ? 0 : totalSales.hashCode());
		result = prime * result
				+ ((totalUnpaidApprovedCommisions == null) ? 0 : totalUnpaidApprovedCommisions.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompainsAllTimeComisions other = (CompainsAllTimeComisions) obj;
		if (totalCommisions == null) {
			if (other.totalCommisions != null)
				return false;
		} else if (!totalCommisions.equals(other.totalCommisions))
			return false;
		if (totalPendingCommisions == null) {
			if (other.totalPendingCommisions != null)
				return false;
		} else if (!totalPendingCommisions.equals(other.totalPendingCommisions))
			return false;
		if (totalSales == null) {
			if (other.totalSales != null)
				return false;
		} else if (!totalSales.equals(other.totalSales))
			return false;
		if (totalUnpaidApprovedCommisions == null) {
			if (other.totalUnpaidApprovedCommisions != null)
				return false;
		} else if (!totalUnpaidApprovedCommisions.equals(other.totalUnpaidApprovedCommisions))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AllTimeComisions [totalCommisions=" + totalCommisions + ", totalSales=" + totalSales
				+ ", totalPendingCommisions=" + totalPendingCommisions + ", totalUnpaidApprovedCommisions="
				+ totalUnpaidApprovedCommisions + "]";
	}
	
	
}
