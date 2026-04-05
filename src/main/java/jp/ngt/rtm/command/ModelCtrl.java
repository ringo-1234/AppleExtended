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

package jp.ngt.rtm.command;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityArtillery;
import jp.ngt.rtm.entity.train.util.TrainState;
import jp.ngt.rtm.entity.train.util.TrainState.TrainStateType;
import jp.ngt.rtm.entity.vehicle.EntityVehicle;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.item.ItemAmmunition.BulletType;
import jp.ngt.rtm.modelpack.IResourceSelector;
import net.minecraft.command.ICommandSender;

public enum ModelCtrl {
    NOTCH(
            (s) -> {
                return s.equals("notch");
            },
            (obj) -> {
                return obj instanceof EntityTrainBase;
            },
            (target, player, order, value) -> {
                int notch = Integer.valueOf(value);
                return ((EntityTrainBase) target).setNotch(notch);
            },
            "mctrl <train> notch <-8 ~ 5>"),
    DIR(
            (s) -> {
                return s.equals("dir");
            },
            (obj) -> {
                return obj instanceof EntityTrainBase;
            },
            (target, player, order, value) -> {
                int dir = Integer.valueOf(value);
                ((EntityTrainBase) target).setTrainDirection(dir);
                return true;
            },
            "mctrl <train> dir <0 or 1>"),
    DATA_MAP(
            (s) -> {
                return s.startsWith("dm:");
            },
            (obj) -> {
                return obj instanceof IResourceSelector;
            },
            (target, player, order, value) -> {
                String dataName = order.replace("dm:", "");
                if (!((IResourceSelector) target).getResourceState().getDataMap().set(dataName, value, 3)) {
                    NGTLog.sendChatMessage(player, "[" + dataName + "] is not key.");
                    return false;
                }
                return true;
            },
            "mctrl <?> dm:<data name> <(type)value>"),
    VEHICLE_STATE(
            (s) -> {
                return s.startsWith("state:");
            },
            (obj) -> {
                return obj instanceof EntityVehicleBase;
            },
            (target, player, order, value) -> {
                String dataName = order.replace("state:", "");
                try {
                    TrainState state = TrainState.valueOf(value);
                    TrainStateType type = TrainStateType.valueOf(dataName);
                    ((EntityVehicleBase) target).setVehicleState(type, state.data);
                    return true;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    NGTLog.sendChatMessage(player, "Illegal argument.");
                    return false;
                }
            },
            "mctrl <vehicle> state:<data name> <value>"),
    V_MOV_DIST(
            (s) -> {
                return s.equals("move");
            },
            (obj) -> {
                return obj instanceof EntityVehicle;
            },
            (target, player, order, value) -> {
                EntityVehicle vehicle = ((EntityVehicle) target);
                double dist = Double.valueOf(value);
                vehicle.controller.setMoveDistance(vehicle, dist);
                return true;
            },
            "mctrl <vehicle> move <distance>"),
    V_ADD_YAW(
            (s) -> {
                return s.equals("addYaw");
            },
            (obj) -> {
                return (obj instanceof EntityVehicle) || (obj instanceof EntityArtillery);
            },
            (target, player, order, value) -> {
                if (target instanceof EntityArtillery) {
                    EntityArtillery firearm = ((EntityArtillery) target);
                    float yaw = Float.valueOf(value);
                    firearm.controller.addYaw(firearm, yaw);
                } else {
                    EntityVehicle vehicle = ((EntityVehicle) target);
                    float yaw = Float.valueOf(value);
                    vehicle.controller.addYaw(vehicle, yaw);
                }
                return true;
            },
            "mctrl <vehicle or artillery> addYaw <value>"),
    V_ADD_PITCH(
            (s) -> {
                return s.equals("addPitch");
            },
            (obj) -> {
                return (obj instanceof EntityVehicle) || (obj instanceof EntityArtillery);
            },
            (target, player, order, value) -> {
                if (target instanceof EntityArtillery) {
                    EntityArtillery firearm = ((EntityArtillery) target);
                    float yaw = Float.valueOf(value);
                    firearm.controller.addPitch(firearm, -yaw);
                } else {
                    //EntityVehicle vehicle =((EntityVehicle)target);
                    //float yaw = Float.valueOf(value);
                    //vehicle.controller.addPitch(vehicle, yaw);
                }
                return true;
            },
            "mctrl <artillery> addPitch <value>"),
    FIRE(
            (s) -> {
                return s.equals("fire");
            },
            (obj) -> {
                return obj instanceof EntityArtillery;
            },
            (target, player, order, value) -> {
                EntityArtillery firearm = ((EntityArtillery) target);
                BulletType type = BulletType.getBulletType(firearm.getResourceState().getResourceSet().getConfig().ammoType);
                int count = Integer.valueOf(value);
                firearm.fire(null, type, count);
                return true;
            },
            "mctrl <artillery> fire <number of bullet>"),
    NO_FUNC((s) -> {
        return false;
    }, (obj) -> {
        return false;
    }, (target, player, order, value) -> {
        return false;
    }, "");

    public final CommandMatcher matcher;
    public final TargetFilter filter;
    public final CommandExecutor executor;
    public final String discription;

    private ModelCtrl(CommandMatcher par1, TargetFilter par2, CommandExecutor par3, String par4) {
        this.matcher = par1;
        this.filter = par2;
        this.executor = par3;
        this.discription = par4;
    }

    public static ModelCtrl getCommand(String par1) {
        for (ModelCtrl ctrl : ModelCtrl.values()) {
            if (ctrl.matcher.match(par1)) {
                return ctrl;
            }
        }
        return ModelCtrl.NO_FUNC;
    }

    public interface CommandMatcher {
        boolean match(String s);
    }

    public interface TargetFilter {
        boolean match(Object obj);
    }

    public interface CommandExecutor {
        boolean exec(Object target, ICommandSender player, String order, String value);
    }
}