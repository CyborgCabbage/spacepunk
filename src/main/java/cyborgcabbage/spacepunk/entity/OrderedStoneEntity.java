package cyborgcabbage.spacepunk.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class OrderedStoneEntity extends PathAwareEntity {
    public OrderedStoneEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }
}
