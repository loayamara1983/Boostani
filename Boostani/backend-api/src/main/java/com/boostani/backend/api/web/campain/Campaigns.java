package com.boostani.backend.api.web.campain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author Loay
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "rows", "count" })
public class Campaigns implements Serializable {

	@JsonProperty("rows")
	private List<List<String>> rows = new ArrayList<>();

	@JsonProperty("count")
	private Integer count;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	private final static long serialVersionUID = 2190863715354985373L;

	@JsonProperty("rows")
	public List<List<String>> getRows() {
		return rows;
	}

	@JsonProperty("rows")
	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	@JsonProperty("count")
	public Integer getCount() {
		return count;
	}

	@JsonProperty("count")
	public void setCount(Integer count) {
		this.count = count;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("rows", rows).append("count", count)
				.append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(count).append(additionalProperties).append(rows).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Campaigns) == false) {
			return false;
		}

		Campaigns rhs = ((Campaigns) other);
		return new EqualsBuilder().append(count, rhs.count).append(additionalProperties, rhs.additionalProperties)
				.append(rows, rhs.rows).isEquals();
	}

}
