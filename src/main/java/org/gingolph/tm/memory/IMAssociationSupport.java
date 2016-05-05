package org.gingolph.tm.memory;

import java.util.HashSet;
import java.util.Set;
import org.gingolph.tm.AssociationSupport;
import org.gingolph.tm.TopicImpl;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public class IMAssociationSupport extends IMScopedSupport implements AssociationSupport {
    private Topic type;    
    private final Set<Role> roles = new HashSet<>();
    private Topic reifier;

    @Override
    public Topic getType() {
        return type;
    }

    @Override
    public void setType(Topic type) {
        this.type = type;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    @Override
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    @Override
    public Topic getReifier() {
        return reifier;
    }

    @Override
    public void setReifier(Topic reifier) {
        this.reifier = reifier;
    }

    
}
