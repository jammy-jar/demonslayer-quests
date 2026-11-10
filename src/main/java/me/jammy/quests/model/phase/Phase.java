package me.jammy.quests.model.phase;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.model.boss.Boss;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.miniboss.MiniBoss;
import me.jammy.quests.model.miniboss.MiniBosses;
import me.jammy.quests.util.config.Msgs;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class Phase {
    private final Boss boss;
    private final int num;
    private final double health;
    private final double movementSpeed;
    private final long attackDelay;
    private final Particle attackParticle;
    private final Sound attackSound;
    private final double damage;
    private final List<Attack> attacks;
    private BukkitScheduler attackScheduler;
    private int task;

    public Phase(final Boss boss, final Phases phase) {
        this.boss = boss;
        this.num = phase.getPhaseNum();
        this.health = PhaseParser.initHealth(phase.getBosses(), phase.getPhaseNum());
        this.movementSpeed = phase.getMovementSpeed();
        this.attackDelay = PhaseParser.initAttackDelay(phase.getBosses(), phase.getPhaseNum());
        this.attacks = phase.getAttacks();
        this.attackScheduler = Bukkit.getScheduler();
        this.attackParticle = phase.getParticle();
        this.damage = PhaseParser.initDamage(phase.getBosses(), phase.getPhaseNum());
        this.attackSound = phase.getSound();
    }

    public void activate() {
        boss.getEntity().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        if (boss.getBoss() == Bosses.ENMU && num == 3) {
            Msgs.of("<dark_purple>Enmu is shape shifting!").send(boss.getOpponent().getPlayer());
            boss.spawn(EntityType.ENDERMAN);
        }
        if (boss.getBoss() == Bosses.ENMU && num == 4) {
            Msgs.of("<dark_purple>Enmu is Fusing with the Arena!").send(boss.getOpponent().getPlayer());
            Msgs.of("<dark_purple>Attack their heart at the centre!").send(boss.getOpponent().getPlayer());
            boss.spawn(EntityType.SHULKER);
        }
        if (boss.getBoss() == Bosses.GYUTARO && num == 2) {
            Msgs.of("<dark_purple>Gyutaro is now invulnerable, you must kill Daki!").send(boss.getOpponent().getPlayer());
            MiniBoss miniBoss = OasisQuests.instance().getBossSummoner().summon(boss, MiniBosses.DAKI);
            miniBoss.spawn(miniBoss.getBoss().getEntityType());
            boss.getEntity().setInvulnerable(true);
        }
        if (boss.getBoss() == Bosses.DAKI && num == 2) {
            Msgs.of("<dark_purple>Gyutaro is using Core Detachment. Daki is not more powerful!").send(boss.getOpponent().getPlayer());
            ((MiniBoss) boss).getParent().nextPhase();
        }
        if (boss.getBoss() == Bosses.DAKI && num == 3) {
            Msgs.of("<dark_purple>Daki is now weak and Gyutaro is vulnerable again, fight him!").send(boss.getOpponent().getPlayer());
            Boss parent = ((MiniBoss) boss).getParent();
            parent.nextPhase();
            parent.getEntity().setInvulnerable(false);
        }
        runAttacker();
    }

    public void cancel() {
        attackScheduler.cancelTask(task);
    }

    public int getNum() {
        return num;
    }

    public double getHealth() {
        return health;
    }

    public void action() {
        double totalProb = attacks.stream()
                .mapToDouble(Attack::getProbability)
                .reduce(0, Double::sum);

        if (boss.getEntity().isDead())
            return;
        Player opponent = boss.getOpponent().getPlayer();
        if (opponent.getLocation().distance(boss.getEntity().getLocation()) > 50)
            return;

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(boss.getEntity());
        if (boss.getEntity() instanceof Mob mob)
            mob.setTarget(opponent);
        else if (npc != null)
            if (npc.isSpawned())
                npc.getNavigator().setTarget(opponent, true);

        double random = Math.random() * totalProb;
        for (Attack attack : attacks) {
            random = random - attack.getProbability();
            if (random < 0) {
                attack.perform(damage, opponent, boss.getEntity(), attackParticle, attackSound);
                break;
            }
        }
    }

    private void runAttacker() {
        if (attackDelay <= 0)
            return;
        this.task = attackScheduler.scheduleSyncRepeatingTask(OasisQuests.instance(), this::action, 0, attackDelay);
    }
}
