package cyborgcabbage.spacepunk.util;

import com.google.gson.JsonObject;
import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BuildRocketCriterion extends AbstractCriterion<BuildRocketCriterion.Conditions> {
    static final Identifier ID = Spacepunk.id("build_rocket");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public BuildRocketCriterion.Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new BuildRocketCriterion.Conditions();
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, c -> true);
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions() {
            super(ID, EntityPredicate.Extended.EMPTY);
        }
    }
}
