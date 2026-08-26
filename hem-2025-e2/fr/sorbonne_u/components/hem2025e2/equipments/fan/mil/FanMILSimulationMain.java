package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetMediumSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e2.equipments.meter.mil.ElectricMeterElectricityModel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanMILSimulationMain</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
public class			FanMILSimulationMain
{
	public static void	main(String[] args)
	{
		FanSimulationConfigurationI.staticInvariants();
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
													new HashMap<>();

			atomicModelDescriptors.put(
					FanElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							FanElectricityModel.class,
							FanElectricityModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					FanUserModel.URI,
					AtomicModelDescriptor.create(
							FanUserModel.class,
							FanUserModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					ElectricMeterElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							ElectricMeterElectricityModel.class,
							ElectricMeterElectricityModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					FanMeterInputStubModel.URI,
					AtomicHIOA_Descriptor.create(
							FanMeterInputStubModel.class,
							FanMeterInputStubModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
													new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(FanElectricityModel.URI);
			submodels.add(FanUserModel.URI);
			submodels.add(ElectricMeterElectricityModel.URI);
			submodels.add(FanMeterInputStubModel.URI);

			Map<EventSource,EventSink[]> connections =
								new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(FanUserModel.URI, SwitchOnFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SwitchOnFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SwitchOffFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SwitchOffFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SetLowSpeed.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SetLowSpeed.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SetMediumSpeed.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SetMediumSpeed.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SetHighSpeed.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SetHighSpeed.class)
					});

			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			VariableSource source =
					new VariableSource("currentIntensity", Double.class,
									  FanElectricityModel.URI);
			VariableSink[] sinks =
					new VariableSink[] {
							new VariableSink("currentFanIntensity", Double.class,
											 ElectricMeterElectricityModel.URI)
					};
			bindings.put(source, sinks);

			source = new VariableSource("batteriesInputPower", Double.class,
								FanMeterInputStubModel.URI);
			sinks = new VariableSink[] {
					new VariableSink("batteriesInputPower", Double.class,
										 ElectricMeterElectricityModel.URI)
			};
			bindings.put(source, sinks);

			source = new VariableSource("batteriesOutputPower", Double.class,
								FanMeterInputStubModel.URI);
			sinks = new VariableSink[] {
					new VariableSink("batteriesOutputPower", Double.class,
										 ElectricMeterElectricityModel.URI)
			};
			bindings.put(source, sinks);

			source = new VariableSource("solarPanelOutputPower", Double.class,
								FanMeterInputStubModel.URI);
			sinks = new VariableSink[] {
					new VariableSink("solarPanelOutputPower", Double.class,
										 ElectricMeterElectricityModel.URI)
			};
			bindings.put(source, sinks);

			source = new VariableSource("generatorOutputPower", Double.class,
								FanMeterInputStubModel.URI);
			sinks = new VariableSink[] {
					new VariableSink("generatorOutputPower", Double.class,
										 ElectricMeterElectricityModel.URI)
			};
			bindings.put(source, sinks);

			source = new VariableSource("currentHeaterIntensity", Double.class,
								FanMeterInputStubModel.URI);
			sinks = new VariableSink[] {
					new VariableSink("currentHeaterIntensity", Double.class,
										 ElectricMeterElectricityModel.URI)
			};
			bindings.put(source, sinks);

			source = new VariableSource("currentHairDryerIntensity", Double.class,
								FanMeterInputStubModel.URI);
			sinks = new VariableSink[] {
					new VariableSink("currentHairDryerIntensity", Double.class,
										 ElectricMeterElectricityModel.URI)
			};
			bindings.put(source, sinks);

			source = new VariableSource("currentRefrigeratorIntensity", Double.class,
							FanMeterInputStubModel.URI);
			sinks = new VariableSink[] {
					new VariableSink("currentRefrigeratorIntensity", Double.class,
									 ElectricMeterElectricityModel.URI)
			};
			bindings.put(source, sinks);

			coupledModelDescriptors.put(
					FanCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							FanCoupledModel.class,
							FanCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings));

			ArchitectureI architecture =
					new Architecture(
							FanCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							FanSimulationConfigurationI.TIME_UNIT);

			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

			se.doStandAloneSimulation(0.0, 100.0/60.0);
			SimulationReportI sr = se.getSimulatedModel().getFinalReport();
			System.out.println(sr);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
