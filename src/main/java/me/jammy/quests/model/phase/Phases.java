package me.jammy.quests.model.phase;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.model.attack.impl.*;
import me.jammy.quests.model.boss.Bosses;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;
import java.util.function.Supplier;

public enum Phases {
    KYOGAI_ONE(Bosses.KYOGAI, 1, Particle.CRIT, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0d, ImmutableList.of(Slash::new, Fling::new)),
    KYOGAI_TWO(Bosses.KYOGAI, 2, Particle.CRIT, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0d, ImmutableList.of(Slash::new, Fling::new)),
    RUI_ONE(Bosses.RUI, 1, Particle.EXPLOSION_NORMAL, Sound.ENTITY_SILVERFISH_HURT, 0.2d, ImmutableList.of(Slash::new, ShrinkingSphere::new)),
    RUI_TWO(Bosses.RUI, 2, Particle.EXPLOSION_NORMAL, Sound.ENTITY_SPIDER_AMBIENT, 0.4d, ImmutableList.of(Slash::new, Leap::new, Beam::new)),
    MUKAGO_ONE(Bosses.MUKAGO, 1, Particle.SLIME, Sound.ENTITY_ALLAY_HURT, 0.2d, ImmutableList.of(GroundStrike::new, Spikes::new)),
    MUKAGO_TWO(Bosses.MUKAGO, 2, Particle.SLIME, Sound.ENTITY_ALLAY_HURT, 0.5d, ImmutableList.of(GroundStrike::new, Spikes::new)),
    WAKURABO_ONE(Bosses.WAKURABO, 1, Particle.LANDING_HONEY, Sound.ENTITY_AXOLOTL_ATTACK, 0.6d, ImmutableList.of(Knock::new, Slash::new)),
    WAKURABO_TWO(Bosses.WAKURABO, 2, Particle.LANDING_HONEY, Sound.ENTITY_AXOLOTL_ATTACK, 0.8d, ImmutableList.of(Knock::new, Slash::new, Regen::new)),
    ROKURO_ONE(Bosses.ROKURO, 1, Particle.DRIPPING_OBSIDIAN_TEAR, Sound.ENTITY_PIGLIN_BRUTE_ANGRY, 0.1d, ImmutableList.of(Torture::new, Slash::new, GroundSpikes::new)),
    ROKURO_TWO(Bosses.ROKURO, 2, Particle.DRIPPING_OBSIDIAN_TEAR, Sound.ENTITY_PIGLIN_BRUTE_ANGRY, 0.15d, ImmutableList.of(Torture::new, Slash::new, Regen::new, GroundSpikes::new)),
    ENMU_ONE(Bosses.ENMU, 1, Particle.COMPOSTER, Sound.ENTITY_HUSK_HURT, 0.3d, ImmutableList.of(Dodge::new, Slash::new, Sleep::new)),
    ENMU_TWO(Bosses.ENMU, 2, Particle.COMPOSTER, Sound.ENTITY_HUSK_HURT, 0.3d, ImmutableList.of(Dodge::new, Slash::new, GroundSpikes::new, Slumber::new, Torture::new)),
    ENMU_THREE(Bosses.ENMU, 3, Particle.COMPOSTER, Sound.ENTITY_HUSK_HURT, 0.6d, ImmutableList.of(Dodge::new, Slash::new)),
    ENMU_FOUR(Bosses.ENMU, 4, Particle.COMPOSTER, Sound.ENTITY_HUSK_HURT, 0d, ImmutableList.of(Slumber::new, Torture::new, Sleep::new)),
    KAIGAKU_ONE(Bosses.KAIGAKU, 1, Particle.CRIT_MAGIC, Sound.ITEM_TRIDENT_THUNDER, 0.3d, ImmutableList.of(ThunderBreathing::new)),
    KAIGAKU_TWO(Bosses.KAIGAKU, 2, Particle.SMOKE_NORMAL, Sound.ITEM_TRIDENT_THUNDER, 0.5d, ImmutableList.of(ThunderBreathing::new, Electrokinesis::new)),
    GYUTARO_ONE(Bosses.GYUTARO, 1, Particle.DRAGON_BREATH, Sound.ENTITY_WARDEN_ANGRY, 0.5d, ImmutableList.of(Regen::new, Dodge::new, Leap::new, Slash::new, Beam::new, ShrinkingSphere::new)),
    GYUTARO_TWO(Bosses.GYUTARO, 2, Particle.DRAGON_BREATH, Sound.ENTITY_WARDEN_ANGRY, 0d, ImmutableList.of()),
    GYUTARO_THREE(Bosses.GYUTARO, 3, Particle.DRAGON_BREATH, Sound.ENTITY_WARDEN_ANGRY, 0.7d, ImmutableList.of(Regen::new, Dodge::new, Leap::new, Slash::new, Beam::new, ShrinkingSphere::new)),
    DAKI_ONE(Bosses.DAKI, 1, Particle.GLOW_SQUID_INK, Sound.ENTITY_WITCH_CELEBRATE, 0.5d, ImmutableList.of(Regen::new, Dodge::new, Leap::new, Slash::new)),
    DAKI_TWO(Bosses.DAKI, 2, Particle.GLOW_SQUID_INK, Sound.ENTITY_WITCH_CELEBRATE, 0.7d, ImmutableList.of(Regen::new, Dodge::new, Knock::new, Slash::new)),
    DAKI_THREE(Bosses.DAKI, 3, Particle.GLOW_SQUID_INK, Sound.ENTITY_WITCH_HURT, 0.2d, ImmutableList.of(Knock::new, Leap::new, Slash::new));

    @Getter private final Bosses bosses;
    @Getter private final int phaseNum;
    @Getter private final Particle particle;
    @Getter private final Sound sound;
    @Getter private final double movementSpeed;
    private final List<Supplier<Attack>> attacks;

    Phases(final Bosses bosses, final int phaseNum, final Particle particle, final Sound sound, final double movementSpeed, final List<Supplier<Attack>> attacks) {
        this.bosses = bosses;
        this.phaseNum = phaseNum;
        this.particle = particle;
        this.sound = sound;
        this.movementSpeed = movementSpeed;
        this.attacks = attacks;
    }

    public List<Attack> getAttacks() {
        return attacks.stream().map(Supplier::get).toList();
    }
}
