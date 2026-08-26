package fr.sorbonne_u.components.hem2025e3.equipments.fan.sil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI.FanState;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanOperationI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.AbstractFanEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.FanEventInfo;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetMediumSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanCyPhy;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI.VariableValue;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanElectricitySILModel</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
@ModelExternalEvents(imported = {SwitchOnFan.class,
							 SwitchOffFan.class,
							 SetLowSpeed.class,
							 SetMediumSpeed.class,
							 SetHighSpeed.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
// -----------------------------------------------------------------------------
public class			FanElectricitySILModel
extends		AtomicHIOA
implements	FanOperationI
{
	private static final long	serialVersionUID = 1L;
	public static boolean		VERBOSE = true;
	public static boolean		DEBUG = false;

	public static final String	URI = FanElectricitySILModel.class.getSimpleName();

	protected FanState			currentState = FanState.OFF;
	protected boolean			consumptionHasChanged = false;

	protected double			lowSpeedConsumption;
	protected double			mediumSpeedConsumption;
	protected double			highSpeedConsumption;
	protected double			tension;
	protected String			applianceId;

	protected double			totalConsumption;
	protected int			externalEventsCount;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(
		FanElectricitySILModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException(
						"Precondition violation: instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.lowSpeedConsumption > 0.0,
				FanElectricitySILModel.class,
				instance,
				"lowSpeedConsumption > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.mediumSpeedConsumption > instance.lowSpeedConsumption,
				FanElectricitySILModel.class,
				instance,
				"mediumSpeedConsumption > lowSpeedConsumption");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.highSpeedConsumption > instance.mediumSpeedConsumption,
				FanElectricitySILModel.class,
				instance,
				"highSpeedConsumption > mediumSpeedConsumption");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.totalConsumption >= 0.0,
				FanElectricitySILModel.class,
				instance,
				"totalConsumption >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentState != null,
				FanElectricitySILModel.class,
				instance,
				"currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.currentIntensity.isInitialised() ||
							instance.currentIntensity.getValue() >= 0.0,
				FanElectricitySILModel.class,
				instance,
				"!currentIntensity.isInitialised() || "
				+ "currentIntensity.getValue() >= 0.0");
		return ret;
	}

	protected static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= FanCyPhy.staticInvariants();
		ret &= FanSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanElectricitySILModel.class,
				"URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				LOW_SPEED_CONSUMPTION_RPNAME != null
							&& !LOW_SPEED_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricitySILModel.class,
				"LOW_SPEED_CONSUMPTION_RPNAME != null && "
				+ "!LOW_SPEED_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				MEDIUM_SPEED_CONSUMPTION_RPNAME != null
							&& !MEDIUM_SPEED_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricitySILModel.class,
				"MEDIUM_SPEED_CONSUMPTION_RPNAME != null && "
				+ "!MEDIUM_SPEED_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				HIGH_SPEED_CONSUMPTION_RPNAME != null
							&& !HIGH_SPEED_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricitySILModel.class,
				"HIGH_SPEED_CONSUMPTION_RPNAME != null && "
				+ "!HIGH_SPEED_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty(),
				FanElectricitySILModel.class,
				"TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(FanElectricitySILModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException(
						"Precondition violation: instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanElectricitySILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.lowSpeedConsumption = FanImplementationI.LOW_POWER;
		this.mediumSpeedConsumption = FanImplementationI.MEDIUM_POWER;
		this.highSpeedConsumption = FanImplementationI.HIGH_POWER;
		this.tension = 220.0;
		this.applianceId = null;

		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public void			turnOn()
	{
		if (this.currentState == FanState.OFF) {
			this.currentState = FanState.LOW;
			this.markConsumptionHasChanged();
		}
	}

	@Override
	public void			turnOff()
	{
		if (this.currentState != FanState.OFF) {
			this.currentState = FanState.OFF;
			this.markConsumptionHasChanged();
		}
	}

	@Override
	public void			setHigh()
	{
		if (this.currentState != FanState.OFF
				&& this.currentState != FanState.HIGH) {
			this.currentState = FanState.HIGH;
			this.markConsumptionHasChanged();
		}
	}

	@Override
	public void			setLow()
	{
		if (this.currentState != FanState.OFF
				&& this.currentState != FanState.LOW) {
			this.currentState = FanState.LOW;
			this.markConsumptionHasChanged();
		}
	}

	@Override
	public void			setMedium()
	{
		if (this.currentState != FanState.OFF
				&& this.currentState != FanState.MEDIUM) {
			this.currentState = FanState.MEDIUM;
			this.markConsumptionHasChanged();
		}
	}

	protected void			markConsumptionHasChanged()
	{
		this.consumptionHasChanged = true;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time startTime)
	{
		super.initialiseState(startTime);

		this.currentState = FanState.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;
		this.externalEventsCount = 0;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		this.currentIntensity.initialise(0.0);

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.getCurrentStateTime() == null) {
			System.out.println("[FanElectricitySILModel] currentStateTime is null for "
					+ this.getURI());
		}
		Duration ret;
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
			ret = new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
			ret = Duration.INFINITY;
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");

		return ret;
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		switch (this.currentState)
		{
			case LOW :
				this.currentIntensity.setNewValue(
						this.lowSpeedConsumption/this.tension,
						t);
				break;
			case MEDIUM :
				this.currentIntensity.setNewValue(
						this.mediumSpeedConsumption/this.tension,
						t);
				break;
			case HIGH :
				this.currentIntensity.setNewValue(
						this.highSpeedConsumption/this.tension,
						t);
				break;
			default :
				this.currentIntensity.setNewValue(0.0, t);
		}

		if (VERBOSE) {
			StringBuffer message =
					new StringBuffer("executes an internal transition ");
			message.append("with current consumption ");
			message.append(this.currentIntensity.getValue());
			message.append(" ");
			message.append(ElectricMeterImplementationI.POWER_UNIT);
			message.append(" at ");
			message.append(this.currentIntensity.getTime());
			this.logMessage(message.toString());
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
							elapsedTime,
							this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
							elapsedTime,
							this.tension * this.currentIntensity.getValue());
		}

		if (!this.shouldProcessEvent(ce)) {
			return;
		}

		this.externalEventsCount++;

		if (VERBOSE) {
			StringBuffer message =
					new StringBuffer("executes an external transition ");
			message.append(ce.toString());
			message.append(")");
			this.logMessage(message.toString());
		}

		assert	ce instanceof AbstractFanEvent :
				new RuntimeException(
						ce + " is not an event that a FanElectricitySILModel can "
						+ "receive and process.");
		ce.executeOn(this);

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
							d,
							this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
							d,
							this.tension * this.currentIntensity.getValue());
		}

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	
	public static final String		LOW_SPEED_CONSUMPTION_RPNAME =
										"LOW_SPEED_CONSUMPTION";
	public static final String		MEDIUM_SPEED_CONSUMPTION_RPNAME =
										"MEDIUM_SPEED_CONSUMPTION";
	public static final String		HIGH_SPEED_CONSUMPTION_RPNAME =
										"HIGH_SPEED_CONSUMPTION";
	public static final String		TENSION_RPNAME = "TENSION";
	public static final String		APPLIANCE_ID_RPNAME = "APPLIANCE_ID";

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String lowName =
			ModelI.createRunParameterName(getURI(),
									  LOW_SPEED_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			this.lowSpeedConsumption = (double) simParams.get(lowName);
		}
		String mediumName =
			ModelI.createRunParameterName(getURI(),
									  MEDIUM_SPEED_CONSUMPTION_RPNAME);
		if (simParams.containsKey(mediumName)) {
			this.mediumSpeedConsumption = (double) simParams.get(mediumName);
		}
		String highName =
			ModelI.createRunParameterName(getURI(),
									  HIGH_SPEED_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			this.highSpeedConsumption = (double) simParams.get(highName);
		}
		String tensionName =
				ModelI.createRunParameterName(getURI(), TENSION_RPNAME);
		if (simParams.containsKey(tensionName)) {
			this.tension = (double) simParams.get(tensionName);
		}
		String applianceIdName =
				ModelI.createRunParameterName(getURI(), APPLIANCE_ID_RPNAME);
		if (simParams.containsKey(applianceIdName)) {
			this.applianceId = (String) simParams.get(applianceIdName);
		}

		if (simParams.containsKey(
					AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	protected boolean		shouldProcessEvent(Event event)
	{
		EventInformationI info = event.getEventInformation();
		if (info instanceof FanEventInfo && this.applianceId != null) {
			String targetId = ((FanEventInfo) info).getApplianceId();
			return this.applianceId.equals(targetId);
		}
		return true;
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class		FanElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh
		protected int		externalEventsCount;

		public				FanElectricityReport(
			String modelURI,
			double totalConsumption,
			int externalEventsCount
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
			this.externalEventsCount = externalEventsCount;
		}

		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String	printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total consumption = ");
			ret.append(this.totalConsumption);
			ret.append(" kwh\n");
			ret.append(indent);
			ret.append('|');
			ret.append("external events received = ");
			ret.append(this.externalEventsCount);
			ret.append("\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new FanElectricityReport(this.getURI(),
				this.totalConsumption,
				this.externalEventsCount);
	}

	public VariableValue<Double>	getCurrentIntensity()
	{
		return new VariableValue<Double>(
							this.currentIntensity.getValue(),
							this.currentIntensity.getTime());
	}
}
// -----------------------------------------------------------------------------
