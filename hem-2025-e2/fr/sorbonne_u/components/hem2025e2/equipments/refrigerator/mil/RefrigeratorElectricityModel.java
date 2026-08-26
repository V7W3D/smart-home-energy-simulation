package fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil;

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
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI.RefrigeratorState;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorActive;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorInactive;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI.VariableValue;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RefrigeratorElectricityModel</code>.
 *
 * <p>Created on : 2026-01-15</p>
 *
 * @author	Softweavers
 */
@ModelExternalEvents(
	imported = {CompressorActive.class, CompressorInactive.class},
	exported = {CompressorActive.class, CompressorInactive.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentTemperature", type = Double.class)
// -----------------------------------------------------------------------------
public class			RefrigeratorElectricityModel
extends		AtomicHIOA
implements	RefrigeratorOperationI
{
	private static final long	serialVersionUID = 1L;
	public static final String	URI = RefrigeratorElectricityModel.class.
											getSimpleName();
	public static boolean		VERBOSE = true;
	public static boolean		DEBUG = false;

	public static final double	AMBIENT_TEMPERATURE = 20.0;
	public static final double	COOL_TARGET_TEMPERATURE =
							RefrigeratorImplementationI.DEFAULT_TARGET_TEMPERATURE;
	public static final double	INITIAL_TEMPERATURE = 5.0;
	public static final double	TIME_CONSTANT = 1.0; // hours
	public static final double	INTEGRATION_STEP = 10.0/3600.0; // 10 seconds
	public static final double	TENSION = 220.0;
	public static final double	TEMPERATURE_UPDATE_TOLERANCE = 1.0e-6;

	protected RefrigeratorState	currentState = RefrigeratorState.SUSPENDED;
	protected EventI		lastReceived;
	protected final Duration	integrationStep;
	protected double		totalConsumption;
	protected double		initialTemperature = INITIAL_TEMPERATURE;
	protected double		targetTemperature = COOL_TARGET_TEMPERATURE;
	protected ArrayList<String>	controlTrace;

	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final DerivableValue<Double>	currentTemperature =
													new DerivableValue<Double>(this);

	protected static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				RefrigeratorElectricityModel.class,
				"URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				TIME_CONSTANT > 0.0,
				RefrigeratorElectricityModel.class,
				"TIME_CONSTANT > 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				INTEGRATION_STEP > 0.0,
				RefrigeratorElectricityModel.class,
				"INTEGRATION_STEP > 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				TENSION > 0.0,
				RefrigeratorElectricityModel.class,
				"TENSION > 0.0");
		return ret;
	}

	protected static boolean	implementationInvariants(
		RefrigeratorElectricityModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentState != null,
				RefrigeratorElectricityModel.class,
				instance,
				"currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.integrationStep.getSimulatedDuration() > 0.0,
				RefrigeratorElectricityModel.class,
				instance,
				"integrationStep.getSimulatedDuration() > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.currentIntensity.isInitialised()
						|| instance.currentIntensity.getValue() >= 0.0,
				RefrigeratorElectricityModel.class,
				instance,
				"!currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.totalConsumption >= 0.0,
				RefrigeratorElectricityModel.class,
				instance,
				"totalConsumption >= 0.0");
		return ret;
	}

	public				RefrigeratorElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.integrationStep = new Duration(INTEGRATION_STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	implementationInvariants(this) :
				new NeoSim4JavaException(
					"RefrigeratorElectricityModel.implementationInvariants(this)");
	}

	protected double	getTargetTemperature()
	{
		if (this.currentState == RefrigeratorState.ON) {
			return this.targetTemperature;
		}
		return AMBIENT_TEMPERATURE;
	}

	protected double	computeDerivative(double current)
	{
		double target = this.getTargetTemperature();
		return (target - current) / TIME_CONSTANT;
	}

	protected double	computeNewTemperature(double deltaT)
	{
		Time t = this.currentTemperature.getTime();
		double oldTemp = this.currentTemperature.evaluateAt(t);
		if (deltaT <= TEMPERATURE_UPDATE_TOLERANCE) {
			return oldTemp;
		}
		return oldTemp + this.currentTemperature.getFirstDerivative() * deltaT;
	}

	protected void		updateCurrentIntensity(Time t)
	{
		if (this.currentState == RefrigeratorState.ON) {
			this.currentIntensity.setNewValue(
					RefrigeratorImplementationI.COMPRESSOR_POWER/TENSION, t);
		} else {
			this.currentIntensity.setNewValue(0.0, t);
		}
	}

	public VariableValue<Double>	getCurrentTemperature()
	{
		return new VariableValue<Double>(
								this.currentTemperature.getValue(),
								this.currentTemperature.getTime());
	}

	// -------------------------------------------------------------------------
	// RefrigeratorOperationI
	// -------------------------------------------------------------------------

	@Override
	public void			resumeCompressor()
	{
		if (this.currentState != RefrigeratorState.ON) {
			this.currentState = RefrigeratorState.ON;
		}
	}

	@Override
	public void			suspendCompressor()
	{
		if (this.currentState != RefrigeratorState.SUSPENDED) {
			this.currentState = RefrigeratorState.SUSPENDED;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time startTime)
	{
		super.initialiseState(startTime);
		this.currentState = RefrigeratorState.SUSPENDED;
		this.lastReceived = null;
		this.totalConsumption = 0.0;
		this.controlTrace = new ArrayList<>();

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		assert	implementationInvariants(this) :
				new NeoSim4JavaException(
					"RefrigeratorElectricityModel.implementationInvariants(this)");
	}

	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		double derivative = this.computeDerivative(this.initialTemperature);
		this.currentTemperature.initialise(this.initialTemperature, derivative);
		this.currentIntensity.initialise(0.0);
		this.recordTrace("init");

		assert	implementationInvariants(this) :
				new NeoSim4JavaException(
					"RefrigeratorElectricityModel.implementationInvariants(this)");
	}

	@Override
	public ArrayList<EventI>	output()
	{
		if (this.lastReceived == null) {
			return null;
		}
		ArrayList<EventI> ret = new ArrayList<EventI>();
		ret.add(this.lastReceived);
		this.lastReceived = null;
		return ret;
	}

	@Override
	public Duration		timeAdvance()
	{
		return this.integrationStep;
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
							elapsedTime,
							this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
							elapsedTime,
							TENSION * this.currentIntensity.getValue());
		}

		double newTemp =
				this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		double newDerivative = this.computeDerivative(newTemp);
		Time t = new Time(this.getCurrentStateTime().getSimulatedTime(),
						 this.getSimulatedTimeUnit());
		this.currentTemperature.setNewValue(newTemp, newDerivative, t);

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer();
			sb.append(this.currentTemperature.getTime().getSimulatedTime());
			sb.append(this.currentState == RefrigeratorState.ON ? " (on)" : " (off)");
			sb.append(" : ");
			sb.append(this.currentTemperature.getValue());
			sb.append(" C, I=");
			sb.append(this.currentIntensity.getValue());
			sb.append(" A");
			this.logMessage(sb.toString());
		}
		this.recordTrace("internal");

		super.userDefinedInternalTransition(elapsedTime);

		assert	implementationInvariants(this) :
				new NeoSim4JavaException(
					"RefrigeratorElectricityModel.implementationInvariants(this)");
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce != null;
		this.lastReceived = ce;

		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
							elapsedTime,
							this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
							elapsedTime,
							TENSION * this.currentIntensity.getValue());
		}

		double newTemp =
				this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		ce.executeOn(this);

		double newDerivative = this.computeDerivative(newTemp);
		Time newTime = new Time(
				this.getCurrentStateTime().getSimulatedTime()
						+ elapsedTime.getSimulatedDuration(),
				this.getSimulatedTimeUnit());
		this.currentTemperature.setNewValue(newTemp, newDerivative, newTime);
		this.updateCurrentIntensity(newTime);

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("executing event: ");
			sb.append(ce.eventAsString());
			sb.append(" at ");
			sb.append(newTime.getSimulatedTime());
			sb.append("; I=");
			sb.append(this.currentIntensity.getValue());
			sb.append(" A");
			this.logMessage(sb.toString());
		}
		this.recordTrace("external");

		super.userDefinedExternalTransition(elapsedTime);

		assert	implementationInvariants(this) :
				new NeoSim4JavaException(
					"RefrigeratorElectricityModel.implementationInvariants(this)");
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
							TENSION * this.currentIntensity.getValue());
		}

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		this.recordTrace("end");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	public static final String		INITIAL_TEMPERATURE_RPNAME =
									"REFRIGERATOR_INITIAL_TEMPERATURE";
	public static final String		TARGET_TEMPERATURE_RPNAME =
									"REFRIGERATOR_TARGET_TEMPERATURE";

	@Override
	public void			setSimulationRunParameters(Map<String, Object> simParams)
	{
		super.setSimulationRunParameters(simParams);
		if (simParams != null) {
			String initName =
					ModelI.createRunParameterName(
							getURI(), INITIAL_TEMPERATURE_RPNAME);
			if (simParams.containsKey(initName)) {
				this.initialTemperature =
						(Double) simParams.get(initName);
			}
			String targetName =
					ModelI.createRunParameterName(
							getURI(), TARGET_TEMPERATURE_RPNAME);
			if (simParams.containsKey(targetName)) {
				this.targetTemperature =
						(Double) simParams.get(targetName);
			}
		}
	}

	protected void		recordTrace(String phase)
	{
		if (this.controlTrace == null
					|| !this.currentTemperature.isInitialised()
					|| !this.currentIntensity.isInitialised()) {
			return;
		}
		String mode =
				this.currentState == RefrigeratorState.ON ?
							"ACTIVE" : "SUSPENDED";
			StringBuffer sb = new StringBuffer();
			sb.append("t=");
			sb.append(this.currentTemperature.getTime().getSimulatedTime());
			sb.append(", intensity=");
			sb.append(this.currentIntensity.getValue());
			sb.append(", temperature=");
			sb.append(this.currentTemperature.getValue());
			sb.append(", mode=");
			sb.append(mode);
			sb.append(", phase=");
			sb.append(phase);
			this.controlTrace.add(sb.toString());
	}

	public static class		RefrigeratorElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption;
		protected ArrayList<String>	controlTrace;

		public				RefrigeratorElectricityReport(
			String modelURI,
			double totalConsumption,
			ArrayList<String> controlTrace
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
			this.controlTrace = controlTrace;
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
			ret.append("total consumption in kwh = ");
			ret.append(this.totalConsumption);
			ret.append(".\n");
			if (this.controlTrace != null && !this.controlTrace.isEmpty()) {
				ret.append(indent);
				ret.append("|control trace (t,intensity,temperature,mode):\n");
				for (String sample : this.controlTrace) {
					ret.append(indent);
					ret.append("|");
					ret.append(sample);
					ret.append("\n");
				}
			}
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}

		@Override
		public String	toString()
		{
			return this.printout("");
		}
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new RefrigeratorElectricityReport(
						this.getURI(),
						this.totalConsumption,
						this.controlTrace);
	}
}
// -----------------------------------------------------------------------------
