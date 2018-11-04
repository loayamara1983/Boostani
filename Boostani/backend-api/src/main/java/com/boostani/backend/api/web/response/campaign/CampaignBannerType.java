package com.boostani.backend.api.web.response.campaign;

/**
 * 
 * @author Loay
 *
 */
public enum CampaignBannerType {

	IMAGE("Image banner"), HTML("HTML banner"), TEXT("Text Link"), FLASH("Flash banner"), PROMO_EMAIL("Promo Email"),
	SIMPLE_PDF("Simple PDF"), LINK("Link");

	private String type;

	private CampaignBannerType(String type) {
		this.type = type;
	}
	
	public static String getType(String type) {
		switch(type) {
			case "I":
				return IMAGE.type;
			case "H":
				return HTML.type;
			case "T":
				return HTML.type;
			case "F":
				return FLASH.type;
			case "E":
				return PROMO_EMAIL.type;
			case "V":
				return SIMPLE_PDF.type;
			case "A":
				return LINK.type;
		}
		
		return null;
	}
	
	
}
