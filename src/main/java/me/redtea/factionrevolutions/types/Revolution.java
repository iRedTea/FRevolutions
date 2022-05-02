package me.redtea.factionrevolutions.types;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Revolution {
    private final String id;

    private String leader;

    private HashMap<String, Role> roles;

    private ArrayList<String> members;

    public Revolution(@NonNull String id, @NonNull String leader, @NonNull HashMap<String, Role> roles, @NonNull ArrayList<String> members) {
        this.id = id;
        this.leader = leader;
        this.roles = roles;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public HashMap<String, Role> getRoles() {
        return roles;
    }

    public void setRoles(HashMap<String, Role> roles) {
        this.roles = roles;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Revolution that = (Revolution) o;
        return id.equals(that.id) && leader.equals(that.leader) && roles.equals(that.roles) && members.equals(that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leader, roles, members);
    }

    @Override
    public String toString() {
        return "Revolution{" +
                "id='" + id + '\'' +
                ", leader='" + leader + '\'' +
                ", roles=" + roles +
                ", members=" + members +
                '}';
    }
}
