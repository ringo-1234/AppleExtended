/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.rtm;

import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.cfg.ConnectorConfig;
import jp.ngt.rtm.modelpack.cfg.ContainerConfig;
import jp.ngt.rtm.modelpack.cfg.FirearmConfig;
import jp.ngt.rtm.modelpack.cfg.FlagConfig;
import jp.ngt.rtm.modelpack.cfg.MachineConfig;
import jp.ngt.rtm.modelpack.cfg.MechanismConfig;
import jp.ngt.rtm.modelpack.cfg.NPCConfig;
import jp.ngt.rtm.modelpack.cfg.OrnamentConfig;
import jp.ngt.rtm.modelpack.cfg.RRSConfig;
import jp.ngt.rtm.modelpack.cfg.RailConfig;
import jp.ngt.rtm.modelpack.cfg.SignalConfig;
import jp.ngt.rtm.modelpack.cfg.SignboardConfig;
import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import jp.ngt.rtm.modelpack.cfg.VehicleConfig;
import jp.ngt.rtm.modelpack.cfg.WireConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetConnector;
import jp.ngt.rtm.modelpack.modelset.ModelSetContainer;
import jp.ngt.rtm.modelpack.modelset.ModelSetFirearm;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import jp.ngt.rtm.modelpack.modelset.ModelSetMechanism;
import jp.ngt.rtm.modelpack.modelset.ModelSetNPC;
import jp.ngt.rtm.modelpack.modelset.ModelSetOrnament;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.modelset.ModelSetSignal;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicle;
import jp.ngt.rtm.modelpack.modelset.ModelSetWire;
import jp.ngt.rtm.modelpack.modelset.TextureSetFlag;
import jp.ngt.rtm.modelpack.modelset.TextureSetRRS;
import jp.ngt.rtm.modelpack.modelset.TextureSetSignboard;

public final class RTMResource
{
	public static ResourceType<FirearmConfig, ModelSetFirearm> FIREARM;
	public static ResourceType<RailConfig, ModelSetRail> RAIL;
	public static ResourceType<SignalConfig, ModelSetSignal> SIGNAL;
	public static ResourceType<ContainerConfig, ModelSetContainer> CONTAINER;
	public static ResourceType<NPCConfig, ModelSetNPC> NPC;
	public static ResourceType<WireConfig, ModelSetWire> WIRE;
	public static ResourceType<SignboardConfig, TextureSetSignboard> SIGNBOARD;
	public static ResourceType<FlagConfig, TextureSetFlag> FLAG;
	public static ResourceType<RRSConfig, TextureSetRRS> RRS;
	public static ResourceType<MechanismConfig, ModelSetMechanism> MECHANISM;

	private static ResourceType<ConnectorConfig, ModelSetConnector> CONNECTOR;
	public static ResourceType<ConnectorConfig, ModelSetConnector> CONNECTOR_RELAY;
	public static ResourceType<ConnectorConfig, ModelSetConnector> CONNECTOR_INPUT;
	public static ResourceType<ConnectorConfig, ModelSetConnector> CONNECTOR_OUTPUT;

	private static ResourceType<MachineConfig, ModelSetMachine> MACHINE;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_GATE;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_POINT;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_TURNSTILE;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_VENDOR;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_LIGHT;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_BUMPINGPOST;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_ANTENNA_SEND;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_ANTENNA_RECEIVE;
	public static ResourceType<MachineConfig, ModelSetMachine> MACHINE_SPEAKER;

	private static ResourceType<OrnamentConfig, ModelSetOrnament> ORNAMENT;
	public static ResourceType<OrnamentConfig, ModelSetOrnament> ORNAMENT_LAMP;
	public static ResourceType<OrnamentConfig, ModelSetOrnament> ORNAMENT_STAIR;
	public static ResourceType<OrnamentConfig, ModelSetOrnament> ORNAMENT_SCAFFOLD;
	public static ResourceType<OrnamentConfig, ModelSetOrnament> ORNAMENT_POLE;
	public static ResourceType<OrnamentConfig, ModelSetOrnament> ORNAMENT_PIPE;
	public static ResourceType<OrnamentConfig, ModelSetOrnament> ORNAMENT_PLANT;

	private static ResourceType<TrainConfig, ModelSetTrain> TRAIN;
	public static ResourceType<TrainConfig, ModelSetTrain> TRAIN_EC;
	public static ResourceType<TrainConfig, ModelSetTrain> TRAIN_DC;
	public static ResourceType<TrainConfig, ModelSetTrain> TRAIN_CC;
	public static ResourceType<TrainConfig, ModelSetTrain> TRAIN_TC;
	public static ResourceType<TrainConfig, ModelSetTrain> TRAIN_TEST;

	private static ResourceType<VehicleConfig, ModelSetVehicle> VEHICLE;
	public static ResourceType<VehicleConfig, ModelSetVehicle> VEHICLE_CAR;
	public static ResourceType<VehicleConfig, ModelSetVehicle> VEHICLE_PLANE;
	public static ResourceType<VehicleConfig, ModelSetVehicle> VEHICLE_SHIP;
	public static ResourceType<VehicleConfig, ModelSetVehicle> VEHICLE_TROLLEY;
	public static ResourceType<VehicleConfig, ModelSetVehicle> VEHICLE_LIFT;

	public static void init()
	{
		FIREARM   = (new ResourceType<>("ModelFirearm",   FirearmConfig.class, ModelSetFirearm.class, false)).setDefault("40cmArtillery");
		RAIL      = (new ResourceType<>("ModelRail",      RailConfig.class, ModelSetRail.class, false)).setDefault("1067mm_Wood");
		SIGNAL    = (new ResourceType<>("ModelSignal",    SignalConfig.class, ModelSetSignal.class, false)).setDefault("4colorB");
		CONTAINER = (new ResourceType<>("ModelContainer", ContainerConfig.class, ModelSetContainer.class, false)).setDefault("19g_JRF_0");
		NPC       = (new ResourceType<>("ModelNPC",       NPCConfig.class, ModelSetNPC.class, false)).setDefault("MannequinNGT01");
		WIRE      = (new ResourceType<>("ModelWire",      WireConfig.class, ModelSetWire.class, false)).setDefault("BasicWireBlack");
		SIGNBOARD = (new ResourceType<>("SignBoard",      SignboardConfig.class, TextureSetSignboard.class, false)).setDefault("textures/signboard/ngt_a01.png");
		FLAG      = (new ResourceType<>("Flag",           FlagConfig.class, TextureSetFlag.class, false)).setDefault("textures/flag/flag_RTM3Anniversary.png");
		RRS       = (new ResourceType<>("RRS",            RRSConfig.class, TextureSetRRS.class, false)).setCustomLoading(true).setDefault("textures/rrs/rrs_01.png");
		MECHANISM = (new ResourceType<>("ModelMechanism", MechanismConfig.class, ModelSetMechanism.class, false)).setDefault("gear_1000mm");

		CONNECTOR = (new ResourceType<>("ModelConnector", ConnectorConfig.class, ModelSetConnector.class, true));
		CONNECTOR_RELAY  = CONNECTOR.getSubType("Relay").setDefault("Insulator01");
		CONNECTOR_INPUT  = CONNECTOR.getSubType("Input").setDefault("Input01");
		CONNECTOR_OUTPUT = CONNECTOR.getSubType("Output").setDefault("Output01");

		MACHINE = (new ResourceType<>("ModelMachine", MachineConfig.class, ModelSetMachine.class, true));
		MACHINE_GATE        = MACHINE.getSubType("Gate").setDefault("CrossingGate01L");
		MACHINE_POINT       = MACHINE.getSubType("Point").setDefault("Point01M");
		MACHINE_TURNSTILE   = MACHINE.getSubType("Turnstile").setDefault("Turnstile01");
		MACHINE_VENDOR      = MACHINE.getSubType("Vendor").setDefault("Vendor01");
		MACHINE_LIGHT       = MACHINE.getSubType("Light").setDefault("SearchLight01");
		MACHINE_BUMPINGPOST = MACHINE.getSubType("BumpingPost").setDefault("BumpingPost_Type2");
		MACHINE_ANTENNA_SEND    = MACHINE.getSubType("Antenna_Send").setDefault("ATC_01");
		MACHINE_ANTENNA_RECEIVE = MACHINE.getSubType("Antenna_Receive").setDefault("TrainDetector_01");
		MACHINE_SPEAKER     = MACHINE.getSubType("Speaker").setDefault("Speaker01");

		ORNAMENT = (new ResourceType<>("ModelOrnament", OrnamentConfig.class, ModelSetOrnament.class, true));
		ORNAMENT_LAMP     = ORNAMENT.getSubType("Lamp").setDefault("Fluorescent01");
		ORNAMENT_STAIR    = ORNAMENT.getSubType("Stair").setDefault("ScaffoldStair01");
		ORNAMENT_SCAFFOLD = ORNAMENT.getSubType("Scaffold").setDefault("Scaffold01");
		ORNAMENT_POLE     = ORNAMENT.getSubType("Pole").setDefault("LinePole01");
		ORNAMENT_PIPE     = ORNAMENT.getSubType("Pipe").setDefault("Pipe01");
		ORNAMENT_PLANT    = ORNAMENT.getSubType("Plant").setDefault("Tree01");

		TRAIN = (new ResourceType<>("ModelTrain", TrainConfig.class, ModelSetTrain.class, true));
		TRAIN_EC = TRAIN.getSubType("EC").setDefault("223-2000_M");
		TRAIN_DC = TRAIN.getSubType("DC").setDefault("kiha600");
		TRAIN_CC = TRAIN.getSubType("CC").setDefault("koki100");
		TRAIN_TC = TRAIN.getSubType("TC").setDefault("torpedo-car");
		TRAIN_TEST = TRAIN.getSubType("Test").setDefault("objTest");

		VEHICLE = (new ResourceType<>("ModelVehicle", VehicleConfig.class, ModelSetVehicle.class, true));
		VEHICLE_CAR   = VEHICLE.getSubType("Car").setDefault("CV33");
		VEHICLE_PLANE = VEHICLE.getSubType("Plane").setDefault("NGT-1");
		VEHICLE_SHIP  = VEHICLE.getSubType("Ship").setDefault("WoodBoat");
		VEHICLE_TROLLEY = VEHICLE.getSubType("Trolley").setDefault("trolley");
		VEHICLE_LIFT = VEHICLE.getSubType("Lift").setDefault("lift_1seat");
	}
}