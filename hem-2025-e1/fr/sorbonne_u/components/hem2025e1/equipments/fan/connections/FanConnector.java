package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;

/**
 * The class <code>FanConnector</code> implements a connector for
 * the <code>FanUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
public class			FanConnector
extends		AbstractConnector
implements	FanUserCI
{
	@Override
	public FanState		getState() throws Exception
	{
		return ((FanUserCI)this.offering).getState();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((FanUserCI)this.offering).turnOn();
	}

	@Override
	public void			turnOff() throws Exception
	{
		((FanUserCI)this.offering).turnOff();
	}

	@Override
	public void			setLow() throws Exception
	{
		((FanUserCI)this.offering).setLow();
	}

	@Override
	public void			setMedium() throws Exception
	{
		((FanUserCI)this.offering).setMedium();
	}

	@Override
	public void			setHigh() throws Exception
	{
		((FanUserCI)this.offering).setHigh();
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((FanUserCI)this.offering).getCurrentPowerLevel();
	}
}
