package fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineUserCI;

/**
 * The class <code>WashingMachineConnector</code> implements a connector for
 * the <code>WashingMachineUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-29</p>
 * 
 * @author	Softweavers
 */
public class			WashingMachineConnector
extends		AbstractConnector
implements	WashingMachineUserCI
{
	@Override
	public WashingMachineState	getState() throws Exception
	{
		return ((WashingMachineUserCI)this.offering).getState();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((WashingMachineUserCI)this.offering).turnOn();
	}

	@Override
	public void			turnOff() throws Exception
	{
		((WashingMachineUserCI)this.offering).turnOff();
	}

	@Override
	public WashProgram	getSelectedProgram() throws Exception
	{
		return ((WashingMachineUserCI)this.offering).getSelectedProgram();
	}

	@Override
	public void			selectProgram(WashProgram program) throws Exception
	{
		((WashingMachineUserCI)this.offering).selectProgram(program);
	}

	@Override
	public void			startCycle() throws Exception
	{
		((WashingMachineUserCI)this.offering).startCycle();
	}

	@Override
	public void			scheduleCycle(int delayMinutes) throws Exception
	{
		((WashingMachineUserCI)this.offering).scheduleCycle(delayMinutes);
	}

	@Override
	public int			getScheduledDelay() throws Exception
	{
		return ((WashingMachineUserCI)this.offering).getScheduledDelay();
	}

	@Override
	public void			cancelCycle() throws Exception
	{
		((WashingMachineUserCI)this.offering).cancelCycle();
	}

	@Override
	public void			suspendCycle() throws Exception
	{
		((WashingMachineUserCI)this.offering).suspendCycle();
	}

	@Override
	public void			resumeCycle() throws Exception
	{
		((WashingMachineUserCI)this.offering).resumeCycle();
	}

	@Override
	public boolean		isSuspended() throws Exception
	{
		return ((WashingMachineUserCI)this.offering).isSuspended();
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((WashingMachineUserCI)this.offering).getCurrentPowerLevel();
	}
}
