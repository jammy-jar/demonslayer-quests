package me.jammy.quests.model.boss;

import lombok.Getter;
import lombok.Setter;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.miniboss.MiniBoss;
import me.jammy.quests.model.phase.Phase;
import me.jammy.quests.model.phase.Phases;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Msgs;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Boss {

    @Getter private LivingEntity entity;
    @Getter private final Bosses boss;
    @Getter private final String displayName;
    private final List<Phase> phases;
    @Getter private Phase currentPhase;
    @Getter private final User opponent;
    @Getter private final double attackDamage;
    @Getter private final Location location;
    @Getter @Setter private boolean running;
    @Getter private BossBar bossBar;

    private final NamespacedKey bossKey;

    public Boss(final Bosses boss, final String displayName, final Location location, final User opponent) {
        this.displayName = displayName;
        this.boss = boss;
        this.opponent = opponent;
        this.location = location;
        this.running = false;
        this.attackDamage = boss.getAttackDamage();

        // Get the Phases from the enum matching the boss.
        // Map the matching Phases to classes of the Phases
        this.phases = Arrays.stream(Phases.values()).filter(phase -> phase.getBosses() == boss)
                .map(phase -> new Phase(this, phase))
                .sorted(Comparator.comparingInt(Phase::getNum)).toList();
        this.currentPhase = phases.get(0);

        this.bossKey = new NamespacedKey(OasisQuests.instance(), "boss");
    }

    public void spawn(EntityType entityType) {
        LivingEntity entity;
        if (entityType == EntityType.PLAYER) {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, boss.toString());
            npc.data().setPersistent("opponent", opponent.getPlayer().getUniqueId().toString());
            npc.spawn(location, SpawnReason.CREATE);
        } else {
            entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
            setAttributes(entity);
        }
    }

    public void setAttributes(LivingEntity entity) {
        entity.getPersistentDataContainer().set(bossKey, PersistentDataType.STRING, boss.toString());

        if (bossBar != null)
            opponent.getPlayer().hideBossBar(bossBar);

        entity.customName(Msgs.of(boss.getDisplayName()).asComp());
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(phases.stream().mapToDouble(Phase::getHealth).sum());

        if (this.entity == null)
            entity.setHealth(phases.stream().mapToDouble(Phase::getHealth).sum());
        else {
            entity.setHealth(this.entity.getHealth());
            this.entity.remove();
        }

        AttributeInstance genericDmg = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (genericDmg != null)
            genericDmg.setBaseValue(attackDamage);
        if (entity instanceof Zombie zombie)
            zombie.setShouldBurnInDay(false);

        float healthPercentage = (float) (entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        this.bossBar = BossBar.bossBar(
                Msgs.of(boss.getDisplayName()).asComp(),
                healthPercentage,
                BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        opponent.getPlayer().showBossBar(bossBar);

        this.entity = entity;

        activate();
    }

    public void activate() {
        currentPhase.activate();
    }

    public void damage(double damage) {
        updateBossBar(damage);

        // Damage Boss and check if the next phase should be activated (if remove health is false).
        if (!checkPhaseChange(damage))
            return;

        // Increment to the next phase.
        nextPhase();
    }

    public void kill() {
        currentPhase.cancel();
        if (this instanceof MiniBoss)
            return;
        OasisQuests.instance().getFightHandler().end(this, true);
    }

    public void nextPhase() {
        currentPhase.cancel();
        if (currentPhase.getNum() == phases.size())
            return;
        currentPhase = phases.get(currentPhase.getNum());
        currentPhase.activate();
        Msgs.of("<dark_purple>Phase <yellow>" + currentPhase.getNum() + " <dark_purple>has begun!").send(opponent.getPlayer());
    }

    public void updateBossBar(double damage) {
        float healthPercentage = (float) ((entity.getHealth() - damage) / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        if (healthPercentage < 0)
            return;
        if (healthPercentage > 1)
            healthPercentage = 1;
        bossBar.progress(healthPercentage);
    }

    public boolean checkPhaseChange(double damage) {
        double requiredDamage = phases.subList(0, currentPhase.getNum()).stream().mapToDouble(Phase::getHealth).sum();
        double healthLost = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - (entity.getHealth() - damage);

        return healthLost > requiredDamage;
    }
}
