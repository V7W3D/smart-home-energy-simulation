package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorUserCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>RefrigeratorOutboundPort</code> implements an outbound port for
 * the <code>RefrigeratorUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
public class			RefrigeratorOutboundPort
extends		AbstractOutboundPort
implements	RefrigeratorUserCI
{
	private static final long serialVersionUID = 1L;

	public				RefrigeratorOutboundPort(ComponentI owner) throws Exception
	{
		super(RefrigeratorUserCI.class, owner);
	}

	public				RefrigeratorOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, RefrigeratorUserCI.class, owner);
	}

	@Override
	public RefrigeratorState	getState() throws Exception
	{
		return ((RefrigeratorUserCI)this.getConnector()).getState();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((RefrigeratorUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void			turnOff() throws Exception
	{
		((RefrigeratorUserCI)this.getConnector()).turnOff();
	}

	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((RefrigeratorUserCI)this.getConnector()).getCurrentTemperature();
	}

	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((RefrigeratorUserCI)this.getConnector()).getTargetTemperature();
	}

	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		((RefrigeratorUserCI)this.getConnector()).setTargetTemperature(target);
	}

	@Override
	public void			suspendCompressor() throws Exception
	{
		((RefrigeratorUserCI)this.getConnector()).suspendCompressor();
	}

	@Override
	public void			resumeCompressor() throws Exception
	{
		((RefrigeratorUserCI)this.getConnector()).resumeCompressor();
	}

	@Override
	public boolean		isCompressorSuspended() throws Exception
	{
		return ((RefrigeratorUserCI)this.getConnector()).isCompressorSuspended();
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((RefrigeratorUserCI)this.getConnector()).getCurrentPowerLevel();
	}
}
