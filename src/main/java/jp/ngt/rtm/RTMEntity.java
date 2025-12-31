package jp.ngt.rtm;

import jp.ngt.ngtlib.util.IMod;
import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.entity.EntityATC;
import jp.ngt.rtm.entity.EntityBullet;
import jp.ngt.rtm.entity.EntityBumpingPost;
import jp.ngt.rtm.entity.EntityMMBoundingBox;
import jp.ngt.rtm.entity.EntityTrainDetector;
import jp.ngt.rtm.entity.RenderBullet;
import jp.ngt.rtm.entity.RenderEntityInstalledObject;
import jp.ngt.rtm.entity.RenderMMBB;
import jp.ngt.rtm.entity.fluid.EntityFluid;
import jp.ngt.rtm.entity.fluid.RenderFluid;
import jp.ngt.rtm.entity.npc.EntityMotorman;
import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.entity.npc.RenderNPC;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityFreightCar;
import jp.ngt.rtm.entity.train.EntityTanker;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.EntityTrainDieselCar;
import jp.ngt.rtm.entity.train.EntityTrainElectricCar;
import jp.ngt.rtm.entity.train.EntityTrainTest;
import jp.ngt.rtm.entity.train.RenderBogie;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import jp.ngt.rtm.entity.train.parts.EntityContainer;
import jp.ngt.rtm.entity.train.parts.EntityFloor;
import jp.ngt.rtm.entity.train.parts.EntityTie;
import jp.ngt.rtm.entity.train.parts.RenderArtillery;
import jp.ngt.rtm.entity.train.parts.RenderContainer;
import jp.ngt.rtm.entity.train.parts.RenderSeat;
import jp.ngt.rtm.entity.train.parts.RenderTie;
import jp.ngt.rtm.entity.vehicle.EntityCar;
import jp.ngt.rtm.entity.vehicle.EntityLift;
import jp.ngt.rtm.entity.vehicle.EntityPlane;
import jp.ngt.rtm.entity.vehicle.EntityShip;
import jp.ngt.rtm.entity.vehicle.EntityTrolley;
import jp.ngt.rtm.entity.vehicle.EntityVehicle;
import jp.ngt.rtm.entity.vehicle.RenderTrolley;
import jp.ngt.rtm.entity.vehicle.RenderVehicleBase;
import jp.ngt.rtm.entity.vehicle.RenderWeatherEffectDummy;
import jp.ngt.rtm.entity.vehicle.WeatherEffectDummy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class RTMEntity
{
	public static final byte FREQ_VEHICLE = 3;
	public static final byte FREQ_INSTALLED = 10;

	private static short nextId;
	private static final short RANGE = 1024;//trackingRange->EntityTrackerで設定される

	public static void init(IMod mod)
	{
		NGTRegHandler.register(EntityBullet.class, "bullet", "RTM.E.Bullet", getNextId(), 256, 3, true, mod);
		NGTRegHandler.register(EntityMMBoundingBox.class, "mmbb", "RTM.E.MMBB", getNextId(), 160, Integer.MAX_VALUE, false, mod);
		NGTRegHandler.register(EntityFluid.class, "fluid", "RTM.E.Fluid", getNextId(), 256, 3, true, mod);

		NGTRegHandler.register(EntityMotorman.class, "motorman", "RTM.E.Motorman", getNextId(), RANGE, 3, true, mod);
		NGTRegHandler.register(EntityNPC.class, "npc", "RTM.E.NPC", getNextId(), RANGE, 3, true, mod);

		NGTRegHandler.register(EntityATC.class, "atc", "RTM.E.ATC", getNextId(), 160, FREQ_INSTALLED, false, mod);
		NGTRegHandler.register(EntityTrainDetector.class, "train_detector", "RTM.E.TrainDetector", getNextId(), 160, FREQ_INSTALLED, false, mod);
		NGTRegHandler.register(EntityBumpingPost.class, "bumping_post", "RTM.E.BumpingPost", getNextId(), 160, FREQ_INSTALLED, false, mod);

		NGTRegHandler.register(EntityFloor.class, "floor", "RTM.E.Floor", getNextId(), RANGE, FREQ_VEHICLE, false, mod);
		NGTRegHandler.register(EntityBogie.class, "bogie", "RTM.E.Bogie", getNextId(), RANGE, FREQ_VEHICLE, false, mod);
		NGTRegHandler.register(EntityContainer.class, "container", "RTM.E.Container", getNextId(), 160, FREQ_VEHICLE, false, mod);
		NGTRegHandler.register(EntityArtillery.class, "artillery", "RTM.E.Artillery", getNextId(), 160, FREQ_VEHICLE, false, mod);
		NGTRegHandler.register(EntityTie.class, "tie", "RTM.E.Tie", getNextId(), 160, FREQ_VEHICLE, false, mod);

		NGTRegHandler.register(EntityCar.class, "car", "RTM.E.Car", getNextId(), 160, FREQ_VEHICLE, true, mod);
		NGTRegHandler.register(EntityShip.class, "ship", "RTM.E.Ship", getNextId(), 160, FREQ_VEHICLE, true, mod);
		NGTRegHandler.register(EntityPlane.class, "plane", "RTM.E.Plane", getNextId(), 160, FREQ_VEHICLE, true, mod);
		NGTRegHandler.register(EntityTrolley.class, "trolley", "RTM.E.Trolley", getNextId(), 160, FREQ_VEHICLE, true, mod);
		NGTRegHandler.register(EntityLift.class, "lift", "RTM.E.Lift", getNextId(), 160, FREQ_VEHICLE, true, mod);

		registerTrain(EntityTrainElectricCar.class, "electric_car", "RTM.E.ElectricCar", mod);
		registerTrain(EntityTrainDieselCar.class, "diesel_car", "RTM.E.DieselCar", mod);
		registerTrain(EntityTrainTest.class, "test_car", "RTM.E.TrainTest", mod);
		registerTrain(EntityFreightCar.class, "freight_car", "RTM.E.FreightCar", mod);
		registerTrain(EntityTanker.class, "tanker", "RTM.E.Tanker", mod);
	}

	private static void registerTrain(Class<? extends EntityTrainBase> clazz, String regName, String name, IMod mod)
	{
		NGTRegHandler.register(clazz, regName, name, getNextId(), RANGE, FREQ_VEHICLE, false, mod);
	}

	@SideOnly(Side.CLIENT)
	public static void initClient()
	{
		NGTUtilClient.registerEntityRender(EntityVehicle.class,       RenderVehicleBase.class);
		NGTUtilClient.registerEntityRender(EntityTrainBase.class,     RenderVehicleBase.class);
		NGTUtilClient.registerEntityRender(WeatherEffectDummy.class,  RenderWeatherEffectDummy.class);
		NGTUtilClient.registerEntityRender(EntityBogie.class,         RenderBogie.class);
		NGTUtilClient.registerEntityRender(EntityFloor.class,         RenderSeat.class);
		NGTUtilClient.registerEntityRender(EntityATC.class,           RenderEntityInstalledObject.class);
    	NGTUtilClient.registerEntityRender(EntityTrainDetector.class, RenderEntityInstalledObject.class);
    	NGTUtilClient.registerEntityRender(EntityBumpingPost.class,   RenderEntityInstalledObject.class);
    	NGTUtilClient.registerEntityRender(EntityContainer.class,     RenderContainer.class);
    	NGTUtilClient.registerEntityRender(EntityArtillery.class,     RenderArtillery.class);
    	NGTUtilClient.registerEntityRender(EntityBullet.class,        RenderBullet.class);
    	NGTUtilClient.registerEntityRender(EntityTie.class,           RenderTie.class);
    	NGTUtilClient.registerEntityRender(EntityMMBoundingBox.class, RenderMMBB.class);
    	NGTUtilClient.registerEntityRender(EntityNPC.class,           RenderNPC.class);
    	NGTUtilClient.registerEntityRender(EntityFluid.class,         RenderFluid.class);
    	NGTUtilClient.registerEntityRender(EntityTrolley.class,       RenderTrolley.class);
	}

	public static int getNextId()
	{
		return nextId++;
	}
}