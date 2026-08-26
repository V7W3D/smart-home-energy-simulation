package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorUserCI;

/**
 * The class <code>RefrigeratorConnector</code> implements a connector for
 * the <code>RefrigeratorUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
public class			RefrigeratorConnector
extends		AbstractConnector
implements	RefrigeratorUserCI
{
	@Override
	public RefrigeratorState	getState() throws Exception
	{
		return ((RefrigeratorUserCI)this.offering).getState();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((RefrigeratorUserCI)this.offering).turnOn();
	}

	@Override
	public void			turnOff() throws Exception
	{
		((RefrigeratorUserCI)this.offering).turnOff();
	}

	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((RefrigeratorUserCI)this.offering).getCurrentTemperature();
	}

	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((RefrigeratorUserCI)this.offering).getTargetTemperature();
	}

	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		((RefrigeratorUserCI)this.offering).setTargetTemperature(target);
	}

	@Override
	public void			suspendCompressor() throws Exception
	{
		((RefrigeratorUserCI)this.offering).suspendCompressor();
	}

	@Override
	public void			resumeCompressor() throws Exception
	{
		((RefrigeratorUserCI)this.offering).resumeCompressor();
	}

	@Override
	public boolean		isCompressorSuspended() throws Exception
	{
		return ((RefrigeratorUserCI)this.offering).isCompressorSuspended();
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((RefrigeratorUserCI)this.offering).getCurrentPowerLevel();
	}
}
