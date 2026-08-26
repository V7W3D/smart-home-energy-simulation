package fr.sorbonne_u.components.hem2025e3;

public final class FanDeployment
{
	public static final int FAN_COUNT = 4;

	public static final String[] FAN_INSTANCE_NAMES = {
			"Salon",
			"Chambre",
			"Bureau",
			"Cuisine"
	};

	public static final String[] FAN_IDS = {
			"Fan_Salon",
			"Fan_Chambre",
			"Fan_Bureau",
			"Fan_Cuisine"
	};

	public static final String[] FAN_INBOUND_PORT_URIS = {
			"fan-salon-inbound",
			"fan-chambre-inbound",
			"fan-bureau-inbound",
			"fan-cuisine-inbound"
	};

	public static final String[] FAN_REFLECTION_INBOUND_PORT_URIS = {
			"fan-salon-rip",
			"fan-chambre-rip",
			"fan-bureau-rip",
			"fan-cuisine-rip"
	};

	public static final String[] FAN_XML_DESCRIPTOR_PATHS = {
			"hem-adapter/fan-salon.xml",
			"hem-adapter/fan-chambre.xml",
			"hem-adapter/fan-bureau.xml",
			"hem-adapter/fan-cuisine.xml"
	};

	public static final String[] FAN_STATE_MODEL_URIS = {
			"FanStateSILModel-Salon",
			"FanStateSILModel-Chambre",
			"FanStateSILModel-Bureau",
			"FanStateSILModel-Cuisine"
	};

	public static final String[] FAN_ELECTRICITY_MODEL_URIS = {
			"FanElectricitySILModel-Salon",
			"FanElectricitySILModel-Chambre",
			"FanElectricitySILModel-Bureau",
			"FanElectricitySILModel-Cuisine"
	};

	public static final String[] FAN_INTENSITY_VARIABLES = {
			"currentFanSalonIntensity",
			"currentFanChambreIntensity",
			"currentFanBureauIntensity",
			"currentFanCuisineIntensity"
	};

	private FanDeployment()
	{
	}
}
