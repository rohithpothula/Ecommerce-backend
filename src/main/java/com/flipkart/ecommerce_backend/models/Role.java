package com.flipkart.ecommerce_backend.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id; // Using Integer as IDs for roles are typically few

  @Enumerated(EnumType.STRING) // Store role name as string (e.g., "ROLE_USER")
  @Column(length = 20, unique = true, nullable = false)
  private ERole name; // Use the ERole enum

  // Optional: Add a mapping back to users if you need to easily find all users with a specific role
  // Be cautious with fetch type - LAZY is almost always preferred for collections
  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  private Set<LocalUser> users = new HashSet<>();

  /**
   * Constructor for creating a Role with a specific name.
   *
   * @param name The ERole enum constant representing the role name.
   */
  public Role(ERole name) {
    this.name = name;
  }

  // toString, equals, hashCode can be useful, especially if adding Roles to Sets
  // Consider implementing them based on 'name' or 'id' for uniqueness

  @Override
  public String toString() {
    return "Role{" + "id=" + id + ", name=" + name + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Role role = (Role) o;
    return name == role.name; // Compare based on the unique enum name
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0; // Hash based on the unique enum name
  }
}
