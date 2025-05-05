package com.raizunne.redstonic.Util;

import com.raizunne.redstonic.RedstonicItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NBTShapedOreRecipe extends ShapedOreRecipe {
    private final Set<Item> allowedSources;
    private final Set<String> allowedTags;

    public NBTShapedOreRecipe(ItemStack result, Object... recipe) {
        super(result, recipe);
        this.allowedSources = new HashSet<>();
        this.allowedTags = new HashSet<>();
    }

    public NBTShapedOreRecipe allowNBTFrom(Item... items) {
        Collections.addAll(this.allowedSources, items);
        return this;
    }

    public NBTShapedOreRecipe allowTags(String... tags) {
        Collections.addAll(this.allowedTags, tags);
        return this;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting) {
        ItemStack result = super.getCraftingResult(crafting);

        for (int i = 0; i < crafting.getSizeInventory(); i++) {
            ItemStack input = crafting.getStackInSlot(i);
            if (input != null && input.hasTagCompound() && allowedSources.contains(input.getItem())) {
                NBTTagCompound inputTag = input.getTagCompound();
                NBTTagCompound copy = new NBTTagCompound();

                for (String tag : allowedTags) {
                    if (inputTag.hasKey(tag)) {
                        copy.setTag(tag, inputTag.getTag(tag));
                    }
                }

                if (!copy.hasNoTags()) {
                    result.setTagCompound(copy);
                }

                break; // chỉ lấy từ 1 nguồn
            }
        }

        return result;
    }
}

