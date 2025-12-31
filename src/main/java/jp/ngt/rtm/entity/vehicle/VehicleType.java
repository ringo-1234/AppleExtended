package jp.ngt.rtm.entity.vehicle;

import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;

public enum VehicleType
{
	CAR(0, RTMResource.VEHICLE_CAR),
	SHIP(1, RTMResource.VEHICLE_SHIP),
	PLANE(2, RTMResource.VEHICLE_PLANE),
	TROLLEY(3, RTMResource.VEHICLE_TROLLEY),
	LIFT(4, RTMResource.VEHICLE_LIFT);

	public final int id;
	public final ResourceType type;

	private VehicleType(int p1, ResourceType p2)
	{
		this.id = p1;
		this.type = p2;
	}
}
