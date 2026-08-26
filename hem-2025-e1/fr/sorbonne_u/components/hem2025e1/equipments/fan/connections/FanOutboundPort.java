package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>FanOutboundPort</code> implements an outbound port for
 * the <code>FanUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
public class			FanOutboundPort
extends		AbstractOutboundPort
implements	FanUserCI
{
	private static final long serialVersionUID = 1L;

	public				FanOutboundPort(ComponentI owner) throws Exception
	{
		super(FanUserCI.class, owner);
	}

	public				FanOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, FanUserCI.class, owner);
	}

	@Override
	public FanState		getState() throws Exception
	{
		return ((FanUserCI)this.getConnector()).getState();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((FanUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void			turnOff() throws Exception
	{
		((FanUserCI)this.getConnector()).turnOff();
	}

	@Override
	public void			setLow() throws Exception
	{
		((FanUserCI)this.getConnector()).setLow();
	}

	@Override
	public void			setMedium() throws Exception
	{
		((FanUserCI)this.getConnector()).setMedium();
	}

	@Override
	public void			setHigh() throws Exception
	{
		((FanUserCI)this.getConnector()).setHigh();
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((FanUserCI)this.getConnector()).getCurrentPowerLevel();
	}
}
