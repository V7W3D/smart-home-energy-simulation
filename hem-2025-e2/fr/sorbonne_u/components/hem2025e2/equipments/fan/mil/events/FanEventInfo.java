package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>FanEventInfo</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
public class FanEventInfo implements EventInformationI
{
	private static final long serialVersionUID = 1L;
	protected final String applianceId;

	public FanEventInfo(String applianceId)
	{
		super();
		assert	applianceId != null && !applianceId.isEmpty() :
				new PreconditionException(
						"applianceId != null && !applianceId.isEmpty()");
		this.applianceId = applianceId;
	}

	public String getApplianceId()
	{
		return this.applianceId;
	}

	@Override
	public String toString()
	{
		return "FanEventInfo[applianceId=" + this.applianceId + "]";
	}
}
