package cyborgcabbage.spacepunk.util;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Optional;

public class RocketNavigation {
    //Universe Prime
    public static Universe UNIVERSE_PRIME = new Universe();
    public static System SOL = new System();
    public static Group GROUP_EARTH = new Group();
    public static Body EARTH;
    public static Body MOON;
    public static Group GROUP_VENUS = new Group();
    public static Body VENUS;

    //Universe Beta
    public static Universe UNIVERSE_BETA = new Universe();
    public static System SOL_BETA = new System();
    public static Group GROUP_EARTH_BETA = new Group();
    public static Body EARTH_BETA;
    public static Body MOON_BETA;

    public RocketNavigation(){
        addUniverse(UNIVERSE_PRIME);
        UNIVERSE_PRIME.addSystem(SOL);
        SOL.addGroup(GROUP_EARTH);
        EARTH = GROUP_EARTH.addBody(World.OVERWORLD);
        MOON = GROUP_EARTH.addBody(Spacepunk.MOON);
        SOL.addGroup(GROUP_VENUS);
        VENUS = GROUP_VENUS.addBody(Spacepunk.VENUS);

        addUniverse(UNIVERSE_BETA);
        UNIVERSE_BETA.addSystem(SOL_BETA);
        SOL_BETA.addGroup(GROUP_EARTH_BETA);
        EARTH_BETA = GROUP_EARTH_BETA.addBody(Spacepunk.BETA_OVERWORLD);
        MOON_BETA = GROUP_EARTH_BETA.addBody(Spacepunk.BETA_MOON);
    }

    private ArrayList<Universe> children = new ArrayList<>();
    public Universe addUniverse(Universe l){
        l.parent = this;
        children.add(l);
        return l;
    }
    public ArrayList<Universe> getChildren(){
        return children;
    }
    public Optional<Body> find(RegistryKey<World> world){
        for (Universe child : children) {
            Optional<Body> body = child.find(world);
            if(body.isPresent()) return body;
        }
        return Optional.empty();
    }
    public static class Universe{
        public RocketNavigation parent;
        private ArrayList<System> children = new ArrayList<>();
        public System addSystem(System l){
            l.parent = this;
            children.add(l);
            return l;
        }
        public ArrayList<System> getChildren(){
            return children;
        }
        public Optional<Body> find(RegistryKey<World> world){
            for (System child : children) {
                Optional<Body> body = child.find(world);
                if(body.isPresent()) return body;
            }
            return Optional.empty();
        }
    }
    public static class System{
        public Universe parent;
        private ArrayList<Group> children = new ArrayList<>();
        public Group addGroup(Group l){
            l.parent = this;
            children.add(l);
            return l;
        }
        public ArrayList<Group> getChildren(){
            return children;
        }
        public Optional<Body> find(RegistryKey<World> world){
            for (Group child : children) {
                Optional<Body> body = child.find(world);
                if(body.isPresent()) return body;
            }
            return Optional.empty();
        }
    }
    public static class Group{
        public System parent;
        private ArrayList<Body> children = new ArrayList<>();
        public Body addBody(Body l){
            l.parent = this;
            children.add(l);
            return l;
        }
        public Body addBody(RegistryKey<World> world){
            return addBody(new Body(world));
        }
        public ArrayList<Body> getChildren(){
            return children;
        }
        public Optional<Body> find(RegistryKey<World> world){
            for (Body child : children) {
                if(child.getWorld() == world){
                    return Optional.of(child);
                }
            }
            return Optional.empty();
        }
    }
    public static class Body{
        public Group parent;
        private RegistryKey<World> world;
        public Body(RegistryKey<World> _world){
            world = _world;
        }
        public RegistryKey<World> getWorld(){
            return world;
        }
    }
}
