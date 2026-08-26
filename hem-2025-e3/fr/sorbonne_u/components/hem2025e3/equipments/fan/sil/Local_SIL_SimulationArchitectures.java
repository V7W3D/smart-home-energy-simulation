package fr.sorbonne_u.components.hem2025e3.equipments.fan.sil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetMediumSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Local_SIL_SimulationArchitectures</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
public abstract class	Local_SIL_SimulationArchitectures
{
	public static RTArchitecture	createFanSIL_Architecture4UnitTest(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
												new HashMap<>();

		atomicModelDescriptors.put(
				FanElectricitySILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						FanElectricitySILModel.class,
						FanElectricitySILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				FanStateSILModel.URI,
				RTAtomicModelDescriptor.create(
						FanStateSILModel.class,
						FanStateSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
												new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(FanElectricitySILModel.URI);
		submodels.add(FanStateSILModel.URI);

		Map<EventSource,EventSink[]> connections =
											new HashMap<EventSource,EventSink[]>();

		connections.put(
			new EventSource(FanStateSILModel.URI,
							SwitchOnFan.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							SwitchOnFan.class)
			});
		connections.put(
			new EventSource(FanStateSILModel.URI,
							SwitchOffFan.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							SwitchOffFan.class)
			});
		connections.put(
			new EventSource(FanStateSILModel.URI,
							SetLowSpeed.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							SetLowSpeed.class)
			});
		connections.put(
			new EventSource(FanStateSILModel.URI,
							SetMediumSpeed.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							SetMediumSpeed.class)
			});
		connections.put(
			new EventSource(FanStateSILModel.URI,
							SetHighSpeed.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							SetHighSpeed.class)
			});

		coupledModelDescriptors.put(
				rootModelURI,
				new RTCoupledModelDescriptor(
						FanCoupledModel.class,
						rootModelURI,
						submodels,
						null,
						null,
						connections,
						null,
						accelerationFactor));

		RTArchitecture architecture =
				new RTArchitecture(
						architectureURI,
						rootModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit,
						accelerationFactor);

		return architecture;
	}

	public static RTArchitecture	createFanSIL_Architecture4IntegrationTest(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
												new HashMap<>();

		atomicModelDescriptors.put(
				rootModelURI,
				RTAtomicModelDescriptor.create(
						FanStateSILModel.class,
						rootModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
												new HashMap<>();

		RTArchitecture architecture =
				new RTArchitecture(
						architectureURI,
						rootModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit,
						accelerationFactor);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
