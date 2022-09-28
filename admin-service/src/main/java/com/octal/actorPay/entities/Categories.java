/**
 *
 */
package com.octal.actorPay.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Naveen
 */
@Entity
@Table(name = "categories")
public class Categories extends AbstractPersistable {
    public Categories() {
    }

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "categories", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<SubCategories> subCategories = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<SubCategories> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<SubCategories> subCategories) {
        this.subCategories = subCategories;
    }
}