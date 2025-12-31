package jp.ngt.rtm;

import jp.ngt.ngtlib.util.Usage;
import jp.ngt.rtm.electric.SignalConverterType;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import jp.ngt.rtm.entity.vehicle.EntityVehicle;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;

public final class RTMTooltip
{
	public static final String TICKET_REMAINING = "tooltip.item.ticket.remaining";
	public static final String TICKET_ENTERED = "tooltip.item.ticket.entered";
	public static final String WRENCH_PREFIX = "tooltip.item.wrench.prefix";
	public static final String WRENCH_MODE = "tooltip.item.wrench.mode_";

	public static void init()
	{
		//Entity系を先に登録すること(判定順的に)
		Usage.INSTANCE.add(EntityTrainBase.class, "usage.entity.train");
		Usage.INSTANCE.add(EntityVehicle.class, "usage.entity.vehicle");
		Usage.INSTANCE.add(EntityArtillery.class, "usage.entity.artillery");

		Usage.INSTANCE.add(RTMBlock.movingMachine, 0, "usage.block.moving_machine.1", "usage.block.moving_machine.2");
		Usage.INSTANCE.add(RTMBlock.movingMachine, 1, "usage.block.moving_machine.1", "usage.block.vehicle_generator.2");
		Usage.INSTANCE.add(RTMBlock.marker, -1, "usage.block.marker.put", "usage.block.marker.setting");
		Usage.INSTANCE.add(RTMBlock.markerSwitch, -1,
				"usage.block.marker_switch.1",
				"usage.block.marker_switch.2",
				"usage.block.marker_switch.3",
				"usage.block.marker_switch.4",
				"usage.block.marker_switch.5");
		Usage.INSTANCE.add(RTMBlock.signalConverter, SignalConverterType.RSIn.id,      "usage.block.signal_converter.rs_in");
		Usage.INSTANCE.add(RTMBlock.signalConverter, SignalConverterType.RSOut.id,     "usage.block.signal_converter.rs_out");
		Usage.INSTANCE.add(RTMBlock.signalConverter, SignalConverterType.Increment.id, "usage.block.signal_converter.increment");
		Usage.INSTANCE.add(RTMBlock.signalConverter, SignalConverterType.Decrement.id, "usage.block.signal_converter.decrement");
		Usage.INSTANCE.add(RTMBlock.signalConverter, SignalConverterType.Wireless.id,  "usage.block.signal_converter.wireless");
		Usage.INSTANCE.add(RTMBlock.trainWorkBench, 0, "usage.block.rtm_workbench");
		Usage.INSTANCE.add(RTMBlock.trainWorkBench, 1, "usage.block.rail_workbench");
		Usage.INSTANCE.add(RTMBlock.slot, -1, "usage.block.slot");

		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.FLUORESCENT.id,    "usage.item.istlobj.fluorescent");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.PLANT.id,          "usage.item.istlobj.plant");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.INSULATOR.id,      "usage.item.istlobj.insulator");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.PIPE.id,           "usage.item.istlobj.pipe");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.CROSSING.id,       "usage.item.istlobj.crossing");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.RAILLOAD_SIGN.id,  "usage.item.istlobj.rrs");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.SIGNAL.id,         "usage.item.istlobj.signal");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.CONNECTOR_IN.id,   "usage.item.istlobj.connector_in");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.CONNECTOR_OUT.id,  "usage.item.istlobj.connector_out");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.ATC.id,            "usage.item.istlobj.atc");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.TRAIN_DETECTOR.id, "usage.item.istlobj.train_detector");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.TURNSTILE.id,      "usage.item.istlobj.turnstile");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.BUMPING_POST.id,   "usage.item.istlobj.bumping_post");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.LINEPOLE.id,       "usage.item.istlobj.linepole");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.POINT.id,          "usage.item.istlobj.point");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.SIGNBOARD.id,      "usage.item.istlobj.signboard");
		//Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.TICKET_VENDOR.id, "usage.item.istlobj.ticket_vendor");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.LIGHT.id,          "usage.item.istlobj.light");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.FLAG.id,           "usage.item.istlobj.flag");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.STAIR.id,          "usage.item.istlobj.stair");
		Usage.INSTANCE.add(RTMItem.installedObject, IstlObjType.SCAFFOLD.id,       "usage.item.istlobj.scaffold");
		Usage.INSTANCE.add(RTMItem.itemtrain, -1,
				"usage.item.train.put",
				"usage.item.train.select",
				"usage.item.train.ride",
				"usage.item.train.getoff",
				"usage.item.train.menu",
				"usage.item.train.acceleration",
				"usage.item.train.decceleration",
				"usage.item.train.horn",
				"usage.item.train.chime",
				"usage.item.train.ats");
		Usage.INSTANCE.add(RTMItem.itemMotorman, 0, "usage.item.motorman");
		Usage.INSTANCE.add(RTMItem.itemMotorman, 1, "usage.item.npc");
		Usage.INSTANCE.add(RTMItem.itemCargo, 0, "usage.item.cargo.container");
		Usage.INSTANCE.add(RTMItem.itemCargo, 1, "usage.item.cargo.firearm");
		Usage.INSTANCE.add(RTMItem.itemCargo, 2, "usage.item.cargo.tie");
		Usage.INSTANCE.add(RTMItem.itemLargeRail, -1, "usage.item.rail");
		Usage.INSTANCE.add(RTMItem.itemVehicle, 0, "usage.item.vehicle.car.1", "usage.item.vehicle.car.2");
		Usage.INSTANCE.add(RTMItem.itemVehicle, 1, "usage.item.vehicle.ship.1", "usage.item.vehicle.ship.2");
		Usage.INSTANCE.add(RTMItem.itemVehicle, 2, "usage.item.vehicle.plane.1", "usage.item.vehicle.plane.2");
		Usage.INSTANCE.add(RTMItem.itemWire, -1, "usage.item.wire");
		//Usage.INSTANCE.add(RTMItem.ticket, -1, "usage.item.ticket");
		//Usage.INSTANCE.add(RTMItem.ticketBook, -1, "usage.item.ticket_book");
		//Usage.INSTANCE.add(RTMItem.icCard, -1, "usage.item.ic_card");
		//Usage.INSTANCE.add(RTMItem.crowbar, -1, "usage.item.crowbar");
		//Usage.INSTANCE.add(RTMItem.wrench, -1, "usage.item.wrench");
		Usage.INSTANCE.add(RTMItem.paintTool, -1, "usage.item.paint_tool");
	}
}