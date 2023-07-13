package su.nightexpress.excellentenchants.enchantment.impl.bow;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.particle.SimpleParticle;
import su.nightexpress.excellentenchants.ExcellentEnchants;
import su.nightexpress.excellentenchants.Placeholders;
import su.nightexpress.excellentenchants.api.enchantment.meta.Arrowed;
import su.nightexpress.excellentenchants.api.enchantment.meta.Chanced;
import su.nightexpress.excellentenchants.api.enchantment.type.BowEnchant;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.impl.meta.ArrowImplementation;
import su.nightexpress.excellentenchants.enchantment.impl.meta.ChanceImplementation;
import su.nightexpress.excellentenchants.enchantment.util.EnchantPriority;

public class FlareEnchant extends ExcellentEnchant implements Chanced, Arrowed, BowEnchant {

    public static final String ID = "flare";

    private ChanceImplementation chanceImplementation;
    private ArrowImplementation arrowImplementation;

    public FlareEnchant(@NotNull ExcellentEnchants plugin) {
        super(plugin, ID, EnchantPriority.LOWEST);

        this.getDefaults().setDescription(Placeholders.ENCHANTMENT_CHANCE + "% chance to create a torch where arrow lands.");
        this.getDefaults().setLevelMax(1);
        this.getDefaults().setTier(0.4);
    }

    @Override
    public void loadSettings() {
        super.loadSettings();
        this.chanceImplementation = ChanceImplementation.create(this, "100.0");
        this.arrowImplementation = ArrowImplementation.create(this, SimpleParticle.of(Particle.FIREWORKS_SPARK));
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.BOW;
    }

    @NotNull
    @Override
    public Chanced getChanceImplementation() {
        return this.chanceImplementation;
    }

    @NotNull
    @Override
    public ArrowImplementation getArrowImplementation() {
        return this.arrowImplementation;
    }

    @Override
    public boolean onShoot(@NotNull EntityShootBowEvent e, @NotNull LivingEntity shooter, @NotNull ItemStack bow, int level) {
        if (!(e.getProjectile() instanceof Arrow arrow)) return false;
        if (!this.isAvailableToUse(shooter)) return false;
        if (!this.checkTriggerChance(level)) return false;

        this.addData(arrow);
        return true;
    }

    @Override
    public boolean onHit(@NotNull ProjectileHitEvent e, @NotNull Projectile projectile, @NotNull ItemStack bow, int level) {
        Block block = e.getHitBlock();
        if (block == null) return false;

        BlockFace face = e.getHitBlockFace();
        if (face == null || face == BlockFace.DOWN) return false;

        Block relative = block.getRelative(face);
        if (!relative.getType().isAir()) return false;

        if (projectile.getShooter() instanceof Player player) {
            BlockCanBuildEvent event = new BlockCanBuildEvent(relative, player, Material.TORCH.createBlockData(), true);
            plugin.getPluginManager().callEvent(event);
            if (!event.isBuildable()) return false;
        }

        if (face == BlockFace.UP) {
            relative.setType(Material.TORCH);
        }
        else {
            relative.setType(Material.WALL_TORCH);

            Directional directional = (Directional) relative.getBlockData();
            directional.setFacing(face);
            relative.setBlockData(directional, true);
        }

        return true;
    }

    @Override
    public boolean onDamage(@NotNull EntityDamageByEntityEvent e, @NotNull Projectile projectile, @NotNull LivingEntity shooter, @NotNull LivingEntity victim, @NotNull ItemStack weapon, int level) {
        return false;
    }
}