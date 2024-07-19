package no.jonathan.quizapplication.user;

import jakarta.persistence.*;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import no.jonathan.quizapplication.shared.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends BaseEntity implements UserDetails, Principal {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
  @SequenceGenerator(name = "user_generator", sequenceName = "user_sec")
  private Long id;

  private String firstname;

  private String lastname;

  @Column(unique = true)
  private String email;

  private String password;

  private boolean accountLocked;

  private boolean enabled;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private UserRole userRole;

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !accountLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public String getName() {
    return email;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public boolean isAccountLocked() {
    return accountLocked;
  }

  public void setAccountLocked(boolean accountLocked) {
    this.accountLocked = accountLocked;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public String getFullName() {
    return firstname + " " + lastname;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return String.format("User{email=[%s]}", email);
  }

  public static class Builder {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;
    private UserRole userRole;

    private Builder() {
      // Initialize with default values if needed
      this.accountLocked = false;
      this.enabled = true;
      this.userRole = UserRole.USER;
    }

    public Builder firstname(String firstname) {
      this.firstname = firstname;
      return this;
    }

    public Builder lastname(String lastname) {
      this.lastname = lastname;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder accountLocked(boolean accountLocked) {
      this.accountLocked = accountLocked;
      return this;
    }

    public Builder enabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public Builder userRole(UserRole userRole) {
      this.userRole = userRole;
      return this;
    }

    public User build() {
      User user = new User();
      user.setFirstname(this.firstname);
      user.setLastname(this.lastname);
      user.setEmail(this.email);
      user.setPassword(this.password);
      user.setAccountLocked(this.accountLocked);
      user.setEnabled(this.enabled);
      user.setUserRole(this.userRole);
      return user;
    }
  }
}
