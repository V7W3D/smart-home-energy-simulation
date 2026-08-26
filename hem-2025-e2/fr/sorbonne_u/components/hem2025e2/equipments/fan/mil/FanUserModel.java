package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

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
import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetMediumSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUserModel</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
@ModelExternalEvents(exported = {SwitchOnFan.class,
							 SwitchOffFan.class,
							 SetLowSpeed.class,
							 SetMediumSpeed.class,
							 SetHighSpeed.class})
// -----------------------------------------------------------------------------
public class			FanUserModel
extends		AtomicES_Model
{
	private static final long	serialVersionUID = 1L;
	public static final String	URI = FanUserModel.class.getSimpleName();
	public static boolean		VERBOSE = true;
	public static boolean		DEBUG = false;

	protected static double		STEP_MEAN_DURATION = 5.0/60.0; // 5 minutes
	protected static double		DELAY_MEAN_DURATION = 4.0;

	protected final RandomDataGenerator	rg;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	staticImplementationInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				STEP_MEAN_DURATION > 0.0,
				FanUserModel.class,
				"STEP_MEAN_DURATION > 0.0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				DELAY_MEAN_DURATION > 0.0,
				FanUserModel.class,
				"DELAY_MEAN_DURATION > 0.0");
		return ret;
	}

	protected static boolean	implementationInvariants(FanUserModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= staticImplementationInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.rg != null,
				FanUserModel.class,
				instance,
				"rg != null");
		return ret;
	}

	protected static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanUserModel.class,
				"URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				MEAN_STEP_RPNAME != null && !MEAN_STEP_RPNAME.isEmpty(),
				FanUserModel.class,
				"MEAN_STEP_RPNAME != null && !MEAN_STEP_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				MEAN_DELAY_RPNAME != null && !MEAN_DELAY_RPNAME.isEmpty(),
				FanUserModel.class,
				"MEAN_DELAY_RPNAME != null && !MEAN_DELAY_RPNAME.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(FanUserModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator();
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanUserModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanUserModel.implementationInvariants(this)");
		assert	FanUserModel.invariants(this) :
				new NeoSim4JavaException("FanUserModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	protected void		generateNextEvent()
	{
		EventI current = this.eventList.peek();
		ES_EventI nextEvent = null;
		if (current instanceof SwitchOffFan) {
			Time t2 = this.computeTimeOfNextUsage(current.getTimeOfOccurrence());
			nextEvent = new SwitchOnFan(t2);
		} else {
			Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
			if (current instanceof SwitchOnFan) {
				nextEvent = new SetMediumSpeed(t);
			} else if (current instanceof SetMediumSpeed) {
				nextEvent = new SetHighSpeed(t);
			} else if (current instanceof SetHighSpeed) {
				nextEvent = new SetLowSpeed(t);
			} else if (current instanceof SetLowSpeed) {
				nextEvent = new SwitchOffFan(t);
			}
		}
		this.scheduleEvent(nextEvent);
	}

	protected Time		computeTimeOfNextEvent(Time from)
	{
		assert	from != null;

		double delay = Math.max(
				this.rg.nextGaussian(STEP_MEAN_DURATION,
									 STEP_MEAN_DURATION/2.0),
				0.1);
		return from.add(new Duration(delay, this.getSimulatedTimeUnit()));
	}

	protected Time		computeTimeOfNextUsage(Time from)
	{
		assert	from != null;

		double delay = Math.max(
				this.rg.nextGaussian(DELAY_MEAN_DURATION,
									 DELAY_MEAN_DURATION/10.0),
				0.1);
		return from.add(new Duration(delay, this.getSimulatedTimeUnit()));
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.rg.reSeedSecure();

		Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
		this.scheduleEvent(new SwitchOnFan(t));
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance());

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}
	}

	@Override
	public ArrayList<EventI>	output()
	{
		if (this.eventList.peek() != null) {
			this.generateNextEvent();
		}
		return super.output();
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	public static final String		MEAN_STEP_RPNAME = "STEP_MEAN_DURATION";
	public static final String		MEAN_DELAY_RPNAME = "DELAY_MEAN_DURATION";

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String stepName =
				ModelI.createRunParameterName(getURI(), MEAN_STEP_RPNAME);
		if (simParams.containsKey(stepName)) {
			STEP_MEAN_DURATION = (double) simParams.get(stepName);
		}
		String delayName =
				ModelI.createRunParameterName(getURI(), MEAN_DELAY_RPNAME);
		if (simParams.containsKey(delayName)) {
			DELAY_MEAN_DURATION = (double) simParams.get(delayName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
// -----------------------------------------------------------------------------
