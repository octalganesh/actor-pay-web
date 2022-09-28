/**
 *
 */
package com.octal.actorPay.entities;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

/**
 * @author Naveen
 */

//@Entity
//@Table(name = "role")
public class Role extends AbstractPersistable {

    public enum RoleName {
        ROLE_USER, ROLE_ADMIN, SUPER_ADMIN, ROLE_VENDOR
    }


//    @Enumerated(EnumType.STRING)
//    @NaturalId
//    @Column(name = "name", length = 30)
    private RoleName name;

//    @Column(name = "description", length = 255)
    private String description;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
//    private Set<Permissions> permissions = new HashSet<>();



    public Role(RoleName name) {
        this.name = name;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public Set<Permissions> getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(Set<Permissions> permissions) {
//        this.permissions = permissions;
//    }
}
