package team11.backend.InformationSecurityProject.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserIn {

    @NotBlank(message = "Field (name) is required")
    @Size(max = 50, message = "Field (name) cannot be longer than 50 characters!")
    private String name;
    @NotBlank(message = "Field (surname) is required")
    @Size(max = 50, message = "Field (surname) cannot be longer than 50 characters!")
    private String surname;
    @NotBlank(message = "Field (telephoneNumber) is required")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s./0-9]{0,10}$",message = "Field (telephoneNumber) format is not valid")
    private String telephoneNumber;
    @NotBlank(message = "Field (email) is required")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",message = "Field (email) format is not valid!")
    private String email;
    @Size(min=6, max = 30, message = "Field (password) cannot be less than 6 characters and more than 30 characters!")
    private String password;


}
