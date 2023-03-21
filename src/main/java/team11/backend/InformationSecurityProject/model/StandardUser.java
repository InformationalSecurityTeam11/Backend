package team11.backend.InformationSecurityProject.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@DiscriminatorValue(value = "STANDARD")
public class StandardUser extends User{
}
