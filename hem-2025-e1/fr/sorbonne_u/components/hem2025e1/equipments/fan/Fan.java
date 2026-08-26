package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEM;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationCI;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationConnector;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>Fan</code> implements a fan component with
 * multiple speed settings (OFF, LOW, MEDIUM, HIGH).
 *
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
@OfferedInterfaces(offered={FanUserCI.class})
@RequiredInterfaces(required={HEMRegistrationCI.class})
public class			Fan
extends		AbstractComponent
implements	FanImplementationI
{
	public static final String	INBOUND_PORT_URI = "FAN-INBOUND-PORT-URI";
	public static final String	APPLIANCE_ID = "Fan";
	public static final String	XML_DESCRIPTOR_PATH = "hem-adapter/fan-config.xml";
	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	protected FanState					currentState;
	protected FanInboundPort			fip;
	protected HEMRegistrationOutboundPort	hemRegistrationPort;

	protected			Fan() throws Exception
	{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}

	protected			Fan(String fanInboundPortURI) throws Exception
	{
		super(1, 0);
		this.initialise(fanInboundPortURI);
	}

	protected			Fan(String reflectionInboundPortURI, String fanInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(fanInboundPortURI);
	}

	protected void		initialise(String fanInboundPortURI) throws Exception
	{
		assert	fanInboundPortURI != null && !fanInboundPortURI.isEmpty() :
				new PreconditionException("fanInboundPortURI != null && !fanInboundPortURI.isEmpty()");

		this.currentState = FanState.OFF;
		this.fip = new FanInboundPort(fanInboundPortURI, this);
		this.fip.publishPort();

		if (Fan.VERBOSE) {
			this.tracer.get().setTitle("Fan component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		
		try {
			// Create outbound port to HEM registration service
			this.hemRegistrationPort = new HEMRegistrationOutboundPort(this);
			this.hemRegistrationPort.publishPort();
			
			// Connect to HEM
			this.doPortConnection(
					this.hemRegistrationPort.getPortURI(),
					HEM.REGISTRATION_INBOUND_PORT_URI,
					HEMRegistrationConnector.class.getCanonicalName());
			
			if (Fan.VERBOSE) {
				this.traceMessage("Fan connected to HEM registration service.\n");
			}
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void	execute() throws Exception
	{
		// Register with HEM
		this.hemRegistrationPort.register(APPLIANCE_ID, INBOUND_PORT_URI, XML_DESCRIPTOR_PATH);
		
		if (Fan.VERBOSE) {
			this.traceMessage("Fan registered with HEM.\n");
		}
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.hemRegistrationPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hemRegistrationPort.unpublishPort();
			this.fip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public FanState		getState() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its state: " + this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public void			turnOn() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is turned on.\n");
		}
		assert	this.currentState == FanState.OFF :
				new PreconditionException("getState() == FanState.OFF");
		this.currentState = FanState.LOW;
	}

	@Override
	public void			turnOff() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is turned off.\n");
		}
		assert	this.currentState != FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");
		this.currentState = FanState.OFF;
	}

	@Override
	public void			setLow() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is set to LOW.\n");
		}
		assert	this.currentState != FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");
		this.currentState = FanState.LOW;
	}

	@Override
	public void			setMedium() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is set to MEDIUM.\n");
		}
		assert	this.currentState != FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");
		this.currentState = FanState.MEDIUM;
	}

	@Override
	public void			setHigh() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is set to HIGH.\n");
		}
		assert	this.currentState != FanState.OFF :
				new PreconditionException("getState() != FanState.OFF");
		this.currentState = FanState.HIGH;
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double power;
		switch (this.currentState) {
			case OFF: power = OFF_POWER; break;
			case LOW: power = LOW_POWER; break;
			case MEDIUM: power = MEDIUM_POWER; break;
			case HIGH: power = HIGH_POWER; break;
			default: power = 0.0;
		}
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its current power level: " + power + " W.\n");
		}
		return power;
	}
}
