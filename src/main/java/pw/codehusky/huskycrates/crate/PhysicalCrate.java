package pw.codehusky.huskycrates.crate;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import pw.codehusky.huskycrates.HuskyCrates;

import java.util.UUID;


public class PhysicalCrate {

    public static Vector3d offset = new Vector3d(0.5, 1, 0.5);
    public Location<World> location;
    public ArmorStand as = null;
    private VirtualCrate virtualCrate;

    public PhysicalCrate(Location<World> crateLocation, VirtualCrate virtualCrate) {
        this.location = crateLocation;
        this.virtualCrate = virtualCrate;
        createHologram();
    }

    public void createHologram() {
        for (Entity e : location.getExtent().getEntities()) {
            if (e instanceof ArmorStand && e.getLocation().copy().sub(offset).getPosition().equals(location.getPosition())) {
                ArmorStand ass = (ArmorStand) e;
                if (ass.getCreator().isPresent() && ass.getCreator().get().equals(UUID.fromString(HuskyCrates.instance.getArmorStandIdentifier()))) {
                    as = ass;
                    break;
                }
            }
        }

        if (as == null) {
            as = (ArmorStand) location.getExtent().createEntity(EntityTypes.ARMOR_STAND, location.getPosition());
            as.setLocation(location.copy().add(offset));
            location.getExtent().spawnEntity(as, HuskyCrates.instance.genericCause);
        }

        as.setCreator(UUID.fromString(HuskyCrates.instance.getArmorStandIdentifier()));
        as.offer(Keys.HAS_GRAVITY, false);
        as.offer(Keys.INVISIBLE, true);
        as.offer(Keys.ARMOR_STAND_MARKER, true);
        as.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        String name = virtualCrate.displayName;
        as.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(name));

    }

    public void runParticles() {
        try {
            double time = Sponge.getServer().getRunningTimeTicks() * 0.25;
            double size = 0.8;

            double x = Math.sin(time) * size;
            double y = Math.sin(time * 2) * 0.2 - 0.45;
            double z = Math.cos(time) * size;
            as.getWorld().spawnParticles(
                    ParticleEffect.builder()
                            .type(ParticleTypes.REDSTONE_DUST)
                            .option(ParticleOptions.COLOR, Color.ofRgb(virtualCrate.color1))
                            .build(),
                    as.getLocation()
                            .getPosition()
                            .add(x, y, z));

            x = Math.cos(time + 10) * size;
            y = Math.sin(time * 2 + 10) * 0.2 - 0.55;
            z = Math.sin(time + 10) * size;
            as.getWorld().spawnParticles(
                    ParticleEffect.builder()
                            .type(ParticleTypes.REDSTONE_DUST)
                            .option(ParticleOptions.COLOR, Color.ofRgb(virtualCrate.color2))
                            .build(),
                    as.getLocation()
                            .getPosition()
                            .add(x, y, z));
        } catch (Exception e) {}
    }
}
