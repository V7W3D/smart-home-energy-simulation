package fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineUserCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>WashingMachineOutboundPort</code> implements an outbound port for
 * the <code>WashingMachineUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-29</p>
 * 
 * @author	Softweavers
 */
public class			WashingMachineOutboundPort
extends		AbstractOutboundPort
implements	WashingMachineUserCI
{
	private static final long serialVersionUID = 1L;

	public				WashingMachineOutboundPort(ComponentI owner) throws Exception
	{
		super(WashingMachineUserCI.class, owner);
	}

	public				WashingMachineOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, WashingMachineUserCI.class, owner);
	}

	@Override
	public WashingMachineState	getState() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getState();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void			turnOff() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).turnOff();
	}

	@Override
	public WashProgram	getSelectedProgram() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getSelectedProgram();
	}

	@Override
	public void			selectProgram(WashProgram program) throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).selectProgram(program);
	}

	@Override
	public void			startCycle() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).startCycle();
	}

	@Override
	public void			scheduleCycle(int delayMinutes) throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).scheduleCycle(delayMinutes);
	}

	@Override
	public int			getScheduledDelay() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getScheduledDelay();
	}

	@Override
	public void			cancelCycle() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).cancelCycle();
	}

	@Override
	public void			suspendCycle() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).suspendCycle();
	}

	@Override
	public void			resumeCycle() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).resumeCycle();
	}

	@Override
	public boolean		isSuspended() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).isSuspended();
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getCurrentPowerLevel();
	}
}
