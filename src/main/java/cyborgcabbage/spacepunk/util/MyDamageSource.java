package cyborgcabbage.spacepunk.util;

import net.minecraft.entity.damage.DamageSource;

public class MyDamageSource extends DamageSource {
    public MyDamageSource(String name) {
        super(name);
    }

    @Override
    public DamageSource setBypassesArmor() {
        return super.setBypassesArmor();
    }
}
