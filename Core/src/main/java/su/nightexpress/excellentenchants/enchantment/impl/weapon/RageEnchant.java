package su.nightexpress.excellentenchants.enchantment.impl.weapon;

import org.bukkit.Particle;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentenchants.ExcellentEnchantsPlugin;
import su.nightexpress.excellentenchants.api.Modifier;
import su.nightexpress.excellentenchants.api.enchantment.Rarity;
import su.nightexpress.excellentenchants.api.enchantment.data.ChanceData;
import su.nightexpress.excellentenchants.api.enchantment.data.ChanceSettings;
import su.nightexpress.excellentenchants.api.enchantment.data.PotionData;
import su.nightexpress.excellentenchants.api.enchantment.data.PotionSettings;
import su.nightexpress.excellentenchants.api.enchantment.type.CombatEnchant;
import su.nightexpress.excellentenchants.enchantment.data.AbstractEnchantmentData;
import su.nightexpress.excellentenchants.enchantment.data.ChanceSettingsImpl;
import su.nightexpress.excellentenchants.enchantment.data.PotionSettingsImpl;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.io.File;

import static su.nightexpress.excellentenchants.Placeholders.*;

public class RageEnchant extends AbstractEnchantmentData implements ChanceData, PotionData, CombatEnchant {

    public static final String ID = "rage";

    private ChanceSettingsImpl chanceSettings;
    private PotionSettingsImpl potionSettings;

    public RageEnchant(@NotNull ExcellentEnchantsPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.setDescription(ENCHANTMENT_CHANCE + "% chance to get " + ENCHANTMENT_POTION_TYPE + " " + ENCHANTMENT_POTION_LEVEL + " (" + ENCHANTMENT_POTION_DURATION + "s.) on hit.");
        this.setMaxLevel(3);
        this.setRarity(Rarity.RARE);
    }

    @Override
    protected void loadAdditional(@NotNull FileConfig config) {
        this.chanceSettings = ChanceSettingsImpl.create(config, Modifier.add(4, 2, 1, 100));

        this.potionSettings = PotionSettingsImpl.create(this, config, PotionEffectType.INCREASE_DAMAGE, false,
            Modifier.add(3, 1, 1, 300),
            Modifier.add(0, 1, 1, 5)
        );
    }

    @NotNull
    @Override
    public ChanceSettings getChanceSettings() {
        return chanceSettings;
    }

    @NotNull
    @Override
    public PotionSettings getPotionSettings() {
        return potionSettings;
    }

    @NotNull
    @Override
    public EnchantmentTarget getCategory() {
        return EnchantmentTarget.WEAPON;
    }

    @NotNull
    @Override
    public EventPriority getAttackPriority() {
        return EventPriority.HIGHEST;
    }

    @Override
    public boolean onAttack(@NotNull EntityDamageByEntityEvent event, @NotNull LivingEntity damager, @NotNull LivingEntity victim, @NotNull ItemStack weapon, int level) {
        if (!this.checkTriggerChance(level)) return false;
        if (!this.addEffect(damager, level)) return false;

        if (this.hasVisualEffects()) {
            UniParticle.of(Particle.LAVA).play(damager.getEyeLocation(), 0.25, 0.1, 30);
        }
        return true;
    }

    @Override
    public boolean onProtect(@NotNull EntityDamageByEntityEvent event, @NotNull LivingEntity damager, @NotNull LivingEntity victim, @NotNull ItemStack weapon, int level) {
        return false;
    }
}
