package net.lanzr.itemkit;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TrashCanMenu extends AbstractContainerMenu {
    private final Container trashContainer;

    // Client-side constructor (used by MenuType factory)
    public TrashCanMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(54));
    }

    // Server-side constructor
    public TrashCanMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuType.GENERIC_9x6, containerId);
        this.trashContainer = container;
        checkContainerSize(container, 54);
        container.startOpen(playerInventory.player);

        // Trash slots: 6 rows x 9 columns
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player inventory: 3 rows x 9 columns
        int playerInvY = 139;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, playerInvY + row * 18));
            }
        }

        // Player hotbar: 1 row x 9 columns
        int hotbarY = playerInvY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        // Can't shift-click items out of trash
        if (slotIndex < 54) return ItemStack.EMPTY;

        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();

        // Move from player inventory to trash (first empty slot)
        if (!this.moveItemStackTo(stack, 0, 54, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        }

        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Delete all items when the UI is closed
        trashContainer.clearContent();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
