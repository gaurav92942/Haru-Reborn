package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.ModuleInfo;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

@ModuleInfo(name = "NoFall", category = Category.Player)
public class NoFall extends Module {
	private boolean handling;

	@EventLink
	public void onTick(TickEvent e) {
		if (PlayerUtil.inGame() && !mc.isGamePaused()) {
			if (inNether())
				this.disable();

			if (this.inPosition() && this.holdWaterBucket()) {
				this.handling = true;
			}

			if (this.handling) {
				this.mlg();
				if (mc.thePlayer.onGround || mc.thePlayer.motionY > 0.0D) {
					this.reset();
				}
			}
		}
	}

	private boolean inPosition() {
		if (mc.thePlayer.motionY < -0.6D && !mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying
				&& !mc.thePlayer.capabilities.isCreativeMode && !this.handling) {
			BlockPos playerPos = mc.thePlayer.getPosition();

			for (int i = 1; i < 3; ++i) {
				BlockPos blockPos = playerPos.down(i);
				Block block = mc.theWorld.getBlockState(blockPos).getBlock();
				if (block.isBlockSolid(mc.theWorld, blockPos, EnumFacing.UP)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean holdWaterBucket() {
		if (this.containsItem(mc.thePlayer.getHeldItem(), Items.water_bucket)) {
			return true;
		} else {
			for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
				if (this.containsItem(mc.thePlayer.inventory.mainInventory[i], Items.water_bucket)) {
					mc.thePlayer.inventory.currentItem = i;
					return true;
				}
			}
			return false;
		}
	}

	private void mlg() {
		ItemStack heldItem = mc.thePlayer.getHeldItem();
		if (this.containsItem(heldItem, Items.water_bucket) && mc.thePlayer.rotationPitch >= 70.0F) {
			MovingObjectPosition object = mc.objectMouseOver;
			if (object.typeOfHit == MovingObjectType.BLOCK && object.sideHit == EnumFacing.UP) {
				mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
			}
		}
	}

	private void reset() {
		ItemStack heldItem = mc.thePlayer.getHeldItem();
		if (this.containsItem(heldItem, Items.bucket)) {
			mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
		}

		this.handling = false;
	}

	private boolean containsItem(ItemStack itemStack, Item item) {
		return itemStack != null && itemStack.getItem() == item;
	}

	private boolean inNether() {
		if (!PlayerUtil.inGame())
			return false;
		return (mc.thePlayer.dimension == -1);
	}
}
